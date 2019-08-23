package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.config.CodeInfo;
import com.inspur.eip.entity.MethodReturn;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.sbw.Sbw;
import com.inspur.eip.exception.EipInternalServerException;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.repository.FirewallRepository;
import com.inspur.eip.repository.SbwRepository;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.common.MethodReturnUtil;
import com.inspur.eip.util.constant.HsConstants;
import com.inspur.eip.util.constant.ReturnStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.Optional;

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
    public String addDnat(String innerip, String extip, String equipid) {
        String ruleid = cmdAddDnat(innerip, extip, equipid);
        if (ruleid != null) {
            return ruleid;
        }

        return ruleid;
    }
    @Override
    public String addSnat(String innerip, String extip, String equipid) {

        String ruleid = cmdAddSnat(innerip, extip, equipid);
        if (ruleid != null) {
            return ruleid;
        }

        return ruleid;
    }

    @Override
    public String addQos(String innerip, String name, String bandwidth, String fireWallId) {
        String pipid;
        String inBandWidth = "50";
        if (Integer.valueOf(bandwidth) > 50) {
            inBandWidth = bandwidth;
        }
        pipid = cmdAddQos(name, innerip, inBandWidth, bandwidth, fireWallId);
        if (null != pipid) {
            return pipid;
        }

        return pipid;
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
    public boolean delDnat(String ruleid, String devId) {
        boolean bSuccess = true;
        if (cmdDelDnat(ruleid, devId)) {
            return true;
        }

        return bSuccess;
    }
    @Override
    public boolean delSnat(String ruleid, String devId) {
        boolean bSuccess = true;
        if (cmdDelSnat(ruleid, devId)) {
            return true;
        }

        return bSuccess;
    }
    @Override
    public MethodReturn addNatAndQos(Eip eip, String fipAddress, String eipAddress, int bandWidth, String firewallId) {
        String pipId = null;
        String dnatRuleId = null;
        String snatRuleId = null;
        String returnStat;
        String returnMsg;
        try {
            dnatRuleId = addDnat(fipAddress, eipAddress, firewallId);
            if (dnatRuleId != null) {
                snatRuleId = addSnat(fipAddress, eipAddress, firewallId);
                if (snatRuleId != null) {
                    if (eip.getChargeMode().equalsIgnoreCase(HsConstants.SHAREDBANDWIDTH)) {
                        Optional<Sbw> optional = sbwRepository.findById(eip.getSbwId());
                        if (optional.isPresent()) {
                            pipId = addFipToSbwQos(eip.getFirewallId(), fipAddress, optional.get().getId());
                        }
                    } else {
                        pipId = addQos(fipAddress, eipAddress, String.valueOf(bandWidth), firewallId);
                    }
                    if (null != pipId || CommonUtil.qosDebug) {
                        eip.setDnatId(dnatRuleId);
                        eip.setSnatId(snatRuleId);
                        eip.setPipId(pipId);
                        log.info("add nat and qos successfully. snat:{}, dnat:{}, qos:{}",
                                eip.getSnatId(), eip.getDnatId(), eip.getPipId());

                        return MethodReturnUtil.success(eip);
                    } else {
                        returnStat = ReturnStatus.SC_FIREWALL_QOS_UNAVAILABLE;
                        returnMsg = CodeInfo.getCodeMessage(CodeInfo.EIP_BIND_FIREWALL_QOS_ERROR);
                    }
                } else {
                    returnStat = ReturnStatus.SC_FIREWALL_SNAT_UNAVAILABLE;
                    returnMsg = CodeInfo.getCodeMessage(CodeInfo.EIP_BIND_FIREWALL_SNAT_ERROR);
                }
            } else {
                returnStat = ReturnStatus.SC_FIREWALL_DNAT_UNAVAILABLE;
                returnMsg = CodeInfo.getCodeMessage(CodeInfo.EIP_BIND_FIREWALL_DNAT_ERROR);
            }

        } catch (Exception e) {
            log.error("bind server exception", e);
            returnStat = ReturnStatus.SC_OPENSTACK_SERVER_ERROR;
            returnMsg = e.getMessage();
        } finally {
            if (null == pipId) {
                if (null != dnatRuleId) {
                    delDnat(dnatRuleId, eip.getFirewallId());
                }
                if (null != snatRuleId) {
                    delSnat(snatRuleId, eip.getFirewallId());
                }
            }
        }
        return MethodReturnUtil.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, returnStat, returnMsg);
    }

    @Override
    public MethodReturn delNatAndQos(Eip eipEntity) {

        String msg = null;
        String returnStat = "200";

        if (delDnat(eipEntity.getDnatId(), eipEntity.getFirewallId())) {
            eipEntity.setDnatId(null);
        } else {
            returnStat = ReturnStatus.SC_FIREWALL_DNAT_UNAVAILABLE;
            msg = "Failed to del dnat in firewall,id:" + eipEntity.getId() + "dnatId:" + eipEntity.getDnatId() + "";
            log.error(msg);
        }

        if (delSnat(eipEntity.getSnatId(), eipEntity.getFirewallId())) {
            eipEntity.setSnatId(null);
        } else {
            returnStat = ReturnStatus.SC_FIREWALL_SNAT_UNAVAILABLE;
            msg += "Failed to del snat in firewall, id:" + eipEntity.getId() + "snatId:" + eipEntity.getSnatId() + "";
            log.error(msg);
        }

        String innerIp = eipEntity.getFloatingIp();
        boolean removeRet;
        if (eipEntity.getChargeMode().equalsIgnoreCase(HsConstants.SHAREDBANDWIDTH) && eipEntity.getSbwId() != null) {
            removeRet = removeFipFromSbwQos(eipEntity.getFirewallId(), innerIp, eipEntity.getSbwId());
        } else {
            removeRet = delQos(eipEntity.getPipId(), eipEntity.getEipAddress(), innerIp, eipEntity.getFirewallId());
            if (removeRet) {
                eipEntity.setPipId(null);
            }
        }
        if (!removeRet) {
            returnStat = ReturnStatus.SC_FIREWALL_QOS_UNAVAILABLE;
            msg += "Failed to del qos, id:" + eipEntity.getId() + " pipId:" + eipEntity.getPipId() + "";
            log.error(msg);
        }
        if (msg == null) {
            return MethodReturnUtil.success();
        } else {
            eipEntity.setStatus(HsConstants.ERROR);
            return MethodReturnUtil.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, returnStat, msg);
        }

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

    private Boolean cmdDelSnat(String snatId, String fireWallId) {

        return true;
    }

    private Boolean cmdDelDnat(String dnatId, String fireWallId) {

        return true;
    }


    private String cmdAddDnat(String fip, String eip, String fireWallId) {


        return null;

    }

    private String cmdAddSnat(String fip, String eip, String fireWallId) {

        String strDnatPtId = fireWallCommondService.execCustomCommand(fireWallId,
                "configure\r"
                        + "ip vrouter trust-vr\r"
                        + "snatrule top from " + fip + " to Any service Any trans-to " + eip + " mode static\r"
                        + "end",
                "rule ID=");
        if (strDnatPtId == null) {
            log.error("Failed to add snat", strDnatPtId);
            return null;
        }
        return strDnatPtId.split("=")[1].trim();
    }



    private String cmdAddQos(String eip, String fip, String inboundBandwidth, String outboundBandwidth, String fireWallId) {

        return eip;
    }


    private String   getRootPipeName(String fip) {
        String[] ipSplit = fip.split("\\.");
        return ipSplit[0] + "." + ipSplit[1] + "." + ipSplit[2] + ".0";
    }


    @Override
    public synchronized boolean cmdAddSbwQos(String name, String bandwidth, String fireWallId) throws EipInternalServerException {
        Boolean flag = Boolean.TRUE;

        return flag;
    }

    @Override
    public synchronized boolean cmdDelSbwQos(String name, String fireWallId) {

        return false;
    }

    private String cmdAddIp2SbwPipe(String sbwId, String fip, String fireWallId) {


        return null;
    }

    @Override
    public JSONObject cmdShowStatisticsByAddressBook(String entryName, String period, String fireWallId){
        return null;
    }


}
