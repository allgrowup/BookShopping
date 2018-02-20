package cn.itcast.goods.user.web.servlet;



import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.user.domain.User;
import cn.itcast.goods.user.service.UserException;
import cn.itcast.goods.user.service.UserService;
import cn.itcast.servlet.BaseServlet;

/**
 * 用户web层
 */
public class UserServlet extends BaseServlet {
	private UserService userService = new UserService();
	/**
	 * 验证用户名是否注册
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String ajaxValidataLoginname(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String loginname = request.getParameter("loginname");
		boolean b = userService.ajaxValidataLoginname(loginname);
		response.getWriter().println(b);
		return null;
	}
	/**
	 * 对email是否注册
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String ajaxValidataEmail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("email");
		boolean b = userService.ajaxValidataEmail(email);
		response.getWriter().println(b);
		return null;
	}
	/**
	 * 对验证码校验
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String ajaxValidataVerifyCode(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String verifyCode = request.getParameter("verifyCode");
		String realVerifyCode = (String) request.getSession().getAttribute("vCode");
		boolean b = realVerifyCode.equalsIgnoreCase(verifyCode);
		response.getWriter().println(b);
		return null;
	}

	/**
	 * 注册用户
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String regist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//将表单数据分装到user对象中
		User userForm = CommonUtils.toBean(request.getParameterMap(), User.class);
		
		//对user对象中的数据进行校验
		Map<String,String> errors = new HashMap<String,String>();
		errors = validataRegist(userForm,request.getSession());
		//说明有错误
		if(errors.size() > 0){
			request.setAttribute("userForm", userForm);
			request.setAttribute("errors", errors);
			return "f:/jsps/user/regist1.jsp";
		}
		//将user对象添加到数据库中
		userService.regist(userForm);
		request.setAttribute("code", "success");
		request.setAttribute("msg", "注册成功，请马上到邮箱激活！");
		return "f:/jsps/msg.jsp";
	}
	/**
	 * 对user对象中的数据进行验证
	 * @param userForm  得到的表单信息
	 * @param session
	 * @return
	 */
	private Map<String,String> validataRegist(User userForm, HttpSession session) {
		Map<String,String> errors = new HashMap<String,String>();
		String loginname = userForm.getLoginname();
		/*
		 * 对用户名进行校验
		 */
		if(loginname==null || loginname.trim().isEmpty()){
			errors.put("loginname", "用户名不能为空！");
		}else if(!userService.ajaxValidataLoginname(loginname)){
			errors.put("loginname", "用户名已经注册！");
		}else if(loginname.length() < 3 || loginname.length() > 20){
			errors.put("loginname", "用户名要在3到20之间！");
		}
		/*
		 * 对用户密码进行校验
		 */
		String loginpass = userForm.getLoginpass();
		if(loginpass==null || loginpass.trim().isEmpty()){
			errors.put("loginpass", "密码不能为空！");
		}else if(loginpass.length() < 3 || loginpass.length() > 20){
			errors.put("loginpass", "密码要在3到20之间！");
		}
		/*
		 * 对用户确认密码进行校验
		 */
		String reloginpass = userForm.getReloginpass();
		if(reloginpass==null || reloginpass.trim().isEmpty()){
			errors.put("loginpass", "确认密码不能为空！");
		}else if(!reloginpass.equals(loginpass)){
			errors.put("reloginpass", "确认密码不一致！");
		}
		/*
		 * 对email进行校验
		 */
		String email = userForm.getEmail();
		if(email==null || email.trim().isEmpty()){
			errors.put(email, "email不能为空！");
		}else if(!userService.ajaxValidataEmail(email)){
			errors.put("email", "email已经注册！");
		}else if(!email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$")){
			errors.put("email", "email格式不正确！");
		}
		/*
		 * 对验证码进行校验
		 */
		String verifyCode = userForm.getVerifyCode();
		String realverifyCode = (String) session.getAttribute("vCode");
		if(verifyCode==null || verifyCode.trim().isEmpty()){
			errors.put("verifyCode", "验证码不能为空！");
		}else if(!verifyCode.equalsIgnoreCase(realverifyCode)){
			errors.put("verifyCode", "验证码不正确！");
		}
		return errors;
		
	}
	/**
	 * 激活功能
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String activation(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String code = request.getParameter("activationCode");
		try {
			userService.activation(code);
			request.setAttribute("code", "success");
			request.setAttribute("msg", "恭喜激活成功，请登入");
		} catch (UserException e) {
	        String error = e.getMessage();
	        request.setAttribute("msg", error);
	        request.setAttribute("code", "error");
		}
		return "f:/jsps/msg.jsp";
	}
	/**
	 * 登入功能和校验
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    //把数据封装到userform中
		User userForm = CommonUtils.toBean(request.getParameterMap(), User.class);
	    //对表单数据进行校验
		Map<String,String> errors = validataEnter(userForm,request.getSession());
		if(errors.size()>0){
			request.setAttribute("errors", errors);
			request.setAttribute("user", userForm);
			return "f:/jsps/user/login.jsp";
		}
		User user = userService.login(userForm);
		if(user == null){
			request.setAttribute("error","用户名或密码不正确！");
			request.setAttribute("user", userForm);
			return "f:/jsps/user/login.jsp";
		}else{
			//用户未激活
			if(user.getStatus() == 0){
				request.setAttribute("error", "用户未激活，请到邮箱激活！");
				//保存数据到user为了会写数据
				request.setAttribute("user", userForm);
				return "f:/jsps/user/login.jsp";
			}else{
				request.getSession().setAttribute("sessionUser", user);
				String loginname = user.getLoginname();
				loginname = URLEncoder.encode(loginname, "utf-8");
				Cookie cookie = new Cookie("loginname",loginname);
				cookie.setMaxAge(60*60*24*10);//保存10天
				response.addCookie(cookie);
				return "r:/index.jsp";
			}	
		}
	}
	/**
	 * 对登入的user对象中的数据进行验证
	 * @param userForm
	 * @param session
	 * @return
	 */
	private Map<String,String> validataEnter(User userForm, HttpSession session) {
		Map<String,String> errors = new HashMap<String,String>();
		String loginname = userForm.getLoginname();
		/*
		 * 对用户名进行校验
		 */
		if(loginname==null || loginname.trim().isEmpty()){
			errors.put("loginname", "用户名不能为空！");
		}else if(loginname.length() < 3 || loginname.length() > 20){
			errors.put("loginname", "用户名要在3到20之间！");
		}
		/*
		 * 对用户密码进行校验
		 */
		String loginpass = userForm.getLoginpass();
		if(loginpass==null || loginpass.trim().isEmpty()){
			errors.put("loginpass", "密码不能为空！");
		}else if(loginpass.length() < 3 || loginpass.length() > 20){
			errors.put("loginpass", "密码要在3到20之间！");
		}
		/*
		 * 对验证码进行校验
		 */
		String verifyCode = userForm.getVerifyCode();
		String realverifyCode = (String) session.getAttribute("vCode");
		if(verifyCode==null || verifyCode.trim().isEmpty()){
			errors.put("verifyCode", "验证码不能为空！");
		}else if(!verifyCode.equalsIgnoreCase(realverifyCode)){
			errors.put("verifyCode", "验证码不正确！");
		}
		return errors;
		
	}
	/**
	 * 修改密码
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String updatePassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User userForm = CommonUtils.toBean(request.getParameterMap(), User.class);
		User user = (User) request.getSession().getAttribute("sessionUser");
		if(user == null){
			return "f:/jsps/user/login.jsp";
		}
		try {
			userService.updatePassword(user.getUid(), userForm.getLoginpass(), userForm.getNewpass());
			request.setAttribute("code", "success");
			request.setAttribute("msg", "恭喜修改成功！");
			return "f:/jsps/msg.jsp";
		} catch (UserException e) {
			request.setAttribute("mag",e.getMessage());
			return "f:/jsps/user/pwd.jsp";
		}
	}
	
	
	/**
	 * 退出
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String quit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getSession().invalidate();
		return "r:/jsps/user/login.jsp";
	}
}
