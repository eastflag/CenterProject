package com.center.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StaticController {
	@RequestMapping("/admin")
	public void addUser(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.sendRedirect("/admin/index.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
