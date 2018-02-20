package cn.itcast.goods.user.dao;
/*
 * 用户模块持久层
 */

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.goods.user.domain.User;
import cn.itcast.jdbc.TxQueryRunner;

public class UserDao {
	
	/**
	 * 按用户id和密码查询
	 * @param loginname 用户名
	 * @param loginpass 密码
	 * @return
	 * @throws SQLException 
	 */
	public boolean findByLoginpass(String uid, String loginpass) throws SQLException {
		String sql = "select count(*) from t_user where uid=? and loginpass=?";
		Number number = (Number) qr.query(sql, new ScalarHandler(),uid,loginpass);
		 return number.intValue() > 0;
	}
	/**
	 * 修改密码
	 * @param uid
	 * @param loginpass
	 * @throws SQLException
	 */
	public void updatapassword(String uid,String loginpass) throws SQLException{
		String sql = "update t_user set loginpass=? where uid=?";
		qr.update(sql,loginpass,uid);
	}
	
	
	
	
	
	//简化对数据库的操作的类
	private QueryRunner qr = new TxQueryRunner();
	/**
	 * 按用户名和密码查询
	 * @param loginname
	 * @param loginpass
	 * @return
	 * @throws SQLException 
	 */
	public User findByLoginnameAndLoginpass(String loginname, String loginpass) throws SQLException {
		String sql = "select * from t_user where loginname=? and loginpass=?";
		return qr.query(sql, new BeanHandler<User>(User.class), loginname, loginpass);
	}
	
	/**
	 * 根据激活码查询用户
	 * @param code
	 * @return
	 * @throws SQLException
	 */
	public User findByCode(String code) throws SQLException{
		String sql = "select * from t_user where activationCode = ?";
		return qr.query(sql, new BeanHandler<User>(User.class),code);
	}
	/**
	 * 根据id修改用户的状态
	 * @param status
	 * @param uid
	 * @throws SQLException
	 */
	public void changeStatus(int status,String uid) throws SQLException{
		String sql = "update t_user set status = ? where uid = ?";
		qr.update(sql,status,uid);
	}
	/**
	 * 是否用户已经注册
	 * @param loginname
	 * @return
	 * @throws SQLException
	 */
	public boolean ajaxValidataLoginname(String loginname) throws SQLException{
		String sql = "select count(1) from t_user where loginname=?";
		Number number = (Number) qr.query(sql, new ScalarHandler(),loginname);
		return number.intValue() == 0;
	}
	/**
	 * 是否邮箱已经注册
	 * @param email
	 * @return
	 * @throws SQLException
	 */
	public boolean ajaxValidataEmail(String email) throws SQLException{
		String sql = "select count(1) from t_user where email=?";
		Number number = (Number) qr.query(sql, new ScalarHandler(),email);
		return number.intValue() == 0;
	}
	/**
	 * 添加用户信息到数据库
	 * @param user
	 */
	public void add(User user){
		String sql = "insert into t_user values(?,?,?,?,?,?)";
		Object[] params = {user.getUid(),user.getLoginname(),user.getLoginpass(),user.getEmail(),user.getStatus(),user.getActivationCode()};
		try {
			qr.update(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
}
