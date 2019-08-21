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
import com.inspur.eip.repository.FirewallRepository;
import com.inspur.eip.repository.SbwRepository;
import com.inspur.eip.util.common.CommonUtil;
import com.inspur.eip.util.common.JaspytUtils;
import com.inspur.eip.util.common.MethodReturnUtil;
import com.inspur.eip.util.constant.ErrorStatus;
import com.inspur.eip.util.constant.HillStoneConfigConsts;
import com.inspur.eip.util.constant.HsConstants;
import com.inspur.eip.util.constant.ReturnStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
//@ConditionalOnProperty(value = "firewall.type",havingValue = "radware")
public class LbService implements IDevProvider{


    @Value("${firewall.ip}")
    private String firewallIp;

    @Value("${firewall.port}")
    private String firewallPort;

    @Value("${firewall.user}")
    private String firewallUser;

    @Value("${firewall.password}")
    private String firewallPasswd;

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

    //    @Value("${jasypt.password}")
    private String secretKey = "EbfYkitulv73I2p0mXI50JMXoaxZTKJ7";
    private Map<String, Firewall> firewallConfigMap = new HashMap<>();
    private String vr = "trust-vr";

    @Override
    public  String test(){
        log.info("this is hillstone service");
        return "test lb";

    }
    @Override
    public Firewall getFireWallById(String id) {
        if (!firewallConfigMap.containsKey(id)) {

            Firewall fireWallConfig = new Firewall();

            fireWallConfig.setUser(JaspytUtils.decyptPwd(secretKey, firewallUser));
            fireWallConfig.setPasswd(JaspytUtils.decyptPwd(secretKey, firewallPasswd));
            fireWallConfig.setIp(firewallIp);
            fireWallConfig.setPort(firewallPort);
            firewallConfigMap.put(id, fireWallConfig);
            log.info("get firewall ip:{}, port:{}, passwd:{}, user:{}", firewallIp,
                    firewallPort, firewallUser, firewallPasswd);

        }
        return firewallConfigMap.get(id);
    }

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

    private boolean cmdUpdateRootQosBandWidth(String fireWallId, String pipNmae, String bandwidth) {
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
//            String delResult = fireWallCommondService.execCustomCommand(fireWallId,
//                    "configure\r"
//                            + "end",
//                    null);
//            if (null != delResult && delResult.equals("ERROR")) {
//                log.error("Firewall connection check error:{}", delResult);
//                return false;
//            } else {
//                return true;
//            }
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
   

    /**
     *
     * @param entryName     eipAddress
     * @param fireWallId    防火墙id
     * @param control      创建或者删除操作  true:创建   flase：删除
     * @return
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
            log.info("success show :");
            return json;
        }else {
            log.error(ErrorStatus.ENTITY_BADREQUEST_ERROR.getMessage() + "param not correct,entryName:{},period;{}", entryName,period);
            throw new EipBadRequestException(ErrorStatus.ENTITY_BADREQUEST_ERROR.getCode(), ErrorStatus.ENTITY_BADREQUEST_ERROR.getMessage());
        }
    }


}
