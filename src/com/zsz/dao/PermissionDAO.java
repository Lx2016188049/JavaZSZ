package com.zsz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.zsz.dao.utils.JDBCUtils;
import com.zsz.dto.PermissionDTO;

public class PermissionDAO {
	/*T_Permissions
	 *  Id  Description   Name             IsDeleted
	 *  1   查询管理员    AdminUser.Query  0
	 *  2   删除管理员    AdminUser.Delete 0
	 *T_RolePermissions  多对多关系
	 *  Id  RoleId  PermissionId
	 *  1    2       1
	 *  2    2       2
	 *T_Roles
	 *  Id   Name       IsDeleted
	 *  1    房源管理员   0
	 *  2    编辑管理员   0
	 *  
	 *  
	 */

	//获取角色的权限
	public PermissionDTO[] getByRoleId(long roleId)
	{
		List<PermissionDTO> list = new ArrayList<PermissionDTO>();
		ResultSet  rs = null;
		try {
			rs = JDBCUtils.executeQuery(
					"select * from T_Permissions where Id in (select PermissionId from T_RolePermissions where RoleId=?)", roleId);
		    while(rs.next())
		    {
		    	list.add(toDTO(rs));
		    }
		    return list.toArray(new PermissionDTO[list.size()]);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally
		{
			JDBCUtils.closeAll(rs);
		}
	}
	private static PermissionDTO toDTO(ResultSet rs) throws SQLException
	{
		PermissionDTO dto = new PermissionDTO();
		dto.setId(rs.getLong("Id"));
		dto.setDeleted(rs.getBoolean("IsDeleted"));
		dto.setDescription(rs.getString("Description"));
		dto.setName(rs.getString("Name"));
		return dto;
	}

	//给角色roleId增加权限项id permIds
	public void addPermIds(long roleId, long[] permIds)
	{		
		  try {
			  //todo:可以优化，放到同一个connection
				  for(long permId:permIds)
					{				
					  JDBCUtils.executeNonQuery("insert into T_RolePermissions(RoleId,PermissionId) values(?,?)", roleId,permId);
					}
			 } catch (SQLException e) {
				throw new RuntimeException(e);
	         }			
	}

	//更新角色role的权限项：先删除再添加
	public void updatePermIds(long roleId, long[] permIds)
	{
		try
		{
			JDBCUtils.executeNonQuery("delete from t_rolepermissions where RoleId=?", roleId);
			addPermIds(roleId, permIds);
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
		
	}
	public PermissionDTO getByName(String name)
	{
		ResultSet rs = null;
		try
		{
			rs = JDBCUtils.executeQuery("select * from t_permissions where Name=? and IsDeleted=0", name);
			if (rs.next())
			{
				return toDTO(rs);
			} else
			{
				return null;
			}
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		} finally
		{
			JDBCUtils.closeAll(rs);
		}
	}
	public PermissionDTO[] getAll()
	{
		List<PermissionDTO> list = new ArrayList<PermissionDTO>();
		ResultSet rs = null;
		try
		{
			rs = JDBCUtils.executeQuery("select * from t_permissions where  IsDeleted=0");
			while (rs.next())
			{
				list.add(toDTO(rs));
			}
			return list.toArray(new PermissionDTO[list.size()]);
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		} finally
		{
			JDBCUtils.closeAll(rs);
		}
	}
	public PermissionDTO getById(long id)
	{
		ResultSet rs = null;
		try
		{
			rs = JDBCUtils.executeQuery("select * from t_permissions where Id=? and IsDeleted=0", id);
			if (rs.next())
			{
				return toDTO(rs);
			} else
			{
				return null;
			}
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		} finally
		{
			JDBCUtils.closeAll(rs);
		}
	}

}
