package com.inspur.eip.util;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class HttpResponse {
    String responseBody;
    Integer statusCode;
}
