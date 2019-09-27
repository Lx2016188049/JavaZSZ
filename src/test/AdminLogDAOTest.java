package test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zsz.dao.AdminLogDAO;

public class AdminLogDAOTest {

	@Test
	public void testAddNew() {
		AdminLogDAO dao = new AdminLogDAO();
		dao.addnew(1, "啦啦啦啦啦");
	    
		
	}

}
