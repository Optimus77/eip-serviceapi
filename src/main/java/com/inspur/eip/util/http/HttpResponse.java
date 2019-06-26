package com.inspur.eip.util.http;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class HttpResponse {
    String responseBody;
    Integer statusCode;
}
