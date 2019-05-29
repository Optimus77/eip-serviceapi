package com.inspur.eip.entity.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MethodReturn<T> {

    int httpCode;
    String innerCode;
    String message;
    private T eip;
}