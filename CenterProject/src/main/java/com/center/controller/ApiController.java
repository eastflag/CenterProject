package com.center.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.center.Constant;
import com.center.domain.AdminAccessVO;
import com.center.domain.AdminVO;
import com.center.domain.UserVO;
import com.center.result.Result;
import com.center.result.ResultData;
import com.center.service.ApiService;
import com.center.utils.CommonUtil;
import com.center.utils.CryptographyPasswordHash;

@RestController
@RequestMapping("/api")
public class ApiController {
	private static Logger logger = LoggerFactory.getLogger(ApiController.class);
	
	@Autowired
	private ApiService apiService;
	
	//사용자 회원가입
	@RequestMapping("/addUser")
	public Result addUser(@RequestBody UserVO inUser) {
		//중복 체크
		UserVO user = apiService.getUser(inUser);
		if (user != null) {
			return new Result(100, "id exist"); //향후 ajax로 변경
		}
		
		String hashPassword = CryptographyPasswordHash.computePasswordHash(inUser.getPassword(), null);
		inUser.setPassword(hashPassword);
		int resultcount = apiService.addUser(inUser);
		
		if(resultcount > 0) {
			return new Result(0, "success");
		} else {
			return new Result(100, "fail");
		}
	}
	
	//사용자 로그인
	@RequestMapping("/userLogin")
	public Result login(@RequestBody UserVO inUser) {
		UserVO user = apiService.getUser(inUser);
		if (user == null) {
			return new Result(100, "login fail");
		}

		logger.debug("inUser:" + inUser.getPassword() + ", user:" + user.getPassword());
		boolean result = CryptographyPasswordHash.verifyPassword(inUser.getPassword(), user.getPassword());
		
		if(result) {
			return new Result(0, "success");
		} else {
			return new Result(100, "login fail");
		}
	}
	
	//관리자 페이지 로그인
	@RequestMapping("/adminLogin")
	public Result adminLogin(@RequestBody AdminVO inAdmin, HttpServletRequest request) {
		logger.debug("/api/adminLogin--------------------------------------------------------");
		
		AdminVO admin = apiService.getAdmin(inAdmin);
		if(admin == null) {
			return new ResultData<AdminVO>(100, "아이디가 존재하지 않습니다.", null);
		}

		boolean result = CryptographyPasswordHash.verifyPassword(inAdmin.getPassword(), admin.getPassword());
		if(result) {
			//토큰 발행
			String token;
			try {
				token = CommonUtil.createJWT(admin.getId(), admin.getId(), String.valueOf(admin.getRole_level()), Constant.SESSION_TIMEOUT);
				admin.setToken(token);
				
				//접속 정보 기록
				AdminAccessVO accessVO = new AdminAccessVO();
				accessVO.setAdmin_id(admin.getAdmin_id());
				accessVO.setAccess_ip(request.getRemoteAddr());
				apiService.addAdminAccess(accessVO);
				
				return new ResultData<AdminVO>(0, "success", admin);
			} catch (IOException e) {
				e.printStackTrace();
				return new ResultData<AdminVO>(200, "서버오류가 발생하였습니다. 잠시후에 시도하세요.", null);
			}
		} else {
			return new ResultData<AdminVO>(200, "아이디나 패스워드를 확인하세요.", null);
		}
	}
}
