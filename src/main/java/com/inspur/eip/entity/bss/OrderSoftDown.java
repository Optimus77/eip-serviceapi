package com.inspur.eip.entity.bss;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
public class OrderSoftDown {
    private String region;
    private String flowId;

    private List<SoftDownInstance> instanceList;

}
