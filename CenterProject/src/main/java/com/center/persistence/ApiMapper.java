package com.center.persistence;

import java.util.List;

import com.center.domain.AdminAccessVO;
import com.center.domain.AdminVO;
import com.center.domain.UserAccessVO;
import com.center.domain.UserVO;

public interface ApiMapper {
	public int insertUser(UserVO user);
	public UserVO selectUser(UserVO user);
	
	public int insertUserAccess(UserAccessVO userAccess);
	public UserAccessVO selectUserAccess(UserAccessVO userAccess);
	public List<UserAccessVO> selectUserAccessList(UserAccessVO userAccess);
	public int countUserAccess();
	
	public int insertAdmin(AdminVO admin);
	public int updateAdmin(AdminVO admin);
	public int deleteAdmin(AdminVO admin);
	public int countAdmin();
	public AdminVO selectAdmin(AdminVO admin);
	public List<AdminVO> selectAdminList(AdminVO admin);
	
	public int insertAdminAccess(AdminAccessVO adminAccess);
	public List<AdminAccessVO> selectAdminAccessList(AdminAccessVO adminAccess);
	public int countAdminAccess();
}
