package com.inspur.eip.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="eipPool")
@Getter
@Setter
public class EipPool implements Serializable {

    @Id
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @GeneratedValue(generator = "system-uuid")
    @Column(name ="eip_id",nullable = false, insertable = false, updatable = false)
    private String id;

    @Column(name="dev_id")
    private String devId;

    @Column(name="ip")
    private String ip;

}
