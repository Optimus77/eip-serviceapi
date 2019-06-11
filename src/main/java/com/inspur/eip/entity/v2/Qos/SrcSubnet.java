package com.inspur.eip.entity.v2.Qos;

import lombok.Data;

import java.io.Serializable;
@Data
public class SrcSubnet implements Serializable {

    private String ip;

    private Integer netMask;
}
