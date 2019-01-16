package com.inspur.eip.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class EipUpdateParamWrapper implements Serializable {
    private EipUpdateParam   eip;
}
