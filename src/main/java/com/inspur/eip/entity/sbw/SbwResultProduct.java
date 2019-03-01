package com.inspur.eip.entity.sbw;

import lombok.Data;

import java.util.List;

@Data
public class SbwResultProduct{
      private String productSetStatus;
    private List<SbwProduct> productList;
}
