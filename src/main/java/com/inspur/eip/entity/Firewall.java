package com.inspur.eip.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
public class Firewall {
	@Id
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@GeneratedValue(generator = "system-uuid")
	@Column(name ="firewall id",nullable = false, insertable = false, updatable = false)
    String id;

    @Column(name="port")
	String ip;

    @Column(name="port")
	private String port;

    @Column(name="user")
	private String user;

    @Column(name="passwd")
	private String passwd;

    @Column(name="devtype")
	private String devtype;

    @Column(name="param1")
	private String param1;

    @Column(name="param2")
	private String param2;

    @Column(name="param3")
	private String param3;
}
