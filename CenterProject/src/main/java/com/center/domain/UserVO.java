package com.center.domain;

import lombok.Data;

@Data
public class UserVO {
	private int user_id;
	private String id;
	private String password;
	private String name;
	private String email;
	private String mdn;
	private int status;
	private String created;
	private String updated;
}
