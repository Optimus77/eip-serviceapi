package com.inspur.eip.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="eip")
@Getter
@Setter
public class Eip implements Serializable {

    @Id
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @GeneratedValue(generator = "system-uuid")
    @Column(name ="eip_id",nullable = false, insertable = false, updatable = false)
    private String id;

    @Column(name="eip_name")
    private String name;

    @Column(name="elastic_ipv4")
    private String eipIpv4;

    @Column(name="floating_ipv4")
    private String floatingIpv4;

    @Column(name="fixed_ipv4")
    private String fixedIpv4;

    @Column(name="elastic_ipv6")
    private String eipIpv6;

    @Column(name="floating_ipv6")
    private String floatingIpv6;

    @Column(name="fixed_ipv6")
    private String fixedIpv6;

    @Column(name="instance_id")
    private String instanceId;

    @Column(name="instance_type")
    private String instanceType;

    @Column(name="vpc_id")
    private String vpcId;

    @Column(name="bandwidth")
    private String banWidth;

    @Column(name="link_type")
    private String linkType;

    @Column(name="shared_bandwidth_id")
    private String sharedBandWidthId;

    @Column(name="acl_id")
    private String aclId;

    @Column(name="qos_id")
    private String pipId;

    @Column(name="snat_id")
    private String snatId;

    @Column(name="dnat_id")
    private String dnatId;

    @Column(name="dev_id")
    private String devId;

    @Column(name="state",nullable = false)
    private String stat="0";

    @Column(name="create_time" ,nullable = false)
    private Date createTime=new Date();

    @Column(name="update_time")
    private Date updateTime;

}
