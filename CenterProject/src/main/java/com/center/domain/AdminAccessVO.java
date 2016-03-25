package com.center.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class AdminAccessVO extends SearchVO {
	private int admin_access_id;
	private int admin_id;
	private String access_ip;
	private String created;
}
