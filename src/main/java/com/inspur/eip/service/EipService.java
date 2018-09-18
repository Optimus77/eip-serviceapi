package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.Eip;
import com.inspur.eip.entity.EipUpdateParamWrapper;
import com.inspur.eip.entity.EipPool;
import com.inspur.eip.repository.EipPoolRepository;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.repository.EipRestRepository;
import com.inspur.eip.util.CommonUtil;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.api.exceptions.ResponseException;
import org.openstack4j.model.network.*;
import org.openstack4j.model.network.builder.NetFloatingIPBuilder;
import org.openstack4j.openstack.networking.domain.NeutronFloatingIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;


@Service
public class EipService {

    @Autowired
    private EipRepository eipRepository;
    @Autowired
    private EipPoolRepository eipPoolRepository;
    @Autowired
    private FirewallService firewallService;

    private OSClientV3 osClientV3;

    private final static Logger log = Logger.getLogger(EipService.class.getName());

    private OSClientV3 getOsClientV3() {
        try{
            return CommonUtil.getOsClientV3Util();
        }catch (Exception e){
            throw new ResponseException("Failed to get token.", 401);
        }
    }

    private synchronized EipPool allocateEip(String region, String networkId, String firewallId){

        List<EipPool> eipList = eipPoolRepository.findAll();
        for(EipPool eip: eipList) {
            if (eip != null) {
                if (eip.getState().equals("0")) {
                    if (null != firewallId) {
                        if (eip.getFireWallId().equals(firewallId)) {
                            eipPoolRepository.delete(eip);
                            return eip;
                        }
                    }
                }
            }
        }
        return null;
    }


    private Eip findEipEntryById(String eipId){
        Eip eipEntity = null;
        Optional<Eip> eip = eipRepository.findById(eipId);
        if (eip.isPresent()) {
            eipEntity = eip.get();
        }
        return eipEntity;
    }

    public Eip createEip(Eip eipConfig, String externalNetWorkId, String portId) {
        Eip eipMo = null;
        EipPool eip = allocateEip("region", externalNetWorkId, null);
        if (null != eip) {
            NetFloatingIP floatingIP = createFloatingIp("region", externalNetWorkId, portId);
            eipMo = new Eip();
            eipMo.setFloatingIp(floatingIP.getFloatingIpAddress());
            eipMo.setFixedIp(floatingIP.getFixedIpAddress());
            eipMo.setEip(eip.getIp());
            eipMo.setFirewallId(eip.getFireWallId());
            eipMo.setFloatingIpId(floatingIP.getId());
            eipMo.setBanWidth(eipConfig.getBanWidth());
            eipMo.setName(eipConfig.getName());
            eipMo.setVpcId(eipConfig.getVpcId());
            eipRepository.save(eipMo);

        }
        return eipMo;
    }

    public Boolean associatePortWithEip(String eip_id, String portId, String instanceType) {
        Eip eip = findEipEntryById(eip_id);
        if(null !=eip) {
            if (associatePortWithFloatingIp("region", eip.getFloatingIpId(), portId)) {
                String dnatRuleId = firewallService.addDnat(eip.getFloatingIp(), eip.getEip(), eip.getFirewallId());
                String snatRuleId = firewallService.addSnat(eip.getFloatingIp(), eip.getEip(), eip.getFirewallId());
                String pipId = firewallService.addQos(eip.getFloatingIp(),
                        eip.getEip(),
                        eip.getBanWidth(),
                        eip.getFirewallId());
                eip.setInstanceId(portId);
                eip.setInstanceType(instanceType); //1:ecs 2:cps 3:slb
                eip.setDnatId(dnatRuleId);
                eip.setSnatId(snatRuleId);
                eip.setPipId(pipId);
                eip.setState("1");
                eipRepository.save(eip);
                return true;
            }
        }
        return false;
    }

    public Boolean disassociatePortWithEip(String eip_id) {
        Eip eip = findEipEntryById(eip_id);
        if(disassociateFloatingIpFromPort("region", eip.getFloatingIpId())){
            Boolean result1 = firewallService.delDnat(eip.getDnatId(), eip.getFirewallId());
            Boolean result2 = firewallService.delSnat(eip.getDnatId(), eip.getFirewallId());
            if(result1 && result2) {
                if(firewallService.delQos(eip.getPipId(), eip.getFirewallId())){
                    eip.setInstanceId(null);
                    eip.setInstanceType(null); //1:ecs 2:cps 3:slb
                    eip.setDnatId(null);
                    eip.setSnatId(null);
                    eip.setPipId(null);
                    eip.setState("0");
                    eipRepository.save(eip);
                    return true;
                }
            }
        }
        return false;
    }

    private synchronized NetFloatingIP createFloatingIp(String region, String networkId, String portId) {
        osClientV3 = getOsClientV3();

        NetFloatingIPBuilder builder = new NeutronFloatingIP.FloatingIPConcreteBuilder();
        builder.floatingNetworkId(networkId);
        if (null != portId) {
            builder.portId(portId);
        }
        NetFloatingIP netFloatingIP = osClientV3.networking().floatingip().create(builder.build());
        if (netFloatingIP != null) {
            log.info("Allocated Floating ip: " + netFloatingIP.getId() + " To server with Id: ");
        } else {
            String message = String.format(
                    "Cannot create floating ip under network: %s in region: %s",
                    networkId, region);
            log.warning(message);
            throw new ResponseException(message, 500);
        }

        return netFloatingIP;
    }

    private synchronized Boolean associatePortWithFloatingIp(String region, String netFloatingIpId, String portId) {

        osClientV3 = getOsClientV3();
        NetFloatingIP associateToPort = osClientV3.networking().floatingip().associateToPort(netFloatingIpId, portId);
        if(associateToPort == null) {
            String message = String.format("Associating floating ip: %s to port: %s in region: %s failed.",
                    netFloatingIpId, portId, region);
            log.warning(message);
            throw new ResponseException(message, 500);
        }
        return true;
    }
    private synchronized Boolean disassociateFloatingIpFromPort(String region, String netFloatingIpId) {

        osClientV3 = getOsClientV3();
        NetFloatingIP associateToPort = osClientV3.networking().floatingip().disassociateFromPort(netFloatingIpId);
        if(associateToPort == null) {
            String message = String.format("Disassociating floating ip: %s in region: %s failed.",
                    netFloatingIpId, region);
            log.warning(message);
            throw new ResponseException(message, 500);
        }
        return true;
    }


    public Boolean deleteFloatingIp(String name, String eipId){
        osClientV3 = getOsClientV3();
        return osClientV3.networking().floatingip().delete(eipId).isSuccess();
    }

    public List<? extends NetFloatingIP> listFloatingIps(){
        OSClientV3 osClientV3 = getOsClientV3();
        Map<String, String> filteringParams = new HashMap<>();
        filteringParams.put("tenant_id",CommonUtil.getTokenInfo().getString("project"));
        return  osClientV3.networking().floatingip().list();

        //System.out.println(list);

    }

    /**
     * get detail of the eip
     * @param eip_id  the id of the eip instance
     * @return the json result
     */
    public String getEipDetail(String eip_id) {

        JSONObject returnjs = new JSONObject();
        try {
            Optional<Eip> eip = eipRepository.findById(eip_id);
            if (eip.isPresent()) {
                Eip eipEntity = eip.get();

                JSONObject eipJSON = new JSONObject();
                eipJSON.put("eipid", eipEntity.getId());//the id of eip
                NetFloatingIP bandingFloatIp =EipRestRepository.getFloatingIp(eipEntity.getFloatingIpv4Id());
                if(bandingFloatIp!=null){
                    log.info(bandingFloatIp.toString());
                    eipJSON.put("status", bandingFloatIp.getStatus());//the floating ip status
                }else{
                    eipJSON.put("status", "ERROR GET INFO");//the floating ip status
                }
                eipJSON.put("iptype", eipEntity.getLinkType());//
                eipJSON.put("eip_address", eipEntity.getEip());//
                eipJSON.put("private_ip_address", eipEntity.getFloatingIp());//
                eipJSON.put("bandwidth", Integer.parseInt(eipEntity.getBanWidth()));//
                eipJSON.put("chargetype", "THIS IS EMPTY"); //can't find
                eipJSON.put("chargemode", "THIS IS EMPTY");//cant't find
                eipJSON.put("create_at", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(eipEntity.getCreateTime()));
                JSONObject resourceset = new JSONObject();
                resourceset.put("resourcetype", eipEntity.getInstanceType());
                resourceset.put("resource_id", eipEntity.getInstanceId());
                eipJSON.put("resourceset", resourceset);

                returnjs.put("eip", eipJSON);

            } else {
                returnjs.put("error", "can not find instance use this id:" + eip_id+"");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnjs.put("error", e.getMessage()+"");
        }finally{
            log.info(returnjs.toString());
            return returnjs.toString();
        }

    }

    /**
     * bandWidth
     * @param
     * @return
     */
    public String updateEipBandWidth(String id, EipUpdateParamWrapper param) {

        JSONObject returnjs = new JSONObject();
        try {
            Optional<Eip> eip = eipRepository.findById(id);
            if (eip.isPresent()) {
                Eip eipEntity = eip.get();
                if(param.getEipUpdateParam().getBandWidth()!=null && param.getEipUpdateParam().getBandWidth()!=null){
                    Integer.parseInt(param.getEipUpdateParam().getBandWidth());

                    String width=param.getEipUpdateParam().getBandWidth();
                    log.info(width);
                    //TODO UpdateQos
                    log.info("before change："+eipEntity.getBanWidth());
                    eipEntity.setBanWidth(width);
                    log.info("after  change："+eipEntity.getBanWidth());
                    eipRepository.save(eipEntity);
                    JSONObject eipJSON = new JSONObject();
                    eipJSON.put("eipid", eipEntity.getId());//the id of eip
                    NetFloatingIP bandingFloatIp =EipRestRepository.getFloatingIp(eipEntity.getFloatingIpv4Id());
                    if(bandingFloatIp!=null){
                        log.info(bandingFloatIp.toString());
                        eipJSON.put("status", bandingFloatIp.getStatus());//the floating ip status
                    }else{
                        eipJSON.put("status", "error:can't get it");//the floating ip status
                    }
                    eipJSON.put("iptype", eipEntity.getLinkType());//
                    eipJSON.put("eip_address", eipEntity.getEip());//
                    eipJSON.put("port_id", eipEntity.getFloatingIp());//
                    eipJSON.put("bandwidth", Integer.parseInt(eipEntity.getBanWidth()));//
                    eipJSON.put("chargetype", "THIS IS EMPTY"); //can't find
                    eipJSON.put("create_at", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(eipEntity.getCreateTime()));
                    returnjs.put("eip",eipJSON);

                }else{
                    returnjs.put("error", "need the param bindwidth");
                }
            } else {
                returnjs.put("error", "can not find instance use this id:" +id+"");
            }
        }catch (NumberFormatException e){
            e.printStackTrace();
            returnjs.put("error", "BandWidth must be a Integer");
        } catch (Exception e) {
            e.printStackTrace();
            returnjs.put("error", e.getMessage()+"");
        }finally{
            log.info(returnjs.toString());
            return returnjs.toString();
        }

    }

    /**
     *
     * @param id
     * @param portId
     * @return
     */

    public String bandPort(String id,String portId){
        JSONObject returnjs = new JSONObject();
        try {
            Optional<Eip> eip = eipRepository.findById(id);
            if (eip.isPresent()) {
                Eip eipEntity = eip.get();
                String instanceType = eipEntity.getInstanceType();
                switch(instanceType){
                    case "1":
                        // 1：ecs
                        EipRestRepository.associatePortWithFloatingIp(eipEntity.getFloatingIpv4Id(),portId);
                        eipEntity.setInstanceType("1");
                        eipEntity.setInstanceId(portId);
                        eipRepository.save(eipEntity);
                        break;
                    case "2":
                        // 2：cps
                        break;
                    case "3":
                        // 3：slb
                        break;
                    default:
                        //default ecs
                        EipRestRepository.associatePortWithFloatingIp(eipEntity.getFloatingIpv4Id(),portId);
                        eipEntity.setInstanceType("1");
                        eipEntity.setInstanceId(portId);
                        eipRepository.save(eipEntity);
                        break;
                }
                JSONObject eipJSON = new JSONObject();
                eipJSON.put("eipid", eipEntity.getId());//the id of eip
                NetFloatingIP bandingFloatIp =EipRestRepository.getFloatingIp(eipEntity.getFloatingIpv4Id());
                if(bandingFloatIp!=null){
                    log.info(bandingFloatIp.toString());
                    eipJSON.put("status", bandingFloatIp.getStatus());//the floating ip status
                }else{
                    eipJSON.put("status", "ERROR GET INFO");//the floating ip status
                }
                eipJSON.put("iptype", eipEntity.getLinkType());//
                eipJSON.put("eip_address", eipEntity.getEipIpv4());//
                eipJSON.put("port_id","portId" ); //
                eipJSON.put("bandwidth", Integer.parseInt(eipEntity.getBanWidth()));//
                eipJSON.put("chargetype", "THIS IS EMPTY");//cant't find
                eipJSON.put("create_at", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(eipEntity.getCreateTime()));
                returnjs.put("eip", eipJSON);
            } else {
                returnjs.put("error", "can not find EIP instance use this id:" + id+"");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnjs.put("error", e.getMessage()+"");
        }finally{
            log.info(returnjs.toString());
            return returnjs.toString();
        }
    }

    /**
     *
     * @param id
     * @return
     */
    public String unBandPort(String id){

        JSONObject returnjs = new JSONObject();
        try {
            Optional<Eip> eip = eipRepository.findById(id);
            if (eip.isPresent()) {
                Eip eipEntity = eip.get();
                String instanceType = eipEntity.getInstanceType();
                switch(instanceType){
                    case "1":
                        // 1：ecs
                        EipRestRepository.disassociateFloatingIpFromPort(eipEntity.getFloatingIpv4Id());
                        eipEntity.setInstanceType(null);
                        eipEntity.setInstanceId("");
                        eipRepository.save(eipEntity);
                        break;
                    case "2":
                        // 2：cps
                        break;
                    case "3":
                        // 3：slb
                        break;
                    default:
                        //default ecs
                        EipRestRepository.disassociateFloatingIpFromPort(eipEntity.getFloatingIpv4Id());
                        eipEntity.setInstanceType("");
                        eipEntity.setInstanceId("");
                        eipRepository.save(eipEntity);
                        break;
                }
                JSONObject eipJSON = new JSONObject();
                eipJSON.put("eipid", eipEntity.getId());//the id of eip
                NetFloatingIP bandingFloatIp =EipRestRepository.getFloatingIp(eipEntity.getFloatingIpv4Id());
                if(bandingFloatIp!=null){
                    log.info(bandingFloatIp.toString());
                    eipJSON.put("status", bandingFloatIp.getStatus());//the floating ip status
                }else{
                    eipJSON.put("status", "ERROR GET INFO");//the floating ip status
                }
                eipJSON.put("iptype", eipEntity.getLinkType());////TODO
                eipJSON.put("eip_address", eipEntity.getEipIpv4());////TODO
                eipJSON.put("port_id","portI " ); //TODO
                eipJSON.put("bandwidth", Integer.parseInt(eipEntity.getBanWidth()));//
                eipJSON.put("chargetype", "THIS IS EMPTY");////TODO
                eipJSON.put("create_at", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(eipEntity.getCreateTime()));
                returnjs.put("eip", eipJSON);
            } else {
                returnjs.put("error", "can not find EIP instance use this id:" + id+"");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnjs.put("error", e.getMessage()+"");
        }finally{
            log.info(returnjs.toString());
            return returnjs.toString();
        }
    }

}
