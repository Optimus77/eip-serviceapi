package com.inspur.eip.entity;

import lombok.Data;

import java.util.List;

@Data
public class OrderSoftDown {
    private String region;
    private String flowId;

    private List<SoftDownInstance> instanceList;

}
