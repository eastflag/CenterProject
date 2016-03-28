package com.center.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.center.domain.AdminAccessVO;
import com.center.domain.AdminVO;
import com.center.result.Result;
import com.center.result.ResultData;
import com.center.result.ResultDataTotal;
import com.center.service.ApiService;
import com.center.utils.CryptographyPasswordHash;

@RestController
@RequestMapping("/admin/api")
public class AdminController {
	private static Logger logger = LoggerFactory.getLogger(ApiController.class);
	
	@Autowired
	private ApiService apiService;
	
	//admin 관리=====================================================================
	@RequestMapping("/addAdmin")
    public Result addAdmin(@RequestBody AdminVO inAdmin) {
		logger.debug("/addAdmin------------------------------------------------------------");
		
		AdminVO admin = apiService.getAdmin(inAdmin);
		if (admin != null) {
			return new Result(100, "id exists");
		}
		
		try {
			String hashPassword = CryptographyPasswordHash.computePasswordHash(inAdmin.getPassword(), null);
			inAdmin.setPassword(hashPassword);
			long resultCount = apiService.addAdmin(inAdmin);
			if(resultCount > 0) {
				return new Result(0, "success");
			} else {
				return new Result(100, "insert failed");
			}
		} catch (PersistenceException e) {
			return new Result(100, "insert failed");
		} 
	}
	
	@RequestMapping("/modifyAdmin")
    public Result modifyAdmin(@RequestBody AdminVO inAdmin) {
		logger.debug("/modifyAdmin----------------------------------------------------------");
		
		long resultCount = apiService.modifyAdmin(inAdmin);
		if(resultCount > 0) {
			return new Result(0, "success");
		} else {
			return new Result(100, "update failed");
		}
	}
	
	@RequestMapping("/removeAdmin")
    public Result removeAdmin(@RequestBody AdminVO inAdmin) {
		logger.debug("/removeAdmin----------------------------------------------------------");
		
		long resultCount = apiService.removeAdmin(inAdmin);
		if(resultCount > 0) {
			return new Result(0, "success");
		} else {
			return new Result(100, "delete failed");
		}
	}
	
	@RequestMapping("/getAdmin")
    public ResultData<AdminVO> getAdmin(@RequestBody AdminVO inAdmin) {
		logger.debug("/getAdmin--------------------------------------------------");
		AdminVO admin = apiService.getAdmin(inAdmin);
		
		if(admin == null) {
			return new ResultData<AdminVO>(100, "fail", admin);
		} else {
			admin.setPassword("");
			return new ResultData<AdminVO>(0, "success", admin);
		}
	}
	
	@RequestMapping("/getAdminList")
    public ResultDataTotal<List<AdminVO>> getAdminList(@RequestBody AdminVO inAdmin) {
		logger.debug("/getAdminList--------------------------------------------------");
		List<AdminVO> adminList = apiService.getAdminList(inAdmin);
		
		int total = apiService.countAdmin();
		
		return new ResultDataTotal<List<AdminVO>>(0, "success", adminList, total);
	}
	
	//백오피스 접근 정보 가져오기
	@RequestMapping("/getAdminAccessList")
    public ResultData<List<AdminAccessVO>> getAdminAccessList(@RequestBody AdminAccessVO inAdminAccess) {
		logger.debug("/getAdminAccessList--------------------------------------------------");
		List<AdminAccessVO> accessList = apiService.getAdminAccessList(inAdminAccess);
		int total = apiService.countAdminAccess();
		
		return new ResultDataTotal<List<AdminAccessVO>>(0, "success", accessList, total);
	}
}
