package test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zsz.dao.AdminUserDAO;
import com.zsz.dto.AdminUserDTO;

public class AdminUserDAOTest {

	@Test
	public void testAddAdminUser() {
		AdminUserDAO dao = new AdminUserDAO();
		long Id1 = dao.addAdminUser("李一", "13203703800", "3800", "3800@qq.com", 1L);
		AdminUserDTO getById = dao.getById(Id1);
		AdminUserDTO getByPhone = dao.getByPhoneNum("13203703800");
		Boolean checkLogin = dao.checkLogin("13203703800", "3800");
		assertNotNull(getById);
		assertNotNull(getByPhone);
		assertTrue(checkLogin);
		
	}
	
	@Test
	public void testUpdateAdminUser() {
		AdminUserDAO dao = new AdminUserDAO();
		AdminUserDTO[] getAllByCityId = dao.getAll(1);
		assertNotNull(getAllByCityId);
		AdminUserDTO[] getAll = dao.getAll();
		assertNotNull(getAll);
		dao.updateAdminUser(24, "李一", "3800", "3800@qq.com", 3L);
		AdminUserDTO[] getAllByCityId2 = dao.getAll(3);
		assertNotNull(getAllByCityId2);
		Boolean hasQueryAdminPerm = dao.hasPermission(24, "AdminUser.Query");
		assertTrue(hasQueryAdminPerm);
		
	}
		
}
