package com.inspur.eip.service;

import com.alibaba.fastjson.JSONObject;
import com.inspur.eip.entity.MethodReturn;
import com.inspur.eip.entity.eip.Eip;
import com.inspur.eip.entity.fw.Firewall;

public interface IDevProvider {

    String addQos(String innerip, String name, String bandwidth, String fireWallId);

    boolean delQos(String pipid, String eip, String fip, String devId);

    MethodReturn delNatAndQos(Eip eipEntity);

    MethodReturn addNatAndQos(Eip eip, String fipAddress, String eipAddress, int bandWidth, String firewallId);

    String addFipToSbwQos(String firewallId, String floatIp, String sbwId);

    boolean removeFipFromSbwQos(String firewallId, String floatIp, String sbwId);

    boolean ping(String ipAddress, String fireWallId);

    boolean updateQosBandWidth(String firewallId, String pipId, String pipNmae, String bindwidth, String fip, String eip);

    boolean cmdAddSbwQos(String name, String bandwidth, String fireWallId);

    boolean cmdDelSbwQos(String name, String fireWallId);

    JSONObject cmdShowStatisticsByAddressBook(String entryName, String period, String fireWallId);

    boolean cmdCreateOrDeleteAddressBook(String entryName, String fireWallId, boolean control);

    boolean cmdOperateStatisticsBook(String entryName, String firewallId, boolean control);

    boolean cmdInsertOrRemoveParamInAddressBook(String entryName, String param, String addressType, String fireWallId, boolean control);
}
