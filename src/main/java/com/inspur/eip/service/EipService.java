package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.Eip;
import com.inspur.eip.entity.EipPool;
import com.inspur.eip.repository.EipPoolRepository;
import com.inspur.eip.repository.EipRepository;
import com.inspur.eip.util.CommonUtil;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.api.exceptions.ResponseException;
import org.openstack4j.model.network.*;
import org.openstack4j.model.network.builder.NetFloatingIPBuilder;
import org.openstack4j.openstack.networking.domain.NeutronFloatingIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private OSClientV3 osClientV3;

    private final static Logger log = Logger.getLogger(EipService.class.getName());

    private OSClientV3 getOsClientV3(){
        return CommonUtil.getOsClientV3Util();
    }

    public synchronized EipPool allocateEip(String region, String networkId){

        List<EipPool> eipList = eipPoolRepository.findAll();
        for(EipPool eip: eipList){
            if(eip != null){
                eipPoolRepository.delete(eip);
                return eip;
            }
        }
        return null;
    }


    public synchronized NetFloatingIP createFloatingIp(String region, String networkId, String portId) {
        //System.out.println("into");
        osClientV3 = getOsClientV3();
        //System.out.println(osClientV3);
        NetFloatingIPBuilder builder = new NeutronFloatingIP.FloatingIPConcreteBuilder();
        builder.floatingNetworkId(networkId);
        if(null != portId) {
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

    public synchronized Boolean associatePortWithFloatingIp(String region, String netFloatingIpId, String portId) {

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
    public synchronized Boolean disassociateFloatingIpFromPort(String region, String netFloatingIpId) {

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

    public NetFloatingIP getFloatingIpDetail(String netFloatingIpId){
        osClientV3 = getOsClientV3();
        Map<String, String> filteringParams = new HashMap<>();
        filteringParams.put("tenant_id",CommonUtil.getProjectId());

        List<NetFloatingIP> netFloatingIPS= (List<NetFloatingIP>) osClientV3.networking().floatingip().list(filteringParams);
        log.info(JSONObject.toJSONString(netFloatingIPS));
        NetFloatingIP netFloatingIP =osClientV3.networking().floatingip().get(netFloatingIPS.get(0).getId());
        return netFloatingIP;
    }

    public Boolean deleteFloatingIp(String name, String eipId){
        osClientV3 = getOsClientV3();
        return osClientV3.networking().floatingip().delete(eipId).isSuccess();
    }

    public List<? extends NetFloatingIP> listFloatingIps(){
        OSClientV3 osClientV3 = getOsClientV3();
        Map<String, String> filteringParams = new HashMap<>();
        filteringParams.put("tenant_id",CommonUtil.getProjectId());
        return  osClientV3.networking().floatingip().list();

        //System.out.println(list);

    }


    public Optional<Eip> getEipDetail(String eip_id){
        Optional<Eip> eip= eipRepository.findById(eip_id);
        osClientV3 = getOsClientV3();

        osClientV3.networking().floatingip().get(eip_id);
        return eip;
    }

    public Eip updateEipPort(Eip eip){

        return eip;
    }

    public Eip updateEipBandWidth(Eip eip){

        return eipRepository.save(eip);
    }

}
