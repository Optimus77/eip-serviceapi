package com.inspur.eip.service;

import com.inspur.eip.entity.Eip;
import com.inspur.eip.repository.EipRepository;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.api.exceptions.ResponseException;
import org.openstack4j.core.transport.Config;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.network.*;
import org.openstack4j.model.network.builder.NetFloatingIPBuilder;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.openstack.networking.domain.NeutronFloatingIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


@Service
public class EipService {


    @Autowired
    private EipRepository eipRepository;


    private static String authUrl = "https://10.110.25.117:5000/v3"; //endpoint Url
    private static String user = "vpc";
    private static String password = "123456a?";
    private static String projectId = "65a859f362f749ce95237cbd08c30edf";
    private static String userDomainId = "default";
    private OSClientV3 osClientV3;
    private Config config = Config.newConfig().withSSLVerificationDisabled();
    private final static Logger log = Logger.getLogger(EipService.class.getName());

    private OSClientV3 getOsClientV3(){
        return OSFactory.builderV3()
                .endpoint(authUrl)
                .credentials(user, password, Identifier.byId(userDomainId))
                .withConfig(config)
                .scopeToProject(Identifier.byId(projectId))
                .authenticate();
    }

    public synchronized NetFloatingIP createFloatingIp(String region, String networkId,
                                                       String portId) {
        System.out.println("into");
        osClientV3 = getOsClientV3();
        System.out.println(osClientV3);
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

    public synchronized void associatePortWithFloatingIp(String region, String netFloatingIpId, String portId) {

        osClientV3 = getOsClientV3();
        NetFloatingIP associateToPort = osClientV3.networking().floatingip().associateToPort(netFloatingIpId, portId);
        if(associateToPort == null) {
            String message = String.format("Associating floating ip: %s to port: %s in region: %s failed.",
                    netFloatingIpId, portId, region);
            log.warning(message);
            throw new ResponseException(message, 500);
        }
    }
    public synchronized void disassociateFloatingIpFromPort(String region, String netFloatingIpId) {

        osClientV3 = getOsClientV3();
        NetFloatingIP associateToPort = osClientV3.networking().floatingip().disassociateFromPort(netFloatingIpId);
        if(associateToPort == null) {
            String message = String.format("Disassociating floating ip: %s in region: %s failed.",
                    netFloatingIpId, region);
            log.warning(message);
            throw new ResponseException(message, 500);
        }
    }

    public Boolean deleteFloatingIp(String name, String eipId){
        osClientV3 = getOsClientV3();
        return osClientV3.networking().floatingip().delete(eipId).isSuccess();
    }

    public List<String> listFloatingIps(){
        OSClientV3 os = getOsClientV3();
        List<String> pools = os.compute().floatingIps().getPoolNames();

        System.out.println(pools);
        return pools;
    }


    public Optional<Eip> getEipDetail(String eip_id){
        Optional<Eip> eip= eipRepository.findById(eip_id);
        return eip;
    }

    public Eip updateEipPort(Eip eip){

        return eip;
    }

    public Eip updateEipBandWidth(Eip eip){

        return eipRepository.save(eip);
    }

}
