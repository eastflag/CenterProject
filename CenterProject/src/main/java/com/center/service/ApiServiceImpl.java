package com.center.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.center.domain.AdminAccessVO;
import com.center.domain.AdminVO;
import com.center.domain.UserAccessVO;
import com.center.domain.UserVO;
import com.center.persistence.ApiMapper;

@Service("apiService")
public class ApiServiceImpl implements ApiService {
	
	@Autowired
	private ApiMapper apiMapper;

	@Override
	public int addUser(UserVO user) {
		return apiMapper.insertUser(user);
	}

	@Override
	public UserVO getUser(UserVO user) {
		return apiMapper.selectUser(user);
	}

	@Override
	public int addUserAccess(UserAccessVO userAccess) {
		return apiMapper.insertUserAccess(userAccess);
	}

	@Override
	public UserAccessVO getUserAccess(UserAccessVO userAccess) {
		return apiMapper.selectUserAccess(userAccess);
	}

	@Override
	public List<UserAccessVO> getUserAccessList(UserAccessVO userAccess) {
		return apiMapper.selectUserAccessList(userAccess);
	}

	@Override
	public int countUserAccess() {
		return apiMapper.countUserAccess();
	}

	@Override
	public int addAdmin(AdminVO admin) {
		return apiMapper.insertAdmin(admin);
	}

	@Override
	public int modifyAdmin(AdminVO admin) {
		return apiMapper.updateAdmin(admin);
	}

	@Override
	public int removeAdmin(AdminVO admin) {
		return apiMapper.deleteAdmin(admin);
	}

	@Override
	public int countAdmin() {
		return apiMapper.countAdmin();
	}

	@Override
	public AdminVO getAdmin(AdminVO admin) {
		return apiMapper.selectAdmin(admin);
	}

	@Override
	public List<AdminVO> getAdminList(AdminVO admin) {
		return apiMapper.selectAdminList(admin);
	}

	@Override
	public int addAdminAccess(AdminAccessVO adminAccess) {
		return apiMapper.insertAdminAccess(adminAccess);
	}

	@Override
	public List<AdminAccessVO> getAdminAccessList(AdminAccessVO adminAccess) {
		return apiMapper.selectAdminAccessList(adminAccess);
	}

	@Override
	public int countAdminAccess() {
		return apiMapper.countAdminAccess();
	}

}
