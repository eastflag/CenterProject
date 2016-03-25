package com.center.service;

import java.util.List;

import com.center.domain.AdminAccessVO;
import com.center.domain.AdminVO;
import com.center.domain.UserAccessVO;
import com.center.domain.UserVO;

public interface ApiService {
	public int addUser(UserVO user);
	public UserVO getUser(UserVO user);
	
	public int addUserAccess(UserAccessVO userAccess);
	public UserAccessVO getUserAccess(UserAccessVO userAccess);
	public List<UserAccessVO> getUserAccessList(UserAccessVO userAccess);
	public int countUserAccess();
	
	public int addAdmin(AdminVO admin);
	public int modifyAdmin(AdminVO admin);
	public int removeAdmin(AdminVO admin);
	public int countAdmin();
	public AdminVO getAdmin(AdminVO admin);
	public List<AdminVO> getAdminList(AdminVO admin);
	
	public int addAdminAccess(AdminAccessVO adminAccess);
	public List<AdminAccessVO> getAdminAccessList(AdminAccessVO adminAccess);
	public int countAdminAccess();
}
