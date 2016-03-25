package com.center.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class UserAccessVO extends SearchVO {
	private int user_access_id;
	private int user_id;
	private int access_os;
	private int access_type;
	private String created;
}
