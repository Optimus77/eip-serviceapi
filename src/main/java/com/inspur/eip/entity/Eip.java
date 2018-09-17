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
    @Column(name ="eip_id",nullable = false, insertable = false, updatable = false,length = 36)
    private String id;//the eip id ,generate by hibernate uuid strategy

    @Column(name="eip_name" ,length = 128)
    private String name;//the eip name

    @Column(name="elastic_ipv4",columnDefinition="char(16)")
    private String eipIpv4;//the public ipv4 address on internet

    @Column(name="floating_ipv4",columnDefinition="char(16)")
    private String floatingIpv4;//the floating ipv4 address on openstack

    @Column(name="fixed_ipv4",columnDefinition="char(16)")
    private String fixedIpv4;//the fixed ipv4 address on openstack

    @Column(name="elastic_ipv6",columnDefinition="char(40)")
    private String eipIpv6;//the  public ipv6 address on internet

    @Column(name="floating_ipv6",columnDefinition="char(40)")
    private String floatingIpv6;//the floating ipv6 address on openstack

    @Column(name="fixed_ipv6",columnDefinition="char(40)")
    private String fixedIpv6;//the fixed ipv6 address on openstack

    @Column(name="floating_ipv4_id",columnDefinition="varchar(36)")
    private String floatingIpv4Id;

    @Column(name="floating_ipv6_id",columnDefinition="varchar(36)")
    private String floatingIpv6Id;

    @Column(name="firewall_id",columnDefinition="varchar(36)")
    private String firewallId;

    @Column(name="instance_id",columnDefinition="varchar(36)")
    private String instanceId;//the instance id of  the fixed ip address banded

    @Column(name="instance_type",columnDefinition="char(1)")
    private String instanceType;//the instance type  1：ecs 2：cps 3：slb

    @Column(name="vpc_id",columnDefinition="varchar(36)")
    private String vpcId;//the id  of  vpc that ecs instance  belong to

    @Column(name="bandwidth",columnDefinition="varchar(10)")
    private String banWidth;//the eip bandWidth

    @Column(name="link_type",columnDefinition="char(1)")
    private String linkType;//eip link type 1.Unicom 2.China Mobile 3 China Telecom

    @Column(name="shared_bandwidth_id",columnDefinition="varchar(36)")
    private String sharedBandWidthId;//the id of sharedbandwidth

    @Column(name="acl_id",columnDefinition="varchar(36)")
    private String aclId;

    @Column(name="state",nullable = false,columnDefinition="char(1)")
    private String stat="0";//state 0  1 2 3

    @Column(name="create_time" ,nullable = false)
    private Date createTime=new Date();

    @Column(name="update_time")
    private Date updateTime;

}
