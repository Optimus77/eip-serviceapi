package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.config.CodeInfo;
import com.inspur.eip.entity.MethodReturn;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.fw.*;
import com.inspur.eip.entity.sbw.Sbw;
import com.inspur.eip.exception.EipBadRequestException;
import com.inspur.eip.exception.EipInternalServerException;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.repository.SbwRepository;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.common.MethodReturnUtil;
import com.inspur.eip.util.constant.ErrorStatus;
import com.inspur.eip.util.constant.HillStoneConfigConsts;
import com.inspur.eip.util.constant.HsConstants;
import com.inspur.eip.util.constant.ReturnStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Service
public class FirewallService {

    @Autowired
    private QosService qosService;

    @Autowired
    private SbwRepository sbwRepository;

    @Autowired
    private EipRepository eipRepository;

    @Autowired
    private FireWallCommondService fireWallCommondService;

    private String addDnat(String fip, String eip, String fireWallId) {
        String strDnatPtId = fireWallCommondService.execCustomCommand(fireWallId,
                "configure\r"
                        + "ip vrouter trust-vr\r"
                        + "dnatrule top from  Any to " + eip + " service Any trans-to " + fip + "\r"
                        + "end",
                "rule ID=");
        if (strDnatPtId == null) {
            log.error("Failed to add dnat", strDnatPtId);
            return null;
        }
        if (strDnatPtId.contains("=")) {
            return strDnatPtId.split("=")[1].trim();
        } else {
            log.error("cmd add dnat error, return:{}", strDnatPtId);
            return null;
        }
    }

    private String addSnat(String fip, String eip, String fireWallId) {
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


    public String addQos(String fip, String eip, String bandwidth, String fireWallId) {
        String pipid;
        String inBandWidth = "50";
        if (Integer.valueOf(bandwidth) > 50) {
            inBandWidth = bandwidth;
        }

        if (null == fip || null == eip) {
            return null;
        }
        String rootPipeNmae = getRootPipeName(fip);
        if (0 >= eipRepository.countByPipId(rootPipeNmae)) {
            boolean result = cmdAddRootPipe(rootPipeNmae, eip, fip, inBandWidth, bandwidth, fireWallId);
            log.info("Add root-pipe {}, result:{}", rootPipeNmae, result);
            if (result) {
                return rootPipeNmae;
            }
            return null;
        }
        String retString = "Tip: Pipe \"" + eip + "\" is enabled";
        String strResult = fireWallCommondService.execCustomCommand(fireWallId,
                "configure\r"
                        + "qos-engine first\r"
                        + "root-pipe  " + rootPipeNmae + "\r"
                        + "pipe  " + eip + "\r"
                        + "pipe-map \r"
                        + "dst-ip " + fip + "/32\r"
                        + "exit\r"
                        + "pipe-rule forward reserve-bandwidth Mbps 1 max Mbps " + inBandWidth + "\r"
                        + "pipe-rule backward reserve-bandwidth Mbps 1 max Mbps " + bandwidth + "\r"
                        + "end",
                retString);
        if (strResult == null || !strResult.contains(retString)) {
            log.error("Failed to add cmd qos", strResult);
            return null;
        }
        return rootPipeNmae;
    }

    /**
     * update the Qos bindWidth
     *
     * @param firewallId firewall id
     * @param bindwidth  bind width
     */

    public boolean updateQosBandWidth(String firewallId, String pipId, String pipNmae, String bindwidth, String fip, String eip) {

        Firewall fwBean = CommonUtil.getFireWallById(firewallId);
        if (fwBean != null) {
            if (null != fip && pipId.equals(getRootPipeName(fip))) {
                return cmdUpdateQosBandWidth(eip, fip, bindwidth, firewallId);
            } else if (pipId.length() == "9dea38f8-f59c-4847-ba43-f0ef61a6986c".length()) {
                return cmdUpdateRootQosBandWidth(firewallId, pipNmae, bindwidth);
            }
        }
        return false;
    }

    private boolean cmdUpdateRootQosBandWidth(String fireWallId, String pipNmae, String bandwidth) {

        String inBandWidth = "50";
        if (Integer.valueOf(bandwidth) > 50) {
            inBandWidth = bandwidth;
        }
        String retString = "Root pipe \"" + pipNmae + "\" is unavailable";
        String strResult = fireWallCommondService.execCustomCommand(fireWallId,
                "configure\r"
                        + "qos-engine first\r"
                        + "root-pipe  " + pipNmae + "\r"
                        + "pipe-rule forward bandwidth Mbps " + inBandWidth + "\r"
                        + "pipe-rule backward bandwidth Mbps " + bandwidth + "\r"
                        + "end",
                null);
        if (strResult == null) {
            return true;
        }
        log.error("Failed to update cmd qos", strResult);
        return false;
    }

    /**
     * del qos
     *
     * @param pipid pipid
     * @param devId devid
     * @return ret
     */

    public boolean delQos(String pipid, String eip, String fip, String devId) {
        if (StringUtils.isNotEmpty(pipid)) {
            if (null != eip && null != fip && pipid.equals(getRootPipeName(fip))) {
                return cmdDelQos(pipid, eip, devId);
            }else if(HsConstants.UUID_LENGTH.length() == pipid.length()){
                return cmdDelSbwQos( pipid, devId);
            }
            Firewall fwBean = CommonUtil.getFireWallById(devId);
            // 只能根据管道名称 eg：9dea38f8-f59c-4847-ba43-f0ef61a6986c 或者管道Id eg: 1563205723075033629 删除，不能删EIP的根管道eg:10.110.26.0
            if (null != fwBean && (HsConstants.PIPE_ID_LENGTH.length() == pipid.length() || HsConstants.UUID_LENGTH.length() == pipid.length())) {
                QosService qs = new QosService(fwBean.getIp(), fwBean.getPort(), fwBean.getUser(), fwBean.getPasswd());
                return qs.delQosPipe(pipid);
            } else {
                log.info("Failed to get fireWall by id when del qos,dev:{}, pipId:{}", devId, pipid);
            }
        } else {
            log.info("qos id is empty, no need to del qos.");
            return true;
        }
        return false;
    }



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

    public String addFipToSbwQos(String firewallId, String fip, String sbwId) {
        if (sbwId.length() != HsConstants.UUID_LENGTH.length()) {
            return null;
        }
        String retCheck = "unrecognized keyword 1";
        String pipeMapId = "1";
        String strResult = fireWallCommondService.execCustomCommand(firewallId,
                "configure\r"
                        + "qos-engine first\r"
                        + "root-pipe  " + sbwId + "\r"
                        + "pipe-map " + pipeMapId + "\r"
                        + "end",
                retCheck);
        if (strResult != null && strResult.contains(retCheck)) {
            pipeMapId = "";
        }

        String addResult = fireWallCommondService.execCustomCommand(firewallId,
                "configure\r"
                        + "qos-engine first\r"
                        + "root-pipe  " + sbwId + "\r"
                        + "pipe-map " + pipeMapId + "\r"
                        + "dst-ip " + fip + "/32\r"
                        + "end",
                null);
        if (addResult == null) {
            return sbwId;
        }
        log.error("Failed to add cmd qos", strResult);
        return null;
    }

    /**
     * remove eip from shared band
     *
     * @param firewallId id
     * @param floatIp    fip
     * @return ret
     */

    public boolean removeFipFromSbwQos(String firewallId, String floatIp, String sbwId) {
        if (StringUtils.isBlank(floatIp)){
            log.error("floating ip is null,floatIp:{}",floatIp);
            return false;
        }
        if (cmdDelIpInSbwPipe(sbwId, floatIp, firewallId)) {
            log.info("success cmd to del ip from sbw qos");
            return true;
        }
        Firewall fwBean = CommonUtil.getFireWallById(firewallId);
        if (fwBean != null) {
            qosService.setFwIp(fwBean.getIp());
            qosService.setFwPort(fwBean.getPort());
            qosService.setFwUser(fwBean.getUser());
            qosService.setFwPwd(fwBean.getPasswd());
            Boolean removeResult = qosService.removeIpFromPipe(floatIp, sbwId);
            if (removeResult) {
                log.info("Success remove Ip from SbwQos，sbwId:{},floatIp:{} ", sbwId, floatIp );
                return true;
            }
            log.warn("Failed remove Ip from SbwQos :floatIp:{} sbwId:{}", floatIp, sbwId);
        }
        return false;
    }


    public boolean ping(String ipAddress, String fireWallId) {
        try {

            int  timeOut =  3000 ;
            return InetAddress.getByName(ipAddress).isReachable(timeOut);
        } catch (Exception e) {
            return false;
        }
    }

    private Boolean delSnat(String snatId, String fireWallId) {
        if (snatId != null) {
            String delResult = fireWallCommondService.execCustomCommand(fireWallId,
                    "configure\r"
                            + "ip vrouter trust-vr\r"
                            + "no Snatrule id " + snatId + "\r"
                            + "end",
                    null);
            if (delResult != null) {
                if (!delResult.contains("cannot be found")) {
                    return false;
                }
                log.error("Failed to delete snatId.");
            }
        }
        log.info("Cmd delete snat :{} successfully", snatId);
        return true;
    }

    private Boolean delDnat(String dnatId, String fireWallId) {
        if (null != dnatId) {
            String delResult = fireWallCommondService.execCustomCommand(fireWallId,
                    "configure\r"
                            + "ip vrouter trust-vr\r"
                            + "no dnatrule id " + dnatId + "\r"
                            + "end",
                    null);
            if (delResult != null) {
                if (!delResult.contains("cannot be found")) {
                    return false;
                }
                log.error("Failed to delete dnatId.");
            }
        }
        log.info("Cmd delete dnat :{} successfully", dnatId);
        return true;
    }


    public Boolean cmdDelQos(String rootPipeName, String eip, String fireWallId) {
        String ret = fireWallCommondService.execCustomCommand(fireWallId,
                "configure\r"
                        + "qos-engine first\r"
                        + "root-pipe  " + rootPipeName + "\r"
                        + "no pipe  " + eip + "\r"
                        + "end",
                "^-----");
        if (ret == null) {
            log.info("Del qos by cmd successfully:");
            return true;
        }
        if (ret.contains("^-----")) {
            log.error("Can not find {} in {}", eip, rootPipeName);
            return true;
        }
        log.error("Failed to del qos by cmd:{}", ret);
        return false;
    }


    boolean cmdUpdateQosBandWidth(String eip, String fip, String bandwidth, String fireWallId) {

        String retString = "Tip: Pipe \"" + eip + "\" is enabled";
        String strResult = fireWallCommondService.execCustomCommand(fireWallId,
                "configure\r"
                        + "qos-engine first\r"
                        + "root-pipe  " + getRootPipeName(fip) + "\r"
                        + "pipe  " + eip + "\r"
                        + "pipe-rule forward reserve-bandwidth Mbps 1 max Mbps " + bandwidth + "\r"
                        + "pipe-rule backward reserve-bandwidth Mbps 1 max Mbps " + bandwidth + "\r"
                        + "end",
                retString);
        if (strResult == null || !strResult.contains(retString)) {
            log.error("Failed to update cmd qos", strResult);
            return false;
        }
        return true;
    }

    private String   getRootPipeName(String fip) {
        String[] ipSplit = fip.split("\\.");
        return ipSplit[0] + "." + ipSplit[1] + "." + ipSplit[2] + ".0";
    }

    private boolean cmdAddRootPipe(String rootPipeName, String eip, String fip, String inBwd, String outBwd, String fireWallId) {

        String retString = "Tip: Pipe \"" + eip + "\" is enabled";
        String strResult = fireWallCommondService.execCustomCommand(fireWallId,
                "configure\r"
                        + "qos-engine first\r"
                        + "root-pipe  " + rootPipeName + "\r"
                        + "qos-mode police\r"
                        + "pipe-map \r"
                        + "dst-ip " + fip + "/24\r"
                        + "src-addr Any\r"
                        + "exit\r"
                        + "pipe-rule forward bandwidth Gbps 1\r"
                        + "pipe-rule backward bandwidth Gbps 1\r"
                        + "pipe  " + eip + "\r"
                        + "pipe-map \r"
                        + "dst-ip " + fip + "/32\r"
                        + "exit\r"
                        + "pipe-rule forward reserve-bandwidth Mbps 1 max Mbps " + inBwd + "\r"
                        + "pipe-rule backward reserve-bandwidth Mbps 1 max Mbps " + outBwd + "\r"
                        + "end",
                retString);
        if (strResult == null || !strResult.contains(retString)) {
            log.error("Failed to add cmd root qos", strResult);
            return false;
        }
        return true;
    }

    /**
     * @param name
     * @param bandwidth
     * @param fireWallId
     * @return
     */

    public synchronized boolean cmdAddSbwQos(String name, String bandwidth, String fireWallId) throws EipInternalServerException {
        Boolean flag = Boolean.TRUE;
        String inBandWidth = "50";
        if (Integer.valueOf(bandwidth) > 50) {
            inBandWidth = bandwidth;
        }
        String retString = "Root pipe \"" + name + "\" is unavailable";
        String strResult = fireWallCommondService.execCustomCommand(fireWallId,
                "configure\r"
                        + "qos-engine first\r"
                        + "root-pipe  " + name + "\r"
                        + "sequence 1\r"
                        + "pipe-rule forward bandwidth Mbps " + inBandWidth + "\r"
                        + "pipe-rule backward bandwidth Mbps " + bandwidth + "\r"
                        + "end",
                retString);
        //成功标志：qos创建成功但不可用
        // Tip: Root pipe "7ec675a5-38be-4fb7-9de0-70faae88b5fa" is unavailable, end string:Tip: Root pipe "7ec675a5-38be-4fb7-9de0-70faae88b5fa" is unavailable
        if (strResult == null || !strResult.contains(retString)) {
            flag = Boolean.FALSE;
            log.error("Failed to add cmd sbw qos", strResult);
        }
        return flag;
    }

    public synchronized boolean cmdDelSbwQos(String name, String fireWallId) {

        String strResult = fireWallCommondService.execCustomCommand(fireWallId,
                "configure\r"
                        + "qos-engine first\r"
                        + "no root-pipe  " + name + "\r"
                        + "end",
                null);
        if (strResult == null) {
            return true;
        }
        log.error("Failed to del cmd sbw qos", strResult);
        return false;
    }


    private boolean cmdDelIpInSbwPipe(String rootPipeName, String fip, String fireWallId) {
        log.info("loading to remove fip from sbw qos");
        String strResult = fireWallCommondService.execCustomCommand(fireWallId,
                "configure\r"
                        + "qos-engine first\r"
                        + "root-pipe  " + rootPipeName + "\r"
                        + "pipe-map 1\r"
                        + "no dst-ip " + fip + "/32\r"
                        + "end",
                null);
        if (strResult != null) {
            log.error("Failed to add cmd qos", strResult);
            return false;
        }
        return true;
    }

    /**
     *
     * @param entryName     eipAddress
     * @param fireWallId    防火墙id
     * @param control      创建或者删除操作  true:创建   flase：删除
     * @return
     */
    public  boolean cmdCreateOrDeleteAddressBook(String entryName, String fireWallId, boolean control){
        StringBuilder sb = new StringBuilder();
        sb.append(HillStoneConfigConsts.CONFIGURE_MODEL_ENTER);
        if (!control){
            sb.append(HillStoneConfigConsts.NO_SPACE);
        }
        sb.append( HillStoneConfigConsts.ADDRESS_SPACE + entryName+ HillStoneConfigConsts.ADDRESSBOOK_SUBFIX ).append(HillStoneConfigConsts.ENTER_END);
        //        configure\r[no] address 192.168.1.11\rend
        String strResult = fireWallCommondService.execCustomCommand(fireWallId, sb.toString(), "unrecognized keyword");
        if (StringUtils.isNotBlank(strResult) && strResult.contains("unrecognized keyword") ) {
            log.warn(ErrorStatus.FIREWALL_UNRECOGNIZED_COMMAND.getMessage()+":{}",strResult);
            return false;
        }else if (StringUtils.isBlank(strResult)) {
            return true;
        }
        log.error(ErrorStatus.FIREWALL_UNRECOGNIZED_COMMAND.getMessage()+ ":{}",strResult);
        throw new EipInternalServerException(ErrorStatus.FIREWALL_DEAL_ADDRESS_BOOK_ERROR.getCode(),ErrorStatus.FIREWALL_DEAL_ADDRESS_BOOK_ERROR.getMessage());
    }

    /**
     * 向地址簿中插入要匹配的条件，支持多种地址类型，入参需调用方做非空校验
     * @param entryName 地址簿名称
     * @param param  匹配项
     * @param addressType  参数类型：case中已罗列
     * @param fireWallId
     * @return
     */
    public boolean cmdInsertOrRemoveParamInAddressBook(String entryName, String param, String addressType, String fireWallId, boolean control) {
        StringBuilder sb = new StringBuilder();
        sb.append(HillStoneConfigConsts.CONFIGURE_MODEL_ENTER + HillStoneConfigConsts.ADDRESS_SPACE + entryName + HillStoneConfigConsts.ADDRESSBOOK_SUBFIX +HillStoneConfigConsts.SSH_ENTER);
        if (!control){
            sb.append(HillStoneConfigConsts.NO_SPACE);
        }
        switch (addressType) {
            case HillStoneConfigConsts.IP_ADDRESS_TYPE:
                //ip 10.110.29.206/32
                sb.append(HillStoneConfigConsts.IP_ADDRESS_TYPE + HillStoneConfigConsts.SSH_SPACE + param + HillStoneConfigConsts.D_TYPE_ADDRESS);
                break;
            case HillStoneConfigConsts.HOST_ADDRESS_TYPE:
                //host baidu.com
                sb.append(HillStoneConfigConsts.HOST_ADDRESS_TYPE + HillStoneConfigConsts.SSH_SPACE + param);
                break;
            case HillStoneConfigConsts.RANGE_ADDRESS_TYPE:
                //range 10.110.29.206 10.110.29.208
                sb.append(HillStoneConfigConsts.RANGE_ADDRESS_TYPE + HillStoneConfigConsts.SSH_SPACE + param + HillStoneConfigConsts.SSH_SPACE + param);
                break;
            case HillStoneConfigConsts.COUNTRY_ADDRESS_TYPE:
                //country CN     That mean: country China
                sb.append(HillStoneConfigConsts.COUNTRY_ADDRESS_TYPE + HillStoneConfigConsts.SSH_SPACE + param);
                break;
            case HillStoneConfigConsts.MEMBER_ADDRESS_TYPE:
                // member zerah1   That mean : add other address-entry
                sb.append(HillStoneConfigConsts.MEMBER_ADDRESS_TYPE + HillStoneConfigConsts.SSH_SPACE + param);
                break;
            default:
                log.error(ErrorStatus.ENTITY_BADREQUEST_ERROR.getMessage() + "addressType:{}", addressType);
                throw new EipBadRequestException(ErrorStatus.ENTITY_BADREQUEST_ERROR.getCode(), ErrorStatus.ENTITY_BADREQUEST_ERROR.getMessage());
        }
        sb.append(HillStoneConfigConsts.ENTER_END);
//        configure\raddress 192.168.1.10\rip 192.168.1.10/32\rend
        String strResult = fireWallCommondService.execCustomCommand(fireWallId, sb.toString(), "unrecognized keyword");
        if (StringUtils.isNotBlank(strResult) && strResult.contains("already added")) {
            log.warn("This param unrecognized in addressBook:{},failed:{}",entryName,param);
            return true;
        } else if (StringUtils.isBlank(strResult)) {
            log.debug("This param add to addressBook:{}, success:{}",entryName,param);
            return true;
        }
        throw new EipInternalServerException(ErrorStatus.FIREWALL_DEAL_ADDRESS_BOOK_ERROR.getCode(),ErrorStatus.FIREWALL_DEAL_ADDRESS_BOOK_ERROR.getMessage());
    }

    /**
     * cmd to create statistics book
     * @param entryName     address book 中已经存在的对象,name为Eip地址
     * @param firewallId
     * @param control      true :创建监控地址簿  flase :删除监控地址簿
     * @return
     * @throws EipInternalServerException
     */
    public boolean cmdOperateStatisticsBook(String entryName, String firewallId, boolean control) throws EipInternalServerException{
        StringBuilder sb = new StringBuilder();
        sb.append(HillStoneConfigConsts.CONFIGURE_MODEL_ENTER);
        if(!control){
            sb.append(HillStoneConfigConsts.NO_SPACE);
        }
        sb.append(HillStoneConfigConsts.STATISTICS_SPACE + HillStoneConfigConsts.ADDRESS_SPACE + entryName + HillStoneConfigConsts.ADDRESSBOOK_SUBFIX +HillStoneConfigConsts.ENTER_END);
//        configure\r address 192.168.1.11\rend
        String strResult = fireWallCommondService.execCustomCommand(firewallId, sb.toString(), "unrecognized keyword");
        if (StringUtils.isNotBlank(strResult) && strResult.contains("unrecognized keyword")){
            log.warn(ErrorStatus.FIREWALL_UNRECOGNIZED_COMMAND.getMessage(),"statistics address book not exist");
            return false;
        }else if (StringUtils.isBlank(strResult)){
            return true;
        }
        log.error(ErrorStatus.FIREWALL_UNRECOGNIZED_COMMAND.getMessage()+ ":{}",strResult);
        throw new EipInternalServerException(ErrorStatus.FIREWALL_UNRECOGNIZED_COMMAND.getCode(), ErrorStatus.FIREWALL_UNRECOGNIZED_COMMAND.getMessage());
    }

    /**
     * 通过查看统计地址簿查看流量统计信息，入参需调用方做非空校验
     * @param entryName
     * @param period
     * @return
     */
    public JSONObject cmdShowStatisticsByAddressBook( String entryName, String period, String fireWallId){
        StringBuilder sb = new StringBuilder();
        sb.append(HillStoneConfigConsts.CONFIGURE_MODEL_ENTER + HillStoneConfigConsts.SHOW_SPACE + HillStoneConfigConsts.STATISTICS_SPACE + HillStoneConfigConsts.ADDRESS_SPACE + entryName +  HillStoneConfigConsts.ADDRESSBOOK_SUBFIX);
        switch (period){
            case "":
                break;
            case HillStoneConfigConsts.CURRENT_PERIOD_TYPE:
                sb.append(HillStoneConfigConsts.SSH_SPACE + HillStoneConfigConsts.CURRENT_PERIOD_TYPE);
                break;
            case HillStoneConfigConsts.LASTHOUR_PERIOD_TYPE:
                sb.append(HillStoneConfigConsts.SSH_SPACE + HillStoneConfigConsts.LASTHOUR_PERIOD_TYPE);
                break;
            case HillStoneConfigConsts.LASTDAY_PERIOD_TYPE:
                sb.append(HillStoneConfigConsts.SSH_SPACE + HillStoneConfigConsts.LASTDAY_PERIOD_TYPE);
                break;
            case HillStoneConfigConsts.LASTMONTH_PERIOD_TYPE:
                sb.append(HillStoneConfigConsts.SSH_SPACE + HillStoneConfigConsts.LASTMONTH_PERIOD_TYPE);
                break;
            default:
                log.error(ErrorStatus.ENTITY_BADREQUEST_ERROR.getMessage() + "period:{}", period);
                throw new EipBadRequestException(ErrorStatus.ENTITY_BADREQUEST_ERROR.getCode(), ErrorStatus.ENTITY_BADREQUEST_ERROR.getMessage());
        }
        sb.append(HillStoneConfigConsts.SSH_ENTER + HillStoneConfigConsts.SSH_SPACE + HillStoneConfigConsts.END);
        //        configure\rshow statistics address 192.168.1.11 lasthour\rend
        JSONObject json = fireWallCommondService.cmdShowStasiticsAddress(fireWallId, sb.toString());
        if (json !=null){
            log.debug("success show :{}",json);
            return json;
        }else {
            log.error(ErrorStatus.ENTITY_BADREQUEST_ERROR.getMessage() + "param not correct,entryName:{},period;{}", entryName,period);
            throw new EipBadRequestException(ErrorStatus.ENTITY_BADREQUEST_ERROR.getCode(), ErrorStatus.ENTITY_BADREQUEST_ERROR.getMessage());
        }
    }


}
