package cn.itcast.goods.user.service;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;
import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.user.dao.UserDao;
import cn.itcast.goods.user.domain.User;
import cn.itcast.goods.user.utils.MailUtils;


/*
 * 业务逻辑层
 */
public class UserService {

	private UserDao userDao = new UserDao();
	
	/**
	 * 修改密码
	 * @param uid
	 * @param oldPassword
	 * @param newPassword
	 * @throws UserException 
	 */
	public void updatePassword(String uid,String oldPassword,String newPassword) throws UserException{
		try {
			boolean b = userDao.findByLoginpass(uid, oldPassword);
			if(!b){
				throw new UserException("旧密码错误！");
			}
			userDao.updatapassword(uid, newPassword);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	
	}
	

	
	/**
	 * 登录功能
	 * @param user
	 * @return
	 */
	public User login(User user) {
		try {
			return userDao.findByLoginnameAndLoginpass(user.getLoginname(), user.getLoginpass());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	
	/**
	 * 对激活码进行验证
	 * @param code
	 * @throws UserException
	 */
	public void activation(String code) throws UserException{
		try {
			User user = userDao.findByCode(code);
			if(user == null){
				throw new UserException("无效激活码！");
			}
			if(user.getStatus() == 1){
				throw new UserException("已经激活过，不能二次激活！");
			}
			userDao.changeStatus(1, user.getUid());
		} catch (SQLException e) {
			throw new UserException(e);
		}
	}
	//是否用户已经注册
	public boolean ajaxValidataLoginname(String loginname){
		try {
			return userDao.ajaxValidataLoginname(loginname);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	//是否邮箱已经注册
	public boolean ajaxValidataEmail(String email){
		try {
			return userDao.ajaxValidataEmail(email);
		} catch (SQLException e) {
				throw new RuntimeException(e);
		}
	}
	/**
	 * 添加user数据 和发送邮件
	 * @param user
	 */
	public void regist(User user){
		//补全user数据
		user.setUid(CommonUtils.uuid());
		user.setStatus(0);
		user.setActivationCode(CommonUtils.uuid() + CommonUtils.uuid());
		//添加用户到数据库
		userDao.add(user);
		
		//加载配置文件
		Properties prop = new Properties();
		try {
			prop.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		//发送邮件
		//邮件主题
		String subject = prop.getProperty("subject");
		//邮件内容
		String content = MessageFormat.format(prop.getProperty("content"), user.getActivationCode());
		//发件人
		String ownEmailAccount = prop.getProperty("from");
	    //发件人密码
		String ownEmailPassword = prop.getProperty("password");
		//收件人的emal
		String receiveMailAccount = user.getEmail();
	    MailUtils.send(receiveMailAccount, subject, content, ownEmailAccount, ownEmailPassword);
	}

}
