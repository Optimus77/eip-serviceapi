package com.inspur.eip.service;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import com.inspur.eip.entity.Qos.*;
import com.inspur.eip.exception.EipInternalServerException;
import com.inspur.eip.util.common.IpUtil;
import com.inspur.eip.util.constant.ErrorStatus;
import com.inspur.eip.util.constant.HillStoneConfigConsts;
import com.inspur.eip.util.constant.HsConstants;
import com.inspur.eip.util.http.HsHttpClient;
import org.apache.commons.lang3.StringUtils;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class QosService {
    private String fwIp;
    private String fwPort;
    private String fwUser;
    private String fwPwd;

    @Autowired
    private FireWallCommondService fwCmdService;

    public QosService() {
    }

    QosService(String fwIp, String fwPort, String fwUser, String fwPwd) {
        this.fwIp = fwIp;
        this.fwPort = fwPort;
        this.fwUser = fwUser;
        this.fwPwd = fwPwd;
    }

    /**
     * 根据管道id/或者Sbw管道的name删除管道
     * @param pipeId
     * @return
     */
    boolean delQosPipe(String pipeId) {
        // 根据管道名称获取管道Id
        if (HsConstants.PIPE_ID_LENGTH.length() != pipeId.length()){
            pipeId = this.getQosPipeIdByName(pipeId);
        }
        String json = "[{\"target\":\"root\",\"node\":{\"name\":\"first\",\"root\":{\"id\":\"" + pipeId + "\"}}}]";
        boolean flag = false;
        try {
            String retr = HsHttpClient.hsHttpDelete(this.fwIp, this.fwPort, this.fwUser, this.fwPwd, "/rest/iQos", json);
            JSONObject jo = new JSONObject(retr);
            Iterator<String> keys = jo.keys();
            while (keys.hasNext()){
                String successKey = keys.next();
                if (HsConstants.SUCCESS.equalsIgnoreCase(successKey) && jo.getBoolean(successKey)){
                    flag =true;
                }else if (HsConstants.EXCEPTION.equalsIgnoreCase(successKey) && jo.getJSONObject(successKey).getString("message").contains("not exist")) {
                    // {"success":false, "result":[], "exception":{"code":"", "message":"Error: The root pipe dose not exist", "stack":""}}
                    log.warn("Error: The root pipe dose not exist,pipeId:{}",pipeId);
                    flag =true;
                }
            }
        } catch (Exception var7) {
            log.error(var7.getMessage());
        }
        return flag;
    }

    /*根据管道名称获取管道id*/
    String getQosPipeIdByName(String pipeName) throws EipInternalServerException {
        try {
            String id = "";
            String params = "/rest/iQos?query=%7B%22conditions%22%3A%5B%7B%22f%22%3A%22name%22%2C%22v%22%3A%22first%22%7D%5D%7D&target=root&node=root&id=%7B%22node%22%3A%22root%22%7D";
            String retr = HsHttpClient.hsHttpGet(this.fwIp, this.fwPort, this.fwUser, this.fwPwd, params);
            JSONObject jo = new JSONObject(retr);
            JSONArray array = jo.getJSONArray("children");
            for (int i = 0; i < array.length(); ++i) {
                JSONObject job = array.getJSONObject(i);
                if (pipeName.equals(job.optString("name"))) {
                    id = job.optString("id");
                    break;
                }
            }
            return id;
        } catch (Exception var11) {
            log.error(var11.getMessage());
            throw new EipInternalServerException(ErrorStatus.SC_FIREWALL_SERVER_ERROR.getCode(),ErrorStatus.SC_FIREWALL_SERVER_ERROR.getMessage());
        }
    }

    /**
     * remove
     *
     * @param floatIp 入参不能为空
     * @param pipeName 管道名称:UUID/floating ipAddress
     * @return ret
     */
    Boolean removeIpFromPipe(String floatIp, String pipeName) {
        String IP32 = IpUtil.ipToLong(floatIp);
        if (StringUtils.isBlank(IP32)) {
            return false;
        }
        Gson gson = new Gson();
        try {
            String pipeId = getQosPipeIdByName(pipeName);
            if (StringUtils.isBlank(pipeId)){
                log.error("can not find pipeId by qosName, sbwId:{}",pipeName);
                return false;
            }
            //query qos pipeId(eg:1563535650434929525) by pipeId
            JSONArray ipContent = getQosRuleByPipeId(pipeId);
            if (ipContent != null && ipContent.length() > 0) {
                ConcurrentHashMap<String, IpRange> map = new ConcurrentHashMap(2);
                for (int i = 0; i < ipContent.length(); i++) {
                    String json = ipContent.get(i).toString();
                    IpContent content = gson.fromJson(json, IpContent.class);
                    if (content != null) {
                        if ((content.getIpRange().getMin() != null && IP32.equals(content.getIpRange().getMin()))
                                || (content.getIpRange().getMax() != null && IP32.equals(content.getIpRange().getMax()))) {
                            map.put(content.getId(), content.getIpRange());
                        }
                    }
                }
                for (String id : map.keySet()) {
                    if (StringUtils.isBlank(id))
                        continue;
                    return deleteConditionFromPipe(pipeId, id);
                }
            }
        } catch (Exception e) {
            log.error("Exception in remove fip from SbwQos:{}" + e.getMessage());
        }
        return false;
    }
    /**
     * @param pipeId   管道id
     * @param sequence ip序号
     * @return ret
     */
    public Boolean deleteConditionFromPipe(String pipeId, String sequence) {
        String param = "[{\"target\":\"root.rule\",\"node\":{\"name\":\"first\",\"root\":{\"id\":\"" + pipeId + "\",\"rule\":[{\"id\":\"" + sequence + "\"}]}}}]";
        try {
            String retr = HsHttpClient.hsHttpDelete(this.fwIp, this.fwPort, this.fwUser, this.fwPwd, "/rest/iQos?target=root.rule", param);
            JSONObject jo = new JSONObject(retr);
            if (jo.has(HsConstants.SUCCESS)){
                if (jo.getBoolean(HsConstants.SUCCESS)){
                    log.info("rest delete ip result{}", retr);
                    return true;
                }else if (jo.has(HsConstants.EXCEPTION) && jo.getJSONObject(HsConstants.EXCEPTION).getString("message").contains("condition does not exists")){
                    log.info("rest delete ip warn, id condition dose not exists :{}",sequence);
                    return true;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * get rule sequence by pipeId
     * @param pipeId
     * @return
     */
    private JSONArray getQosRuleByPipeId(String pipeId) {
        String params = "/rest/iQos?query=%7B%22conditions%22%3A%5B%7B%22f%22%3A%22name%22%2C%22v%22%3A%22first%22%7D%2C%7B%22f%22%3A%22root.id%22%2C%22v%22%3A%22" + pipeId + "%22%7D%5D%7D&target=root.rule";
        try {
            String retr = HsHttpClient.hsHttpGet(this.fwIp, this.fwPort, this.fwUser, this.fwPwd, params);
//            String retr = HsHttpClient.hsHttpGet("10.110.29.206", "443", "hillstone", "hillstone", params);
            if (retr != null) {
                return new JSONArray(retr);
            }
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
        return null;
    }
    /**
     * Enable or disable pipes in qos
     * @param fireWallId
     * @param pipeName
     * @param action     //true :disable  false:no disable
     * @return
     */
    boolean controlPipe(String fireWallId, String pipeName, Boolean action){
        String cmd =null;
        if (action){
            cmd = disablePipe(pipeName);
        }else {
            cmd = noDisablePipe(pipeName);
        }
        if (fwCmdService.execCustomCommand(fireWallId, cmd, null) ==null){
            log.debug("Control pipeline successful ,action:{}",cmd);
            return true;
        }
        //管道不存在 | 禁用或者启用管道失败
        return false;
    }

    private String disablePipe(String pipeName){
        String disableCmd = HillStoneConfigConsts.CONFIGURE_MODEL_ENTER + HillStoneConfigConsts.QOS_ENGINE_FIRST_ENTER+
                HillStoneConfigConsts.ROOT_PIPE_SPACE +pipeName + HillStoneConfigConsts.SSH_ENTER +HillStoneConfigConsts.DISABLE
                +HillStoneConfigConsts.ENTER_END;
        return disableCmd;
    }
    private String noDisablePipe(String pipeName){
        String noDisableCmd = HillStoneConfigConsts.CONFIGURE_MODEL_ENTER +HillStoneConfigConsts.QOS_ENGINE_FIRST_ENTER+
                HillStoneConfigConsts.ROOT_PIPE_SPACE +pipeName + HillStoneConfigConsts.SSH_ENTER +HillStoneConfigConsts.NO_DISABLE
                +HillStoneConfigConsts.ENTER_END;
        return noDisableCmd;
    }

    //Customize the Strig adapter
    private static final TypeAdapter STRING = new TypeAdapter() {
        @Override
        public void write(JsonWriter out, Object value) throws IOException {
            if (value == null) {
                // 在这里处理null改为空字符串
                out.value("");
                return;
            }
            out.value((String) value);
        }

        public String read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return "";
            }
            return reader.nextString();
        }
    };

    void setFwIp(String fwIp) {
        this.fwIp = fwIp;
    }

    void setFwPort(String fwPort) {
        this.fwPort = fwPort;
    }

    void setFwUser(String fwUser) {
        this.fwUser = fwUser;
    }

    void setFwPwd(String fwPwd) {
        this.fwPwd = fwPwd;
    }

    public static void main(String[] args) {
         QosService qosService = new QosService("10.110.29.206","443","hillstone","hillstone");
        Boolean aBoolean = qosService.removeIpFromPipe("192.168.11.34", "5b664f1a-ba29-4026-9422-5b7480907dbe");
        System.out.println(aBoolean);
    }

}
