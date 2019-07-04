package com.inspur.eip.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.*;

import com.inspur.eip.entity.Qos.*;
import com.inspur.eip.exception.EipInternalServerException;
import com.inspur.eip.util.common.IpUtil;
import com.inspur.eip.util.constant.ErrorStatus;
import com.inspur.eip.util.constant.HillStoneConfigConsts;
import com.inspur.eip.util.constant.HsConstants;
import com.inspur.eip.util.http.HsHttpClient;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
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

    HashMap<String, String> createQosPipe(HashMap<String, String> info) {
        HashMap res = new HashMap();

        try {
            String retr = HsHttpClient.hsHttpPost(this.fwIp, this.fwPort, this.fwUser, this.fwPwd, "/rest/iQos?target=root", this.getCreatePipeJson(info));
            JSONObject jo = new JSONObject(retr);
            boolean success = jo.getBoolean(HsConstants.SUCCESS);
            res.put(HsConstants.SUCCESS, success);
            if (success) {
                String qosId = this.getQosPipeId(info.get(HsConstants.PIPE_NAME));
                if (StringUtils.isNotBlank(qosId)) {
                    res.put("id", qosId);
                } else {
                    res.put("msg", "Create success,but id not found,please call find api by pip name.");
                }
            } else {
                log.info("add qos failed, result:{}", jo);
                res.put("msg", jo.getJSONObject(HsConstants.EXCEPTION));
            }
            return res;
        } catch (Exception var7) {
            log.error(var7.getMessage());
            res.put(HsConstants.SUCCESS, HsConstants.FALSE);
            res.put("msg", var7.getMessage());
            return res;
        }
    }

    HashMap<String, String> delQosPipe(String pipeId) {
        HashMap<String, String> res = new HashMap();
        String json = "[{\"target\":\"root\",\"node\":{\"name\":\"first\",\"root\":{\"id\":\"" + pipeId + "\"}}}]";

        try {
            String retr = HsHttpClient.hsHttpDelete(this.fwIp, this.fwPort, this.fwUser, this.fwPwd, "/rest/iQos", json);
            JSONObject jo = new JSONObject(retr);
            boolean success = jo.getBoolean(HsConstants.SUCCESS);
            if (success) {
                res.put(HsConstants.SUCCESS, "true");
            } else if ("Error: The root pipe dose not exist".equals(jo.getJSONObject(HsConstants.EXCEPTION).getString("message"))) {
                res.put(HsConstants.SUCCESS, "true");
                res.put("msg", "pip not found.");
            } else {
                res.put(HsConstants.SUCCESS, HsConstants.FALSE);
                res.put("msg", jo.getString(HsConstants.EXCEPTION));
            }
            return res;
        } catch (Exception var7) {
            log.error(var7.getMessage());
            res.put(HsConstants.SUCCESS, HsConstants.FALSE);
            res.put("msg", var7.getMessage());
            return res;
        }
    }

    HashMap<String, String> updateQosPipe(String pipeId, String pipeName, String bandWidth) {
        HashMap res = new HashMap();
        try {
            String body = this.getUpdateJson(pipeId, pipeName, bandWidth);
            String retr = HsHttpClient.hsHttpPut(this.fwIp, this.fwPort, this.fwUser, this.fwPwd, "/rest/iQos?target=root", body);
            JSONObject jo = new JSONObject(retr);
            log.info("Update: pipeId:{}, pipeName:{}, bandwidht:{}", pipeId, pipeName, bandWidth);
            log.info("updateQosPipe jo {}", jo);
            boolean success = jo.getBoolean(HsConstants.SUCCESS);
            res.put(HsConstants.SUCCESS, success);
            if (jo.getBoolean(HsConstants.SUCCESS)) {
                res.put("msg", jo.get(HsConstants.EXCEPTION));
            }
            return res;
        } catch (Exception var8) {
            log.error(var8.getMessage());
            res.put(HsConstants.SUCCESS, HsConstants.FALSE);
            res.put("msg", var8.getMessage());
            return res;
        }
    }


    private String getCreatePipeJson(HashMap<String, String> map) {
        try {
            String s;
            if (map.containsKey("ip")) {
                s = "{\"name\": \"first\",\"root\": {\"name\":\"" + map.get("pipeName") + "\",\"desc\": \"\",\"qos_mode\": {\"name\": \"shape\"},\"rule\": [{ \"id\": [],\"src_addr\": [{\"name\": \"any\"}]," + "\"src_host\": [],\"src_subnet\": [],\"src_range\": [],\"dst_addr\": [],\"dst_host\": [],\"dst_subnet\": [{\"ip\":" + IpUtil.ipToLong(map.get("ip")) + ",\"netmask\":32" + "}],\"dst_range\": [],\"user\": [],\"usergroup\": [],\"service\": [{\"name\": \"" + map.get("serviceNamne") + "\"}],\"application\": [],\"src_zone\": [],\"ingress_if\": [],\"dst_zone\": [],\"egress_if\": []" + ",\"vlan\": [],\"tos\": []}],\"action\": [{\"dir\": \"1\",\"min\": \"" + map.get(HsConstants.IN_BAND_WIDTH) + "\", \"max\":\"" + map.get(HsConstants.IN_BAND_WIDTH) + "\",\"per_min\": \"\",\"per_max\": \"\",\"per_using\": \"\",\"priority\": 7,\"set_tos\": \"2\",\"tos\": \"\",\"amask\": {" + "\"action_dir\": true,\"action_bandwidth\": false,\"action_reserve_bandwidth\": false,\"action_min\": false,\"action_max\": false," + "\"action_per_ip_min\": false,\"action_per_ip_max\": false,\"action_per_user_min\": false,\"action_per_user_max\": false," + "\"action_per_ip_using\": false,\"action_average_using\": false,\"action_tos_mark\": false,\"action_tos_int\": true," + "\"action_tos_str\": false,\"action_priority\": true,\"action_bandwidth_mbps\": true,\"action_reserve_bandwidth_mbps\": false," + "\"action_min_mbps\": false,\"action_max_mbps\": false,\"action_per_ip_min_mbps\": false,\"action_per_ip_max_mbps\": false," + "\"action_per_user_min_mbps\": false,\"action_per_user_max_mbps\": false,\"action_reserve_bandwidth_percent\": false,\"action_min_percent\": false," + "\"action_max_percent\": false,\"action_bandwidth_gbps\": false,\"action_rserve_bandwidth_gbps\": false,\"action_min_gbps\": false," + "\"action_max_gbps\": false,\"action_mode\": false}},{\"dir\": \"2\",\"min\": \"" + map.get(HsConstants.BAND_WIDTH) + "\", \"max\":\"" + map.get(HsConstants.BAND_WIDTH) + "\",\"per_min\": \"\",\"per_max\": \"\",\"per_using\": \"\",\"priority\": 7,\"set_tos\": \"2\",\"tos\": \"\",\"amask\": {" + "\"action_dir\": true,\"action_bandwidth\": false,\"action_reserve_bandwidth\": false,\"action_min\": false,\"action_max\": false," + "\"action_per_ip_min\": false,\"action_per_ip_max\": false,\"action_per_user_min\": false,\"action_per_user_max\": false,\"action_per_ip_using\": false," + "\"action_average_using\": false,\"action_tos_mark\": false,\"action_tos_int\": true,\"action_tos_str\": false,\"action_priority\": true," + "\"action_bandwidth_mbps\": true,\"action_reserve_bandwidth_mbps\": false,\"action_min_mbps\": false,\"action_max_mbps\": false,\"action_per_ip_min_mbps\": false," + "\"action_per_ip_max_mbps\": false,\"action_per_user_min_mbps\": false,\"action_per_user_max_mbps\": false,\"action_reserve_bandwidth_percent\": false," + "\"action_min_percent\": false,\"action_max_percent\": false,\"action_bandwidth_gbps\": false,\"action_rserve_bandwidth_gbps\": false," + "\"action_min_gbps\": false,\"action_max_gbps\": false,\"action_mode\": false}}],\"id\": 0}}";
            } else {
                s = "{\"name\": \"first\",\"root\":{\"name\":\"" + map.get("pipeName") + "\",\"desc\":\"\",\"qos_mode\":{\"name\":\"shape\"},\"action\":[{\"dir\":\"1\",\"min\":\"" + map.get(HsConstants.IN_BAND_WIDTH) + "\",\"max\":\"" + map.get(HsConstants.IN_BAND_WIDTH) + "\",\"per_min\":\"\",\"per_max\":\"\",\"per_using\":\"\",\"priority\":7,\"set_tos\":\"2\",\"tos\":\"\",\"amask\":{\"action_dir\":true,\"action_bandwidth\":false,\"action_reserve_bandwidth\":false,\"action_min\":false,\"action_max\":false,\"action_per_ip_min\":false,\"action_per_ip_max\":false,\"action_per_user_min\":false,\"action_per_user_max\":false,\"action_per_ip_using\":false,\"action_average_using\":false,\"action_tos_mark\":false,\"action_tos_int\":true,\"action_tos_str\":false,\"action_priority\":true,\"action_bandwidth_mbps\":true,\"action_reserve_bandwidth_mbps\":false,\"action_min_mbps\":false,\"action_max_mbps\":false,\"action_per_ip_min_mbps\":false,\"action_per_ip_max_mbps\":false,\"action_per_user_min_mbps\":false,\"action_per_user_max_mbps\":false,\"action_reserve_bandwidth_percent\":false,\"action_min_percent\":false,\"action_max_percent\":false,\"action_bandwidth_gbps\":false,\"action_rserve_bandwidth_gbps\":false,\"action_min_gbps\":false,\"action_max_gbps\":false,\"action_mode\":false}},{\"dir\":\"2\",\"min\":\"" + map.get(HsConstants.BAND_WIDTH) + "\",\"max\":\"" + map.get(HsConstants.BAND_WIDTH) + "\",\"per_min\":\"\",\"per_max\":\"\",\"per_using\":\"\",\"priority\":7,\"set_tos\":\"2\",\"tos\":\"\",\"amask\":{\"action_dir\":true,\"action_bandwidth\":false,\"action_reserve_bandwidth\":false,\"action_min\":false,\"action_max\":false,\"action_per_ip_min\":false,\"action_per_ip_max\":false,\"action_per_user_min\":false,\"action_per_user_max\":false,\"action_per_ip_using\":false,\"action_average_using\":false,\"action_tos_mark\":false,\"action_tos_int\":true,\"action_tos_str\":false,\"action_priority\":true,\"action_bandwidth_mbps\":true,\"action_reserve_bandwidth_mbps\":false,\"action_min_mbps\":false,\"action_max_mbps\":false,\"action_per_ip_min_mbps\":false,\"action_per_ip_max_mbps\":false,\"action_per_user_min_mbps\":false,\"action_per_user_max_mbps\":false,\"action_reserve_bandwidth_percent\":false,\"action_min_percent\":false,\"action_max_percent\":false,\"action_bandwidth_gbps\":false,\"action_rserve_bandwidth_gbps\":false,\"action_min_gbps\":false,\"action_max_gbps\":false,\"action_mode\":false}}],\"id\":0}}";
            }

            return s;
        } catch (Exception var4) {
            log.error("get create pip error.");
            throw var4;
        }
    }

    private String getUpdateJson(String pipeId, String pipeName, String bandWidth) {
        String errorstr = "{\"name\":\"first\",\"root\":{\"id\":\"" + pipeId + "\",\"name\":\"" + pipeName + "\",\"desc\":\"\",\"qos_mode\":{\"name\":\"shape\"},\"action\":[{\"dir\":\"1\",\"min\":\"" + bandWidth + "\",\"max\":\"" + bandWidth + "\",\"per_min\":\"\",\"per_max\":\"\",\"per_using\":\"\",\"priority\":7,\"set_tos\":\"2\",\"tos\":\"\",\"amask\":{\"action_dir\":true,\"action_bandwidth\":false,\"action_reserve_bandwidth\":false,\"action_min\":false,\"action_max\":false,\"action_per_ip_min\":false,\"action_per_ip_max\":false,\"action_per_user_min\":false,\"action_per_user_max\":false,\"action_per_ip_using\":false,\"action_average_using\":false,\"action_tos_mark\":false,\"action_tos_int\":true,\"action_tos_str\":false,\"action_priority\":true,\"action_bandwidth_mbps\":true,\"action_reserve_bandwidth_mbps\":false,\"action_min_mbps\":false,\"action_max_mbps\":false,\"action_per_ip_min_mbps\":false,\"action_per_ip_max_mbps\":false,\"action_per_user_min_mbps\":false,\"action_per_user_max_mbps\":false,\"action_reserve_bandwidth_percent\":false,\"action_min_percent\":false,\"action_max_percent\":false,\"action_bandwidth_gbps\":false,\"action_rserve_bandwidth_gbps\":false,\"action_min_gbps\":false,\"action_max_gbps\":false,\"action_mode\":false}},{\"dir\":\"2\",\"min\":\"" + bandWidth + "\",\"max\":\"" + bandWidth + "\",\"per_min\":\"\",\"per_max\":\"\",\"per_using\":\"\",\"priority\":7,\"set_tos\":\"2\",\"tos\":\"\",\"amask\":{\"action_dir\":true,\"action_bandwidth\":false,\"action_reserve_bandwidth\":false,\"action_min\":false,\"action_max\":false,\"action_per_ip_min\":false,\"action_per_ip_max\":false,\"action_per_user_min\":false,\"action_per_user_max\":false,\"action_per_ip_using\":false,\"action_average_using\":false,\"action_tos_mark\":false,\"action_tos_int\":true,\"action_tos_str\":false,\"action_priority\":true,\"action_bandwidth_mbps\":true,\"action_reserve_bandwidth_mbps\":false,\"action_min_mbps\":false,\"action_max_mbps\":false,\"action_per_ip_min_mbps\":false,\"action_per_ip_max_mbps\":false,\"action_per_user_min_mbps\":false,\"action_per_user_max_mbps\":false,\"action_reserve_bandwidth_percent\":false,\"action_min_percent\":false,\"action_max_percent\":false,\"action_bandwidth_gbps\":false,\"action_rserve_bandwidth_gbps\":false,\"action_min_gbps\":false,\"action_max_gbps\":false,\"action_mode\":false}}],\"schedule\":[]}}";
        log.debug(errorstr);
        return errorstr;
    }
    /*根据管道名称获取管道id*/
    String getQosPipeId(String pipeName) throws EipInternalServerException {
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
     * @param floatIp fip
     * @param sbwId 管道名称
     * @return ret
     */
    HashMap<String, String> removeIpFromPipe(String floatIp, String sbwId) {
        HashMap<String, String> res = new HashMap();
        if (StringUtils.isBlank(floatIp)) {
            res.put(HsConstants.SUCCESS, HsConstants.FALSE);
            return res;
        }
        String IP32 = IpUtil.ipToLong(floatIp);
        if (StringUtils.isBlank(IP32)) {
            res.put(HsConstants.SUCCESS, HsConstants.FALSE);
            return res;
        }
        Gson gson = new Gson();
        try {
            String pipeId = getQosPipeId(sbwId);
            if (StringUtils.isBlank(pipeId)){
                res.put(HsConstants.SUCCESS, HsConstants.FALSE);
                return res;
            }
            //query qos pipe details by pipeId
            JSONArray ipContent = getQosRuleByPipeId(pipeId);
            if (ipContent != null && ipContent.length() > 0) {
                ConcurrentHashMap<String, IpRange> map = new ConcurrentHashMap(2);
                for (int i = 0; i < ipContent.length(); i++) {
                    String json = ipContent.get(i).toString();
                    IpContent content = gson.fromJson(json, IpContent.class);
                    if (content != null) {
                        if ((content.getIpRange().getMin() != null && IP32.equals(content.getIpRange().getMin())) || (content.getIpRange().getMax() != null && IP32.equals(content.getIpRange().getMax()))) {
                            map.put(content.getId(), content.getIpRange());
                        }
                    }
                }
                for (String id : map.keySet()) {
                    Boolean result = deleteIpFromPipe(pipeId, id);
                    if (result){
                        log.info("Rest remove floating ip success :{}", result);
                        res.put(HsConstants.SUCCESS, HsConstants.TRUE);
                    }else {
                        res.put(HsConstants.SUCCESS, HsConstants.FALSE);
                        log.warn("Rest remove floating ip success :{}", result);
                    }
                }
            }else {
                res.put(HsConstants.SUCCESS, HsConstants.FALSE);
            }
        } catch (Exception e) {
            log.error("Exception in remove fip from SbwQos:{}" + e.getMessage());
        }
        return res;
    }
    /**
     * @param pipeId   管道id
     * @param sequence ip序号
     * @return ret
     */
    private Boolean deleteIpFromPipe(String pipeId, String sequence) {
        String param = "[{\"target\":\"root.rule\",\"node\":{\"name\":\"first\",\"root\":{\"id\":\"" + pipeId + "\",\"rule\":[{\"id\":\"" + sequence + "\"}]}}}]";
        try {
            String retr = HsHttpClient.hsHttpDelete(this.fwIp, this.fwPort, this.fwUser, this.fwPwd, "/rest/iQos?target=root.rule", param);
            JSONObject jo = new JSONObject(retr);
            log.info("hsHttpDelete  result{}", retr);
            boolean success = jo.getBoolean(HsConstants.SUCCESS);
            if (success) {
                return true;
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
            log.info("Control pipeline successful ,action:{}",cmd);
            return true;
        }
        //管道不存在 | 禁用或者启用管道失败
        return false;
    }

    private String disablePipe(String pipeName){
        String disableCmd = HillStoneConfigConsts.CONFIGURE_MODEL_ENTER + HillStoneConfigConsts.QOS_ENGINE_FIRST_ENTER+ HillStoneConfigConsts.ROOT_PIPE_SPACE
                +pipeName + HillStoneConfigConsts.SSH_ENTER +HillStoneConfigConsts.DISABLE +HillStoneConfigConsts.ENTER_END;
        return disableCmd;
    }
    private String noDisablePipe(String pipeName){
        String noDisableCmd = HillStoneConfigConsts.CONFIGURE_MODEL_ENTER +HillStoneConfigConsts.QOS_ENGINE_FIRST_ENTER+ HillStoneConfigConsts.ROOT_PIPE_SPACE
                +pipeName + HillStoneConfigConsts.SSH_ENTER +HillStoneConfigConsts.NO_DISABLE +HillStoneConfigConsts.ENTER_END;
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


}
