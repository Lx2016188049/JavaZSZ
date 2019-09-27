package com.zsz.dao.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;







public class JDBCUtils {
	//dbcp2 数据库连接池
	//静态代码块在类第一次被使用的时候执行一次，在构造函数执行之前执行
	//final 修饰局部变量表示这个变量只能被赋值一次
	//成员变量也可以是 static。成员变量可以实现全局变量或者常量（final static）的效果
	final static BasicDataSource ds;
	static
	{
		Properties prop = new Properties();//配置文件
		try {
			prop.load(JDBCUtils.class.getResourceAsStream("/dbcp2.properties")); //JDBCUtils.class 获得类的对象（反射）
			
		    String driverClassName = prop.getProperty("driverClassName");
		    String url = prop.getProperty("url");
		    String username = prop.getProperty("username");
		    String password = prop.getProperty("password");
		    ds = new BasicDataSource();
		    ds.setDriverClassName(driverClassName);
		    ds.setUrl(url);
		    ds.setUsername(username);
		    ds.setPassword(password);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
	}
	
	//AutoCloseable:接口
	public static void closeQuietly(AutoCloseable c)
	{
		if(c!= null)
		{
			try {
				c.close();
			} catch (Exception e) {
				//安静的关闭
			}
		}
	}
	
	public static void closeAll(ResultSet rs)
	{		
		if(rs == null)
		{
			return;
		}
		try {
			Statement stmt = rs.getStatement();
			Connection conn = stmt.getConnection();
			closeQuietly(rs);
			closeQuietly(stmt);
			closeQuietly(conn);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}		
	}
	
	public static Connection getConnection() throws SQLException
	{
		return ds.getConnection();
	}
	
		
	//执行非查询代码
	public static void executeNonQuery(Connection conn,String sql,Object... params) throws SQLException
	{
		
		PreparedStatement ps=null;
		try {
			 
			ps = conn.prepareStatement(sql); //PreparedStatement:表示预编译的带占位符的SQL语句
			/*
			 * ps = conn.prepareStaement("Insert into T_Persons(Name,Age,Gender) values(?,?,?)");
				ps.setString(1,"李仙")
				ps.setInt(2,21);
				set***的序号从1开始
				若不能确定数据是什么类型用ps.setObject也可以
				参数化查询：安全；效率高（SQL预编译）
			 */
			for(int i=0; i<params.length;i++) 
			{
				ps.setObject(i+1, params[i]);
			}
			ps.execute();//执行
		} finally
		{
			closeQuietly(ps);
			//closeQuietly(conn); //并不是真的关闭了连接，而是告诉连接池：哎，我用完了，还给你。把连接还给连接池。
		}		
	}
	

	// 执行非查询代码
	public static void executeNonQuery(String sql, Object... params) throws SQLException {
		Connection conn = ds.getConnection();
		try {
			executeNonQuery(conn, sql, params);
		} finally {
			closeQuietly(conn);
		}
	}
	
	public static ResultSet executeQuery(Connection conn,String sql,Object... params) throws SQLException
	{
		PreparedStatement ps=null;
		try {
			 
			ps = conn.prepareStatement(sql); //PreparedStatement:表示预编译的带占位符的SQL语句
			
			for(int i=0; i<params.length;i++) 
			{
				ps.setObject(i+1, params[i]);
			}
			return ps.executeQuery();//执行查询
		}catch(SQLException ex) //没有用finaly,是因为：若不发生异常，让调用者自己关闭，若发生异常，则该关的关掉
		{
			closeQuietly(ps);
			//closeQuietly(conn); //并不是真的关闭了连接，而是告诉连接池：哎，我用完了，还给你。把连接还给连接池。
		    throw ex; //重新抛出异常
		}		
	}
	
	
	public static ResultSet executeQuery(String sql, Object... params) throws SQLException {
		Connection conn = ds.getConnection();
		try {
			return executeQuery(conn, sql, params);
		} catch (SQLException ex) {
			closeQuietly(conn);
			throw ex;
		}
	}
	

	
	
	/*
	 * 专门用于执行插入数据的方法，可以获得自增字段的值
	 */
	//获得刚插入数据的主键的功能(插入加查询)
	public static long executeInsert(Connection conn,String sql,Object... params) throws SQLException
	{
		
		PreparedStatement psInsert=null; //插入
		PreparedStatement psLastInsertId = null; //查询
		ResultSet rs = null;
		try {
			 
			psInsert = conn.prepareStatement(sql); //PreparedStatement:表示预编译的带占位符的SQL语句
			
			for(int i=0; i<params.length;i++) 
			{
				psInsert.setObject(i+1, params[i]);
			}
			psInsert.execute();//执行插入
			
			//同一个连接中执行SELECT LAST_INSERT_ID()
			psLastInsertId= conn.prepareStatement("select last_insert_id()");//其他方法：select @@identity；
			rs = psLastInsertId.executeQuery(); //执行查询
			if(rs.next())
			{
				return rs.getLong(1); //数据库中columnIndex从1开始
			}
			else
			{
				throw new RuntimeException("没有找到刚插入记录的自增字段的值");
			}
		} finally
		{
			closeQuietly(rs);
			closeQuietly(psLastInsertId);
			closeQuietly(psInsert);
			//closeQuietly(conn); //并不是真的关闭了连接，而是告诉连接池：哎，我用完了，还给你。把连接还给连接池。
		}		
	}
	
	
	/**
	 * 专门用于执行插入数据的方法，可以获得自增字段的值
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public static long executeInsert(String sql, Object... params) throws SQLException {
		Connection conn = ds.getConnection();
		try {
			return executeInsert(conn, sql, params);
		} finally {
			closeQuietly(conn);
		}
	}
	
	
	
	
	/*
	 * 执行查询，并且返回结果集 中第一行，第一列的值，如果没有值，则返回null
	 * 一般用于select count（*）from t  (因为其返回只有一行一列)
	 */
	public static Object querySingle(Connection conn,String sql,Object... params) throws SQLException 
	{		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			for(int i =0;i<params.length;i++)
			{
				ps.setObject(i+1,params[i]);
			}
			rs = ps.executeQuery();
			if(rs.next())
			{
				return rs.getObject(1);
			}
			else
			{
				return null;
			}
		} catch (SQLException e) {
			closeQuietly(rs);
			closeQuietly(ps);
			throw e;
		}			
	}
	
	
	/**
	 * 执行查询，并且返回结果集中第一行、第一列的值，如果没有值，则返回null selec count(*) from t
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public static Object querySingle(String sql, Object... params) throws SQLException {
		Connection conn = ds.getConnection();
		try
		{
			return querySingle(conn, sql, params);
		}
		finally
		{
			closeQuietly(conn);
		}
	}
	
	
	//回滚
	public static void rollback(Connection conn)
	{
		if(conn != null)
		{
			try {
				conn.rollback();
			} catch (SQLException e) {
							}
		}
	}
	
	public static void main(String[] args) {
		try {
			/*ResultSet rs = executeQuery("select * from T_IdNames");
			while(rs.next())
			{
				System.out.println(rs.getString("Name"));
			}
			JDBCUtils.closeAll(rs);
			
			
			executeNonQuery("insert into T_IdNames(TypeName,Name,IsDeleted) values(?,?,0)", "家具","沙发");
		*/
			
			long id = executeInsert("insert into T_IdNames(TypeName,Name,IsDeleted) values(?,?,0)", "家具","椅子");
			System.out.println(id);
			
			Long obj = (Long)querySingle("select count(*) from T_IdNames"); //Object显示转换为Long，再拆箱为long
			if(obj==null)
			{
				System.out.println("没找到");
			}
			else
			{
				System.out.println(obj);
			}
			
		    
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
