package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.Eip;
import com.inspur.eip.entity.EipUpdateParamWrapper;
import com.inspur.eip.entity.EipPool;
import com.inspur.eip.repository.EipPoolRepository;
import com.inspur.eip.repository.EipRepository;
import org.openstack4j.model.network.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;


@Service
public class EipService {

    @Autowired
    private EipRepository eipRepository;
    @Autowired
    private EipPoolRepository eipPoolRepository;
    @Autowired
    private FirewallService firewallService;
    @Autowired
    private NeutronService neutronService;


    private final static Logger log = Logger.getLogger(EipService.class.getName());


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
        log.warning("Failed to allocate eip in network"+networkId);
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
        try {
            EipPool eip = allocateEip("region", externalNetWorkId, null);
            if (null != eip) {
                NetFloatingIP floatingIP = neutronService.createFloatingIp("region",externalNetWorkId,portId);
                if(null != floatingIP) {
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
                }else {
                    log.warning("Failed to create floating ip in external network:"+externalNetWorkId);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return eipMo;
    }


    public Boolean deleteEip(String name, String eipId){
        Boolean result = false;
        try {
            Eip eipEntity = findEipEntryById(eipId);
            if (null != eipEntity) {
                result = neutronService.deleteFloatingIp(eipEntity.getName(), eipEntity.getFloatingIpId());
                if((null != eipEntity.getPipId()) || (null != eipEntity.getDnatId()) || (null!=eipEntity.getSnatId())){
                    log.warning("Failed to delete eip,Eip is bind to port.");
                    return false;
                }
                EipPool eipPoolMo = new EipPool();
                eipPoolMo.setFireWallId(eipEntity.getFirewallId());
                eipPoolMo.setIp(eipEntity.getEip());
                eipPoolMo.setState("0");
                eipPoolRepository.save(eipPoolMo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<Eip> listEips(String vpcId){
        List<Eip> eips = eipRepository.findAll();
        List<Eip> findResult = new ArrayList<>();
        for(Eip eip: eips){
            if(eip.getVpcId().equals(vpcId)){
                findResult.add(eip);
            }
        }
        return findResult;
    }

    private Boolean associatePortWithEip(Eip eip, String portId, String instanceType) throws Exception{
        if(null != neutronService.associatePortWithFloatingIp(eip.getFloatingIpId(),portId)){
            String dnatRuleId = firewallService.addDnat(eip.getFloatingIp(), eip.getEip(), eip.getFirewallId());
            String snatRuleId = firewallService.addSnat(eip.getFloatingIp(), eip.getEip(), eip.getFirewallId());
            if((null != dnatRuleId) && (null != snatRuleId)){
                String pipId = firewallService.addQos(eip.getFloatingIp(),
                        eip.getEip(),
                        eip.getBanWidth(),
                        eip.getFirewallId());
                if(null != pipId) {
                    eip.setInstanceId(portId);
                    eip.setInstanceType(instanceType); //1:ecs 2:cps 3:slb
                    eip.setDnatId(dnatRuleId);
                    eip.setSnatId(snatRuleId);
                    eip.setPipId(pipId);
                    eip.setState("1");
                    eipRepository.save(eip);
                    return true;
                } else {
                    log.warning("Failed to add qos in firewall"+eip.getFirewallId());
                }
            } else {
                log.warning("Failed to add snat and dnat in firewall"+eip.getFirewallId());
            }
        } else {
            log.warning("Failed to associate port with eip, portId:"+portId);
        }
        return false;
    }

    private Boolean disassociatePortWithEip(Eip eipEntity) throws Exception  {
        if(null != neutronService.disassociateFloatingIpFromPort(eipEntity.getFloatingIpId())){
            Boolean result1 = firewallService.delDnat(eipEntity.getDnatId(), eipEntity.getFirewallId());
            Boolean result2 = firewallService.delSnat(eipEntity.getDnatId(), eipEntity.getFirewallId());
            if(result1 && result2) {
                if(firewallService.delQos(eipEntity.getPipId(), eipEntity.getFirewallId())){
                    eipEntity.setInstanceId(null);
                    eipEntity.setInstanceType(null); //1:ecs 2:cps 3:slb
                    eipEntity.setDnatId(null);
                    eipEntity.setSnatId(null);
                    eipEntity.setPipId(null);
                    eipEntity.setState("0");
                    eipRepository.save(eipEntity);
                    return true;
                } else {
                    log.warning("Failed to del qos"+eipEntity.getPipId());
                }
            } else {
                log.warning("Failed to del snat and dnat in firewall"+eipEntity.getFirewallId());
            }
        } else {
            log.warning("Failed to disassociate port with eip, floatingipid:"+eipEntity.getFloatingIpId());
        }
        return false;
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
                NetFloatingIP bandingFloatIp = neutronService.getFloatingIp(eipEntity.getFloatingIpId());
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
                    boolean updateStatus=firewallService.updateQosBandWidth(eipEntity.getFirewallId(),width);
                    if(updateStatus){
                        log.info("before change："+eipEntity.getBanWidth());
                        eipEntity.setBanWidth(width);
                        log.info("after  change："+eipEntity.getBanWidth());
                        eipRepository.save(eipEntity);
                        JSONObject eipJSON = new JSONObject();
                        eipJSON.put("eipid", eipEntity.getId());//the id of eip
                        NetFloatingIP bandingFloatIp =neutronService.getFloatingIp(eipEntity.getFloatingIpId());
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
                        returnjs.put("error", "the qos set is not success,please contact the dev");
                    }
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

    public String eipbindPort(String id,String portId){
        JSONObject returnjs = new JSONObject();
        try {
            Optional<Eip> eip = eipRepository.findById(id);
            if (eip.isPresent()) {
                Eip eipEntity = eip.get();
                String instanceType = eipEntity.getInstanceType();
                switch(instanceType){
                    case "1":
                        // 1：ecs
                        if(!associatePortWithEip(eipEntity, portId, instanceType)){
                            log.warning("Failed to associate port with eip:%s."+ id);
                        }
                        break;
                    case "2":
                        // 2：cps
                        break;
                    case "3":
                        // 3：slb
                        break;
                    default:
                        log.warning("Unhandled instance type.");
                        break;
                }
                JSONObject eipJSON = new JSONObject();
                eipJSON.put("eipid", eipEntity.getId());//the id of eip
                NetFloatingIP bandingFloatIp =neutronService.getFloatingIp(eipEntity.getFloatingIpId());
                if(bandingFloatIp!=null){
                    log.info(bandingFloatIp.toString());
                    eipJSON.put("status", bandingFloatIp.getStatus());//the floating ip status
                }else{
                    eipJSON.put("status", "ERROR GET INFO");//the floating ip status
                }
                eipJSON.put("iptype", eipEntity.getLinkType());//
                eipJSON.put("eip_address", eipEntity.getEip());//
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
        }
        log.info(returnjs.toString());
        return returnjs.toString();
    }

    /**
     *
     * @param id
     * @return
     */
    public String unBindPort(String id){

        JSONObject returnjs = new JSONObject();
        try {
            Optional<Eip> eip = eipRepository.findById(id);
            if (eip.isPresent()) {
                Eip eipEntity = eip.get();
                String instanceType = eipEntity.getInstanceType();
                switch(instanceType){
                    case "1":
                        // 1：ecs
                        if(!disassociatePortWithEip(eipEntity)){
                            log.warning("Failed to disassociate port with eip"+id);
                        }
                        break;
                    case "2":
                        // 2：cps
                        break;
                    case "3":
                        // 3：slb
                        break;
                    default:
                        //default ecs
                        log.warning("Unhandled instance type.");
                        break;
                }
                JSONObject eipJSON = new JSONObject();
                eipJSON.put("eipid", eipEntity.getId());//the id of eip
                NetFloatingIP bandingFloatIp =neutronService.getFloatingIp(eipEntity.getFloatingIpId());
                if(bandingFloatIp!=null){
                    log.info(bandingFloatIp.toString());
                    eipJSON.put("status", bandingFloatIp.getStatus());//the floating ip status
                }else{
                    eipJSON.put("status", "ERROR GET INFO");//the floating ip status
                }
                eipJSON.put("iptype", eipEntity.getLinkType());////TODO
                eipJSON.put("eip_address", eipEntity.getEip());////TODO
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
        }

        return returnjs.toString();
    }

}
