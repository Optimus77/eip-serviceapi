package com.inspur.eip.entity.bss;

import lombok.Data;

import java.util.List;

@Data
public class OrderResultProduct {

    private String productSetStatus;
    private List<OrderProduct> productList;

}
