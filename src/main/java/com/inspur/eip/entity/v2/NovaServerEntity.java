package com.inspur.eip.entity.v2;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class NovaServerEntity {
    public static final long serialVersionUID = 1L;
    private String id;
    private String name;
}

