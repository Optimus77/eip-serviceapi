package com.inspur.eip.entity.sbw;

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
    @Column(name ="sbw_id",nullable = false, insertable = false, updatable = false)
    private String sbwId;

    private String sbwName;

    //计费方式
    private String billType;

    //仅做续费与停服判断，并无包年包月时常意义
    private String duration;

    private Integer bandWidth;

    private String region;

    @Column(name="create_time" ,nullable = false)
    private Date createTime ;

    @Column(name="update_time" ,nullable = false)
    private Date updateTime ;

    //project id : uuid
    private String projectId;

    private int isDelete;

    private String pipeId;

    private String status ;

    //username :login name
    private String projectName;
}
