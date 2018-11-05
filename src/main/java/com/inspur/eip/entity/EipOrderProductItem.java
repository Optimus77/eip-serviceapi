package com.inspur.eip.entity;

import lombok.Data;

@Data
public class EipOrderProductItem {

    private String code;
    private String name;
    private String unit;
    private String value;
    private String type;
}
