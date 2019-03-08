package com.inspur.eip.entity;

import lombok.Data;

import java.util.List;

@Data
public class EipOrderResultProduct {

    private String productSetStatus;
    private String duration;
    private String durationUnit = "M";
    private List<EipOrderProduct> productList;

}
