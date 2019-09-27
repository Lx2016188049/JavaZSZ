package com.zsz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.zsz.dao.utils.JDBCUtils;
import com.zsz.dto.RegionDTO;

public class RegionDAO {

	/*
	 * Id  Name  CityId  IsDeleted
	 * 1    东城区 2      0
	 * 2    西城区 2      0
	 * RegionDAO：
			public RegionDTO getById(long id)
			public RegionDTO[] getAll(long cityId)//获取城市下的区域
	 */
	//获取RegionDTO表下Id=id的一条记录
	public RegionDTO getById(long id)
	{
		ResultSet rs= null;
		try {
			rs = JDBCUtils.executeQuery("Select * from T_Regions where Id=? and IsDeleted=0", id);
			if(rs.next())
			{
				/*
				RegionDTO dto = new RegionDTO();
				dto.setDeleted(rs.getBoolean("IsDeleted"));
				dto.setId(rs.getLong("Id"));
				dto.setName(rs.getString("Name"));
				dto.setCityId(rs.getLong("CityId"));
				return dto;*/
				
				return toDTO(rs);
			}
			else
			{
				return null;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		finally
		{
			JDBCUtils.closeAll(rs);
		}		
	}
	
	private static RegionDTO toDTO(ResultSet rs) throws SQLException
	{
		RegionDTO dto = new RegionDTO();
		dto.setDeleted(rs.getBoolean("IsDeleted"));
		dto.setId(rs.getLong("Id"));
		dto.setName(rs.getString("Name"));
		dto.setCityId(rs.getLong("CityId"));
		return dto;
	}
	
	public RegionDTO[] getAll(long cityId)//获取城市下的区域
	{
		List<RegionDTO> list = new ArrayList<RegionDTO>();
		ResultSet rs= null;
		try {
			rs = JDBCUtils.executeQuery("Select * from T_Regions where IsDeleted=0 and CityId=?", cityId);
			while(rs.next())
			{	
			  list.add(toDTO(rs));
			}
			return list.toArray(new RegionDTO[list.size()]);
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		finally
		{
			JDBCUtils.closeAll(rs);
		}		
	}
}
