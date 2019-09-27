package com.zsz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.zsz.dao.utils.JDBCUtils;
import com.zsz.dto.SettingDTO;

public class SettingDAO {

	/*
	 * SettingDAO ：
			public void setValue(String name, String value)//设置配置项name的值为value
			public String getValue(String name)//获取配置项name的值
			public SettingDTO[] getAll()
	 */
	
	
	
	//获取配置项name的value值:
	public String getValue(String name,String defaultValue) //传进去一个默认的Value值
	{
		try {
			 //取出数据库中Name=name的Value值
			String value = (String)JDBCUtils.querySingle("select Value from T_Settings where Name=?", name);
		    if(value==null)  //若数据库中的Value值为Null，就返回默认的传进去的defaultValue值
		    {
		    	return defaultValue;
		    }
		    else             //若数据库中Value的值不为null,就返回从数据库中取出的Value值
		    {
		    	return value;
		    }
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	//获取配置项name的value值:
	public String getValue(String name)
	{
		return getValue(name,null); //调用的是public String getValue(String name, String defaultValue)方法
                                //若是能查询到Name=name的Value就返回数据库中的Value值，否则返回一个默认的传进去的Null值。
	}
	
	
	//设置配置项name的值为value
		public void setValue(String name, String value)
		{
			try {
				String oldValue = getValue(name, null); //获取数据库中的value值或者是null值
				if(oldValue == null)
				{
					JDBCUtils.executeNonQuery("insert into T_Settings(Name,Value) values(?,?)", name, value);
				}
				else
				{					
						JDBCUtils.executeNonQuery("update T_Settings set Value=? where Name=?", name, value);
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	
	
	
	
		public SettingDTO[] getAll()
		{
			List<SettingDTO> list = new ArrayList<SettingDTO>();
			ResultSet rs = null;
			try {
				 rs = JDBCUtils.executeQuery("select * from T_Settings");
				while(rs.next())
				{
					list.add(toDTO(rs));
				}
				return list.toArray(new SettingDTO[list.size()]);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			finally
			{
				JDBCUtils.closeAll(rs);
			}
		}
		
		
		private static SettingDTO toDTO(ResultSet rs) throws SQLException
		{
			SettingDTO setDto = new SettingDTO();
			setDto.setId(rs.getLong("Id"));
			setDto.setName(rs.getString("Name"));
			setDto.setValue(rs.getString("Value"));
			return setDto;			
		}
	
	
	
	
	
}
