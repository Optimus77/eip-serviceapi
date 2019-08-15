package com.inspur.eip.entity.bss;

import lombok.Data;

import java.util.List;

/**
 * @Description 日志类计量数据传输格式：to Bss
 * @Author Zerah
 * @Date 2019/7/22 16:04
 **/
@Data
public class FlowAccount2Bss {
    //是否分包： true 是  false:否  如果用户实例列表大于1000条，则需要分包处理
    private String subpackage;
    //包序号，不需要分包，则传1
    private String packageNo;
    //分包数，如果不需要分包，则传1
    private String billCycle;
    //计费周期，天:Day    小时：H    月：M
    private String settleCycle;
    //该账期总条数
    private String count;
    //当前序号
    private String index;
    //用户id
    private String projectId;
    //产品线编码
    private String productLineCode;
    //非必须，存在闲忙时传入true
    private Boolean haveOtherItem =false;

    private List<FlowAccountProductList> productList;
}
