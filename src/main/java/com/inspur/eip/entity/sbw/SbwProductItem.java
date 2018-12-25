package com.inspur.eip.entity.sbw;

import lombok.Data;

import java.io.Serializable;

@Data
public class SbwProductItem{
    private String code;
    private String name;
    private String unit;
    private String value;
    private String type;
}
