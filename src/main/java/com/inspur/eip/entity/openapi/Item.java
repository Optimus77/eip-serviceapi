package com.inspur.eip.entity.openapi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Item {
    private String code;
    private String name;
    private String unit;
    private String value;
    private String type;

    public Item(String code, String value) {
        this.code = code;
        this.value = value;
    }
}
