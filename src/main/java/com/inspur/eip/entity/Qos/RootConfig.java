package com.inspur.eip.entity.Qos;

import lombok.Data;

import java.util.ArrayList;

@Data
public class RootConfig {
    private String id;

    private ArrayList<RuleConfig> rule;
}
