package com.zsz.dao;

import java.sql.SQLException;

import com.zsz.dao.utils.JDBCUtils;

public class AdminLogDAO {
	
	/*插入一条日志：adminUserId为操作用户id，message为消息
    public void addnew(long adminUserId, String message)
    
	 * //数据库中的字段是：什么人在什么时间干了什么事（时间不需要插入，只需用函数生成）
	 */
	 public void addnew(long adminUserId, String message) 
	 {
		try {
			JDBCUtils.executeNonQuery("insert into T_Adminlogs(AdminUserId,CreateDateTime,Message) values(?,now(),?)", adminUserId,message);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} 
	 }
}













