package com.zsz.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.zsz.dao.utils.JDBCUtils;
import com.zsz.dto.RoleDTO;

public class RoleDAO {

	public long addnew(String roleName)//新增角色
	{
			try
			{
				return JDBCUtils.executeInsert("insert into T_Roles(Name,IsDeleted) values(?,0)", roleName);
			} catch (SQLException e)
			{
				throw new RuntimeException(e);
			}		
	}
	
	public void update(long roleId,String roleName)
	{
		try
		{
			JDBCUtils.executeNonQuery("Update T_Roles set Name=? where Id=?", roleName,roleId);
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	
	public void markDeleted(long roleId)
	{
		try
		{
			JDBCUtils.executeNonQuery("Update T_Roles set IsDeleted=1 where Id=?", roleId);
		} catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static RoleDTO toDTO(ResultSet rs) throws SQLException
	{
		RoleDTO dto = new RoleDTO();
		dto.setId(rs.getLong("Id"));
		dto.setDeleted(rs.getBoolean("IsDeleted"));
		dto.setName(rs.getString("Name"));
		return dto;
	}
	
	public RoleDTO getById(long id)
	{
		ResultSet rs = null;
		try
		{
			rs = JDBCUtils.executeQuery("select * from t_roles where Id=? and IsDeleted=0", id);
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

	public RoleDTO[] getAll()
	{
		List<RoleDTO> list = new ArrayList<RoleDTO>();
		ResultSet rs = null;
		try
		{
			rs = JDBCUtils.executeQuery("select * from t_roles where  IsDeleted=0");
			while (rs.next())
			{
				list.add(toDTO(rs));
			}
			return list.toArray(new RoleDTO[list.size()]);
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		} finally
		{
			JDBCUtils.closeAll(rs);
		}
	}

	
	/**
	 * 给adminUserId用户增加权限roleIds
	 * 
	 * @param adminUserId
	 * @param roleIds
	 */
	public void addRoleIds(long adminUserId, long[] roleIds)
	{
		
		Connection conn = null;
		try {
			conn = JDBCUtils.getConnection();
			conn.setAutoCommit(false);
			for(long roleId : roleIds)
			{
				JDBCUtils.executeNonQuery(conn, "insert into T_AdminUserRoles(AdminUserId,RoleId) values(?,?)",adminUserId,roleId );
			}
			conn.commit();
		} catch (SQLException e) {
		    JDBCUtils.rollback(conn);
		    throw new RuntimeException(e);
		}finally
		{
			JDBCUtils.closeQuietly(conn);			
		}	
	}
	
	
	/**
	 * 更新adminUserId的权限为roleIds
	 * @param adminUserId
	 * @param roleIds
	 */
	public void updateRoleIds(long adminUserId, long[] roleIds)
	{
	
		Connection conn = null;
		try
		{
			conn = JDBCUtils.getConnection();
			conn.setAutoCommit(false);
			//更新权限，先删再加
			//先删除旧的关系，再重新添加关系，这样就可以规避角色修改等
			JDBCUtils.executeNonQuery(conn,"delete from T_AdminUserRoles where AdminUserId=?", adminUserId);
			for (long roleId : roleIds)
			{
				JDBCUtils.executeNonQuery(conn, "insert into T_AdminUserRoles(AdminUserId,RoleId) values(?,?)",
						adminUserId, roleId);
			}
			conn.commit();
		} catch (SQLException ex)
		{
			JDBCUtils.rollback(conn);
			throw new RuntimeException(ex);
		} finally
		{
			JDBCUtils.closeQuietly(conn);
		}
	}
	
	//获取用户的角色
	public RoleDTO[] getByAdminUserId(long adminUserId)
	{
		List<RoleDTO> list = new ArrayList<RoleDTO>();
		ResultSet rs = null;
		try
		{
			rs = JDBCUtils.executeQuery(
					"select * from t_roles where Id in(select RoleId from t_adminuserroles where AdminUserId=?)",
					adminUserId);
			while (rs.next())
			{
				list.add(toDTO(rs));
			}
			return list.toArray(new RoleDTO[list.size()]);
		} catch (SQLException ex)
		{
			throw new RuntimeException(ex);
		} finally
		{
			JDBCUtils.closeAll(rs);
		}
	}
}
