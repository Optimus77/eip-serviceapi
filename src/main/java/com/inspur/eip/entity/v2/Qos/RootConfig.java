package com.inspur.eip.entity.v2.Qos;

import lombok.Data;

import java.util.ArrayList;

@Data
public class RootConfig {
    private String id;

    private ArrayList<RuleConfig> rule;
}
