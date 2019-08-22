package com.inspur.eip.entity.sbw;

import com.inspur.iam.adapter.annotation.ContextKey;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="sbw")
@Data
@Builder
public class Sbw implements Serializable {
    @Id
    @Column(name ="id",nullable = false, insertable = false, updatable = false)
    private String id;

    private String sbwName;

    //计费方式
    private String billType;

    //仅做续费与停服判断，并无包年包月时常意义
    private String duration;

    private Integer bandWidth;

    private String region;

    @Column(name="created_time" ,nullable = false)
    private Date createdTime ;

    @Column(name="updated_time" ,nullable = false)
    private Date updatedTime ;

    //project id : uuid
    @ContextKey("accountId")
    private String projectId;

    private int isDelete;

    private String pipeId;

    private String status ;

    //username :login name
    private String userName;

    @ContextKey("creator")
    private String userId;

    private String ipType;
}
