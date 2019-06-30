package com.inspur.eip.service;


import com.inspur.eip.entity.EipUpdateParam;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface IEipService {


    /**
     * 1.delete  floatingIp
     * 2.Determine if Snate and Qos is deleted
     * 3.delete eip
     *
     * @param eipIds  eip ids
     * @return       result: true/false
     */

    ResponseEntity deleteEipList(List<String> eipIds);

    /**
     *  listShareBandWidth the eip
     * @param currentPage  the current page
     * @param limit  element of per page
     * @return       result
     */
    ResponseEntity listEips(int currentPage,int limit, String status);

    ResponseEntity listEipsV(int currentPage, int limit, String status);

    /**
     * get detail of the eip
     * @param eipId  the id of the eip instance
     * @return the json result
     */
    ResponseEntity getEipDetail(String eipId);


    /**
     * eip bind with port
     * @param id      id
     * @param serverId  server id
     * @param type   //1：ecs // 2：cps // 3：slb
     * @param portId   port id
     * @return        result
     */
    ResponseEntity eipBindWithInstance(String id,String type, String serverId, String portId, String slbIp);

    /**
     *  unbind
     * @param eipId eipid
     * @param instanceId  instanceId
     * @return ret
     */
    ResponseEntity eipUnbindWithInstacnce(String eipId, String instanceId);


    /**
     * get eip by floating ip
     */
    ResponseEntity getEipByInstanceIdV2(String instanceId);


    ResponseEntity getEipByInstanceId(String instanceId);



    ResponseEntity getEipByIpAddressV2(String eip);

    ResponseEntity getEipByIpAddress(String eip);

    ResponseEntity getEipCount();

    /**
     *   the eipV6
     * @return       result
     */
    ResponseEntity listEipsByBandWidth(String status);

    ResponseEntity getFreeEipCount();

    ResponseEntity getUsingEipCount();

    ResponseEntity getTotalEipCount();

    ResponseEntity getUsingEipCountByStatus(String status);

    ResponseEntity getEipDetailsByIpAddress(String eipAddress);

}
