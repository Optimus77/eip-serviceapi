package com.inspur.eip.entity.Eipv6;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class EipV6AllocateParam implements Serializable {

    @NotBlank(message = "can not be blank.")
    private String eipId;


}
