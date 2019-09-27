package com.zsz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.zsz.dao.utils.JDBCUtils;
import com.zsz.dto.IdNameDTO;

public class IdNameDAO {

	/*
	 * 数据库字段：Id TypeName Name IsDeleted
	 *       数据：1   户型    一室一厅  0
	 * //类别名，名字
			public long addIdName(String typeName, String name)
			public IdNameDTO getById(long id)
			//获取类别下的IdName（比如所有的民族）
			public IdNameDTO[] getAll(String typeName)
	*/
	//添加一条数据,并返回自增字段id值
	public long addIdName(String typeName, String name)
	{
		try {
			return JDBCUtils.executeInsert("insert into T_IdNames values(TypeName,Name,0)", typeName,name);			 
		} catch (SQLException e) {
			throw new RuntimeException(e);	
		}
	}
	
	//获得Id=id的一条数据
	public IdNameDTO getById(long id)
	{
		ResultSet rs = null;
		try {
			rs = JDBCUtils.executeQuery("select * from T_IdNames where IsDeleted=0 and Id=?", id);
			if(rs.next())
			{
				/*IdNameDTO dto = new IdNameDTO();
				dto.setId(rs.getLong("Id"));
				dto.setName(rs.getString("Name"));
				dto.setTypeName(rs.getString("TypeName"));
				return dto;	*/
				return toDTO(rs);
			}
			else
			{
				return null;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JDBCUtils.closeAll(rs);
		}		
	}
	
	
	//获取TypeName=typeName类别下的IdName表的好几条数据
	public IdNameDTO[] getAll(String typeName)
	{
		List<IdNameDTO> list = new ArrayList<IdNameDTO>();
		ResultSet rs = null;
		try {
			rs = JDBCUtils.executeQuery("select * from T_IdNames where IsDeleted=0 and TypeName=?", typeName);
			while(rs.next())
			{
				/*IdNameDTO dto = new IdNameDTO();
				dto.setId(rs.getLong("Id"));
				dto.setName(rs.getString("Name"));
				dto.setTypeName(rs.getString("TypeName"));
				list.add(dto);	*/
				
				IdNameDTO dto = toDTO(rs);
				list.add(dto);
			}
			return list.toArray(new IdNameDTO[list.size()]);
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			JDBCUtils.closeAll(rs);
		}					
	}
	
	
	private static IdNameDTO toDTO(ResultSet rs) throws SQLException
	{
		IdNameDTO dto = new IdNameDTO();
		dto.setId(rs.getLong("Id"));
		dto.setName(rs.getString("Name"));
		dto.setTypeName(rs.getString("TypeName"));
		return dto;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
