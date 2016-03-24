package com.center.domain;

import lombok.Data;

@Data
public class AdminVO {
	private int admin_id;
	private String id;
	private String password;
	private String name;
	private int role_leve;
	private int status;
	private String created;
	private String updated;
}
