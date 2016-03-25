package com.center.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class AdminVO extends SearchVO {
	private int admin_id;
	private String id;
	private String password;
	private String name;
	private int role_level;
	private int status;
	private String created;
	private String updated;
}
