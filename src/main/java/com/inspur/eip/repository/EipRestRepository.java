package com.inspur.eip.repository;

import com.inspur.eip.service.EipService;
import com.inspur.eip.util.CommonUtil;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.api.exceptions.ResponseException;
import org.openstack4j.model.network.NetFloatingIP;
import org.springframework.stereotype.Repository;

import java.util.logging.Logger;

/**
 * @Auther: jiasirui
 * @Date: 2018/9/14 09:32
 * @Description:  the class support data of openstack rest api
 */

public  class EipRestRepository {

    private final static Logger log = Logger.getLogger(EipService.class.getName());


    /**
     *  get the floatingip detail
     * @param id
     * @return NetFloatingIP entity
     */
    public static NetFloatingIP getFloatingIp(String id) throws Exception {
        OSClientV3 osClientV3 = CommonUtil.getOsClientV3Util();
        NetFloatingIP netFloatingIP = osClientV3.networking().floatingip().get(id);
        return netFloatingIP;
    }


    public static synchronized NetFloatingIP associatePortWithFloatingIp(String netFloatingIpId, String portId) throws Exception  {

        OSClientV3 osClientV3 = CommonUtil.getOsClientV3Util();
        NetFloatingIP associateToPort = osClientV3.networking().floatingip().associateToPort(netFloatingIpId, portId);
        return associateToPort;
    }

    public static synchronized Boolean disassociateFloatingIpFromPort( String netFloatingIpId) throws Exception {

        OSClientV3 osClientV3 = CommonUtil.getOsClientV3Util();
        NetFloatingIP associateToPort = osClientV3.networking().floatingip().disassociateFromPort(netFloatingIpId);
        return true;
    }

}
