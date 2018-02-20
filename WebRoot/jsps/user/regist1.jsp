<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page language="java" import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>注册</title>
<link rel="stylesheet" type="text/css" href="<c:url value='/jsps/css/user/regist1.css'/>">
<script type="text/javascript" src="<c:url value='/jquery/jquery-1.5.1.js' />"></script>
<script type="text/javascript" src="<c:url value='/jsps/js/user/regist1.js' />"></script>
</head>
<body>
	<div class="divBody">
		<div class="divTitle">
		     <span id="spanTitle">新用户注册</span>
		</div>
		<div class="divCenter">
		<form action="<c:url value='/UserServlet' />" method="post" id="registForm">
		<input type="hidden" name="method" value="regist" />
			<table class="tableForm" >
				<tr>
					<td class="tdLabel">用户名：</td>
					<td class="tdInput">
						<input type="text" name="loginname" id="loginname" class="input" value="${userForm.loginname }"/>
					</td>
					<td class="tdError">
					    <label class="labelError" id="loginnameError">${errors.loginname }</label>
					</td>
				</tr>
				<tr>
					<td class="tdLabel">登入密码：</td>
					<td class="tdInput">
						 <input type="password" name="loginpass" id="loginpass" class="input" value="${userForm.loginpass }"/>
					</td>
					<td class="tdError">
						  <label class="labelError" id="loginpassError">${errors.loginpass }</label>
					</td>
				</tr>
				<tr>
					<td class="tdLabel">确认密码：</td>
					<td class="tdInput">
						<input type="password" name="reloginpass" id="reloginpass" class="input" value="${userForm.reloginpass }"/>
					</td>
					<td class="tdError">
						 <label class="labelError" id="reloginpassError">${errors.reloginpass }</label>
					</td>
				</tr>
				<tr>
					<td class="tdLabel">Email：</td>
					<td class="tdInput">
						<input type="text" name="email" id="email" class="input" value="${userForm.email }"/>
					</td>
					<td class="tdError">
						<label class="labelError" id="emailError">${errors.email }</label>
					</td>
				</tr>
				<tr>
					<td class="tdLabel">图形验证码：</td>
					<td class="tdInput">
						<input type="text" name="verifyCode" id="verifyCode" class="input" value="${userForm.verifyCode }"/>
					</td>
					 <td class="tdError">
                         <label class="labelError" id="verifyCodeError">${errors['verifyCode'] }</label>
                     </td>
				</tr>
				<tr>
					<td></td>
					<td>
						<div id="divVerifyCode"><img id="imgVerifyCode" src="<c:url value='/VerifyCodeServlet' />" /></div>
					</td>
					<td><a href="javascript: _change()">换一张</a></td>
				</tr>
				<tr>
					<td></td>
					<td>
						<input type="image" src ="<c:url value='/images/regist1.jpg'/>" id="submit" />
					</td>
					<td></td>
				</tr>
			</table>
			</form>
		</div>
	</div>

</body>
</html>








