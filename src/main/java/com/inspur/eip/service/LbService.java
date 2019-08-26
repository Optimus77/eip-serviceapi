package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.MethodReturn;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.exception.EipInternalServerException;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.repository.FirewallRepository;
import com.inspur.eip.repository.SbwRepository;
import com.inspur.eip.util.common.MethodReturnUtil;
import com.inspur.eip.util.constant.HsConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

@Slf4j
@Service
@ConditionalOnProperty(value = "firewall.type",havingValue = "radware")
public class LbService implements IDevProvider{

    @Autowired
    private FirewallRepository firewallRepository;

    @Autowired
    private QosService qosService;

    @Autowired
    private SbwRepository sbwRepository;

    @Autowired
    private EipRepository eipRepository;

    @Autowired
    private FireWallCommondService fireWallCommondService;



    @Override
    public String addQos(String innerip, String name, String bandwidth, String fireWallId) {
        String pipid;

        return "noqos";
    }

    /**
     * update the Qos bindWidth
     *
     * @param firewallId firewall id
     * @param bindwidth  bind width
     */
    @Override
    public boolean updateQosBandWidth(String firewallId, String pipId, String pipNmae, String bindwidth, String fip, String eip) {


        return Boolean.parseBoolean("True");
    }


    /**
     * del qos
     *
     * @param pipid pipid
     * @param devId devid
     * @return ret
     */
    @Override
    public boolean delQos(String pipid, String eip, String fip, String devId) {

        return true;
    }


    @Override
    public MethodReturn addNatAndQos(Eip eip, String fipAddress, String eipAddress, int bandWidth, String firewallId) {

        return MethodReturnUtil.success();
    }

    @Override
    public MethodReturn delNatAndQos(Eip eipEntity) {

        return MethodReturnUtil.success();
    }

    /**
     * add the Qos bindind ip
     *
     * @param firewallId id
     * @return ret
     */
    @Override
    public String addFipToSbwQos(String firewallId, String floatIp, String sbwId) {
        String retPipeId = null;
        if (sbwId.length() == HsConstants.UUID_LENGTH.length()) {
            retPipeId = cmdAddIp2SbwPipe(sbwId, floatIp, firewallId);
            if (null != retPipeId) {
                return retPipeId;
            }
        }
        return retPipeId;
    }

    /**
     * remove eip from shared band
     *
     * @param firewallId id
     * @param floatIp    fip
     * @return ret
     */
    @Override
    public boolean removeFipFromSbwQos(String firewallId, String floatIp, String sbwId) {

        return true;
    }

    @Override
    public boolean ping(String ipAddress, String fireWallId) {
        try {
            int  timeOut =  3000 ;
            return InetAddress.getByName(ipAddress).isReachable(timeOut);
        } catch (Exception e) {
            return false;
        }
    }


    private String   getRootPipeName(String fip) {
        String[] ipSplit = fip.split("\\.");
        return ipSplit[0] + "." + ipSplit[1] + "." + ipSplit[2] + ".0";
    }


    @Override
    public synchronized boolean cmdAddSbwQos(String name, String bandwidth, String fireWallId) throws EipInternalServerException {
        Boolean flag = Boolean.TRUE;

        return true;
    }

    @Override
    public synchronized boolean cmdDelSbwQos(String name, String fireWallId) {

        return true;
    }

    private String cmdAddIp2SbwPipe(String sbwId, String fip, String fireWallId) {


        return sbwId;
    }

    @Override
    public JSONObject cmdShowStatisticsByAddressBook(String entryName, String period, String fireWallId){
        return null;
    }


}
