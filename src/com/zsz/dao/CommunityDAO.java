package com.zsz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.zsz.dao.utils.JDBCUtils;
import com.zsz.dto.CommunityDTO;

public class CommunityDAO {

  /*
   * 数据库字段：
   *   Id Name RegionId IsDeleted  Location              Traffic  BuiltYear
   *    1 将情院 3       0         学院路与月泉路交叉口  自驾车   2003
   */
	//获取区域regionId下的所有小区
	public CommunityDTO[] getByRegionId(long regionId)
	{
		List<CommunityDTO> list = new ArrayList<CommunityDTO>();
		ResultSet rs = null;
		try {
			rs = JDBCUtils.executeQuery("select * from T_Communities where Regioned=?", regionId);
			while(rs.next())
			{
				list.add(toDTO(rs));
			}
			return list.toArray(new CommunityDTO[list.size()]);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}finally
		{
			JDBCUtils.closeAll(rs);
		}	
	}
	private static CommunityDTO toDTO(ResultSet rs) throws SQLException
	{
		CommunityDTO community = new CommunityDTO();
		community.setDeleted(rs.getBoolean("IsDeleted"));
		community.setId(rs.getLong("Id"));
		community.setName(rs.getString("Name"));
		community.setRegionId(rs.getLong("RegionId"));
		community.setLocation(rs.getString("Location"));
		community.setBuiltYear((Integer)rs.getObject("BuiltYear"));
		community.setTraffic(rs.getString("Traffic"));
		return community;
	}
	
}
