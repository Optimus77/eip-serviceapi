package com.inspur.eip.entity;

import lombok.Data;


@Data
public class WebSocketEntity {

    private String userName;

    private String handlerName;

    private String instanceId;

    private String instanceStatus;

    private String operateType;

    private String messageType;

    private String message;

}
