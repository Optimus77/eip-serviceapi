package com.inspur.eip.entity.bss;

import lombok.Data;

import java.util.List;

/**
 * @Description 产品线列表
 * @Author Zerah
 * @Date 2019/7/22 16:39
 **/
@Data
public class FlowAccountProductList {

    private String region;
    //    可用区
    private String availableZone;
    //    产品类型
    private String productTypeCode;
    //    实例Id
    private String instanceId;
    //实例名称
    private String instanceName;
    //产品详情列表
    private List<OrderProductItem> itemList;
    //非必需， 忙时计量数据
    private List<OrderProductItem> itemListOther;
    //    按月累计用量计费时传
    private List<OrderProductItem> itemListMthSum;
}
