package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.config.CodeInfo;
import com.inspur.eip.entity.MethodReturn;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.sbw.Sbw;
import com.inspur.eip.exception.EipInternalServerException;
import com.inspur.eip.repository.SbwRepository;
import com.inspur.eip.util.common.JaspytUtils;
import com.inspur.eip.util.common.MethodReturnUtil;
import com.inspur.eip.util.constant.HsConstants;
import com.inspur.eip.util.constant.ReturnStatus;
import com.inspur.plugin.hillstone.RadwareService;
import com.inspur.plugin.module.BaseObject;
import com.inspur.plugin.module.entity.AddNat;
import com.inspur.plugin.module.entity.DelNat;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.Optional;

@Slf4j
@Service
@ConditionalOnProperty(value = "firewall.type", havingValue = "radware")
public class LbService implements IDevProvider {
    @Value("${firewall.ip}")
    private  String firewallIp;
    @Value("${firewall.port}")
    private  String firewallPort;
    @Value("${firewall.user}")
    private  String firewallUser;
    @Value("${firewall.password}")
    private  String firewallPasswd;

    private static  String secretKey = "EbfYkitulv73I2p0mXI50JMXoaxZTKJ7";

    private static final  BaseObject baseObject =new BaseObject();

    @PostConstruct
    public void init(){
        baseObject.setManageUser(JaspytUtils.decyptPwd(secretKey, firewallUser));
        baseObject.setManagePwd(JaspytUtils.decyptPwd(secretKey, firewallPasswd));
        baseObject.setManageIP(firewallIp);
        baseObject.setManagePort(firewallPort);
    }

    @Autowired
    private SbwRepository sbwRepository;


    @Override
    public String addQos(String innerip, String name, String bandwidth, String fireWallId) {


        return HsConstants.UUID_LENGTH;
    }

    /**
     * update the Qos bindWidth
     *
     * @param firewallId firewall id
     * @param bindwidth  bind width
     */
    @Override
    public boolean updateQosBandWidth(String firewallId, String pipId, String pipName, String bindwidth, String fip, String eip) {


        return true;
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

    /**
     * radware 实现，添加dnat
     *
     * @param eip
     * @return
     */
    public String addDnatInEquipment(Eip eip) {
//      radware创建dnat
        RadwareService service = new RadwareService();
        AddNat addNat = service.addDnat(eip.getFloatingIp(), 0, eip.getEipAddress(), 0, 0, 0, 0, baseObject);
        return addNat.isStatus() == true ? String.valueOf(addNat.getNatFilterIndex()) : null;
    }

    /**
     * radware 实现，添加snat
     *
     * @param eip
     * @return
     */
    public String addSnatInEquipment(Eip eip) {
//      radware 创建snat
        RadwareService service = new RadwareService();
        AddNat addNat = service.addSnat(eip.getIpType(), 0, eip.getFloatingIp(), null, 0, baseObject);
        return addNat.isStatus() == true ? String.valueOf(addNat.getNatFilterIndex()) : null;
    }

    /**
     * radware 实现，根据过滤器索引删除Snat
     *
     * @param eip
     * @return
     */
    public boolean delSnatFromEquiment(Eip eip) {
        RadwareService service = new RadwareService();
        DelNat delNat = new DelNat();
        delNat.setNatFilterIndex(Integer.parseInt(eip.getSnatId()));
//        delNat.
        DelNat result = service.delSnat(delNat, baseObject);
        return result.isStatus();
    }

    /**
     * 根据过滤器索引删除Dnat
     *
     * @param eip
     * @return
     */
    public boolean delDnatFromEquiment(Eip eip) {
        RadwareService service = new RadwareService();
        DelNat delNat = new DelNat();
        delNat.setNatFilterIndex(Integer.parseInt(eip.getDnatId()));
        DelNat result = service.delDnat(delNat, baseObject);
        return result.isStatus();
    }

    /**
     * radware 实现，暂不支持qos
     * @param eip
     * @param fipAddress
     * @param eipAddress
     * @param bandWidth
     * @param firewallId
     * @return
     */
    @Override
    public MethodReturn addNatAndQos(Eip eip, String fipAddress, String eipAddress, int bandWidth, String firewallId) {
        String returnStat;
        String returnMsg;
        String pipId = null;
        String dnatId = null;
        String snatId = null;
        try {
             dnatId = this.addDnatInEquipment(eip);
            if (dnatId != null) {
                eip.setDnatId(dnatId);
                 snatId = this.addSnatInEquipment(eip);
                if (snatId != null) {
                    eip.setSnatId(snatId);
                    log.info("add nat and  successfully. snat:{}, dnat:{}", eip.getSnatId(), eip.getDnatId());
                    if (eip.getChargeMode().equalsIgnoreCase(HsConstants.SHAREDBANDWIDTH)) {
                        Optional<Sbw> optional = sbwRepository.findById(eip.getSbwId());
                        if (optional.isPresent()) {
                            pipId = this.addFipToSbwQos(eip.getFirewallId(), fipAddress, optional.get().getId());
                        }
                    } else {
                        pipId = this.addQos(fipAddress, eipAddress, String.valueOf(bandWidth), firewallId);
                    }
                    if (pipId != null) {
                        eip.setPipId(pipId);
                    }
                    return MethodReturnUtil.success(eip);
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
            returnStat = ReturnStatus.SC_FIREWALL_SERVER_ERROR;
            returnMsg = e.getMessage();
        } finally {
            if (dnatId != null) {
                this.delDnatFromEquiment(eip);
            }
            if (snatId != null) {
                this.delSnatFromEquiment(eip);
            }
        }
        return MethodReturnUtil.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, returnStat, returnMsg);
    }

    /**
     * radware 实现，暂不支持qos
     * @param eip
     * @return
     */
    @Override
    public MethodReturn delNatAndQos(Eip eip) {
        String msg = null;
        String returnStat = "200";
        if (this.delDnatFromEquiment(eip)) {
            eip.setDnatId(null);
        } else {
            returnStat = ReturnStatus.SC_FIREWALL_DNAT_UNAVAILABLE;
            msg = "Failed to del dnat in firewall,id:" + eip.getId() + "dnatId:" + eip.getDnatId() + "";
            log.error(msg);
        }
        if (this.delSnatFromEquiment(eip)) {
            eip.setSnatId(null);
        } else {
            returnStat = ReturnStatus.SC_FIREWALL_SNAT_UNAVAILABLE;
            msg += "Failed to del snat in firewall, id:" + eip.getId() + "snatId:" + eip.getSnatId() + "";
            log.error(msg);
        }
        String innerIp = eip.getFloatingIp();
        boolean removeRet = false;
        if (eip.getChargeMode().equalsIgnoreCase(HsConstants.SHAREDBANDWIDTH) && eip.getSbwId() != null) {
            removeRet = this.removeFipFromSbwQos(eip.getFirewallId(), innerIp, eip.getSbwId());
        } else if (innerIp != null && eip.getPipId() != null) {
            removeRet = this.delQos(eip.getPipId(), eip.getEipAddress(), innerIp, eip.getFirewallId());
            if (removeRet) {
                eip.setPipId(null);
            }
        }
        if (!removeRet) {
            returnStat = ReturnStatus.SC_FIREWALL_QOS_UNAVAILABLE;
            msg += "Failed to del qos, id:" + eip.getId() + " pipId:" + eip.getPipId() + "";
            log.error(msg);
        }
        if (msg == null) {
            return MethodReturnUtil.success();
        } else {
            eip.setStatus(HsConstants.ERROR);
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
            retPipeId = HsConstants.UUID_LENGTH;
//                    cmdAddIp2SbwPipe(sbwId, floatIp, firewallId);
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
            int timeOut = 3000;
            return InetAddress.getByName(ipAddress).isReachable(timeOut);
        } catch (Exception e) {
            return false;
        }
    }


    private String getRootPipeName(String fip) {
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
    public JSONObject cmdShowStatisticsByAddressBook(String entryName, String period, String fireWallId) {
        return null;
    }


}
