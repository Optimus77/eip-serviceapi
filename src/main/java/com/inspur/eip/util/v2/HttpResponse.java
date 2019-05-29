package com.inspur.eip.util.v2;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class HttpResponse {
    String responseBody;
    Integer statusCode;
}
