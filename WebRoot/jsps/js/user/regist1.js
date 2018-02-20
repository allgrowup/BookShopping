/*在页面加载完成是调用*/
$(function(){
	$(".labelError").each(function(){
		var labelElement = $(this);
		showError(labelElement);
	});
	$("#submit").hover(
		function(){
			$("#submit").attr("src","/goods/images/regist2.jpg");
		},
		function(){
			$("#submit").attr("src","/goods/images/regist1.jpg");
		}
	);
	/*得到焦点的让错误信息隐藏*/
	$(".input").focus(function(){
		var labelId = $(this).attr("id") + "Error";
		$("#" + labelId).text("");
		showError($("#" + labelId));
	});
	//失去焦点校验
	$(".input").blur(function(){
		var id = $(this).attr("id");
		var funName = "validata" + id.substring(0,1).toUpperCase() + id.substring(1) + "()";
		eval(funName);
	});
	//对表单进行整体校验
	$("#registForm").submit(function(){
		var bool = true;//通过校验
		if(!validataLoginname()){
			 bool = false;
		}
		if(!validataLoginpass()){
			 bool = false;
		}
		if(!validataReloginpass()){
			 bool = false;
		}
		if(!validataEmail()){
			 bool = false;
		}
		if(!validataVerifyCode()){
			 bool = false;
		}
		return bool;
	});
	
});
//验证用户名
function validataLoginname(){
	id = "loginname";
	var value = $("#" + id).val();
	//非空校验
	if(!value){
		$("#" + id + "Error").text("用户名不能为空！");
		showError($("#" + id + "Error"));
		return false;
	}
	//长度校验
	if(value.length < 3 || value.length > 20){
		$("#" + id + "Error").text("用户名要在3到20之间！");
		showError($("#" + id + "Error"));
		return false;
	}
	//是否注册校验
	$.ajax({
		url:"/goods/UserServlet",
		data:{method:"ajaxValidataLoginname",loginname:value},
		type:"POST",
		dataType:"json",
		async:false,//是否异步执行
		cache:false,//缓存
		success:function(result){
			if(!result){//如果用户已经存在
				$("#" + id + "Error").text("用户名已经被注册！");
				showError($("#" + id + "Error"));
				return false;
			}
			
		}
		
	});
	return true;
	
}
//验证密码
function validataLoginpass(){
	id = "loginpass";
	var value = $("#" + id).val();
	//非空校验
	if(!value){
		$("#" + id + "Error").text("密码不能为空！");
		showError($("#" + id + "Error"));
		return false;
	}
	//长度校验
	if(value.length < 3 || value.length > 20){
		$("#" + id + "Error").text("密码要在3到20之间！");
		showError($("#" + id + "Error"));
		return false;
	}
	
	return true;

	
}
//验证确认密码
function validataReloginpass(){
	id = "reloginpass";
	var value = $("#" + id).val();
	//非空校验
	if(!value){
		$("#" + id + "Error").text("确认密码不能为空！");
		showError($("#" + id + "Error"));
		return false;
	}
	//长度校验
	if($("#loginpass").val() != value){
		$("#" + id + "Error").text("确认密码不正确！");
		showError($("#" + id + "Error"));
		return false;
	}
	
	return true;
	
	
}
//验证邮箱
function validataEmail(){
	id = "email";
	var value = $("#" + id).val();
	//非空校验
	if(!value){
		$("#" + id + "Error").text("Email不能为空！");
		showError($("#" + id + "Error"));
		return false;
	}
	//长度校验
	if(!/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/.test(value)){
		$("#" + id + "Error").text("邮箱格式不正确！");
		showError($("#" + id + "Error"));
		return false;
	}
	//是否注册校验
	$.ajax({
		url:"/goods/UserServlet",
		data:{method:"ajaxValidataEmail",email:value},
		type:"POST",
		dataType:"json",
		async:false,//是否异步执行
		cache:false,//缓存
		success:function(result){
			if(!result){//如果用户已经存在
				$("#" + id + "Error").text("邮箱已经被注册！");
				showError($("#" + id + "Error"));
				return false;
			}
			
		}
		
	});
	return true;
	
}
//验证验证码
function validataVerifyCode(){
	id = "verifyCode";
	var value = $("#" + id).val();
	//非空校验
	if(!value){
		$("#" + id + "Error").text("验证码不能为空！");
		showError($("#" + id + "Error"));
		return false;
	}
	//长度校验
	if(value.length != 4){
		$("#" + id + "Error").text("验证码不正确！");
		showError($("#" + id + "Error"));
		return false;
	}
	//是否正确校验
	$.ajax({
		url:"/goods/UserServlet",
		data:{method:"ajaxValidataVerifyCode",verifyCode:value},
		type:"POST",
		dataType:"json",
		async:false,//是否异步执行
		cache:false,//缓存
		success:function(result){
			if(!result){//如果用户已经存在
				$("#" + id + "Error").text("验证码错误！");
				showError($("#" + id + "Error"));
				return false;
			}
			
		}
		
	});
	return true;
	
}




//判断ele元素的文本是不是为空 为空隐藏  不为空显示
function showError(ele){
	var text = ele.text();
	if(!text){
		//为空不显示
		ele.css("display","none");
	}else{
		ele.css("display","");
	}
}

function _change(){
	/*
	 * 找到img元素
	 * 给src重新赋值
	 */
	$("#imgVerifyCode").attr("src","/goods/VerifyCodeServlet?a=" + new Date().getTime());
}




