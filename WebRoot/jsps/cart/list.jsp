<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>cartlist.jsp</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script src="<c:url value='/jquery/jquery-1.5.1.js'/>"></script>
	<script src="<c:url value='/js/round.js'/>"></script>
	
	<link rel="stylesheet" type="text/css" href="<c:url value='/jsps/css/cart/list.css'/>">
<script type="text/javascript">
//文档加载完成时调用
$(function(){
	showTotal();
	$("#selectAll").click(function(){
		var bool = $("#selectAll").attr("checked");
		setCheckBox(bool);
	    setjiesuan(bool);
		
	});
	$(":checkbox[name=checkboxBtn]").click(function(){
		var all = $(":checkbox[name=checkboxBtn]").length;
		var select = $(":checkbox[name=checkboxBtn][checked=true]").length;
		if(all == select){
			var bool = $("#selectAll").attr("checked",true);
			setjiesuan(true);
		}else if(select == 0){
			var bool = $("#selectAll").attr("checked",false);
			setjiesuan(false);
		}else{
			var bool = $("#selectAll").attr("checked",false);
			setjiesuan(true);
		}
		showTotal();
		
	});
	$(".jian").click(function(){
		var id = $(this).attr("id").substring(0,32);
		var quantityvalue = $("#" + id + "Quantity").val();
		if(quantityvalue == 1){
			if(confirm("您真的要删除吗？")){
				location="/goods/CartItemServlet?method=bathchDelete&cartItemIds=" + id;
			}
		}else{
			sendUpdateQuantity(id,Number(quantityvalue) - 1);
		}
	});
	$(".jia").click(function(){
		var id = $(this).attr("id").substring(0,32);
		var quantityvalue = $("#" + id + "Quantity").val();
		sendUpdateQuantity(id,Number(quantityvalue) + 1);
	});
	
	
});

function sendUpdateQuantity(id,quantityvalue){
	$.ajax({
		async:false,
		cache:false,
		url:"/goods/CartItemServlet",
		data:{method:"updateQuantity",cartItemId:id,quantity:quantityvalue},
		type:"POST",
		dataType:"json",
		success:function(result){
			$("#" + id + "Quantity").val(result.quantity);
			$("#" + id + "Subtotal").text(result.subtotal);
			showTotal();
			
		}
	});
	
	
}

//通一个复选款设置
function setCheckBox(bool){
	$(":checkbox[name=checkboxBtn]").attr("checked",bool);
}
//设置结算按钮
function setjiesuan(bool){
	if(bool){
		$("#jiesuan").removeClass("kill").addClass("jiesuan");
		$("#jiesuan").unbind("click");//消除所有click事件
	}else{
		$("#jiesuan").removeClass("jiesuan").addClass("kill");
		$("#jiesuan").click(function(){return false;});
	}	
	
}
function loadCartItems(){
	var cartItemIdArray = new Array();
	$(":checkbox[name=checkboxBtn][checked=true]").each(function(){
		cartItemIdArray.push($(this).val());
	});
	$("#cartItemIds").val(cartItemIdArray.toString());
	$("#formTotal").val($("#total").text());
	$("#jiesuanform").submit();
	
}


//计算总计
function showTotal(){
	
	var totalAll = 0; 
	//找到所有被选中的复选框
	$(":checkbox[name=checkboxBtn][checked=true]").each(function(){
		var id = $(this).val();
		var subtotal = $("#" + id + "Subtotal").text();
	    totalAll += Number(subtotal); 
	});
	$("#total").text(round(totalAll,2));
}
//批量删除
function bathchDelete(){
	var array = new Array();
	$(":checkbox[name=checkboxBtn][checked=true]").each(function(){
		array.push($(this).val());
	});
	location="/goods/CartItemServlet?method=bathchDelete&cartItemIds=" + array;
	
}



</script>
  </head>
  <body>


	
	

<c:choose>
	<c:when test="${empty cartItems}">
	<table width="95%" align="center" cellpadding="0" cellspacing="0">
		<tr>
			<td align="right">
				<img align="top" src="<c:url value='/images/icon_empty.png'/>"/>
			</td>
			<td>
				<span class="spanEmpty">您的购物车中暂时没有商品</span>
			</td>
		</tr>
	</table>  
</c:when>


<c:otherwise>
<table width="95%" align="center" cellpadding="0" cellspacing="0">
	<tr align="center" bgcolor="#efeae5">
		<td align="left" width="50px">
			<input type="checkbox" id="selectAll" checked="checked"/><label for="selectAll">全选</label>
		</td>
		<td colspan="2">商品名称</td>
		<td>单价</td>
		<td>数量</td>
		<td>小计</td>
		<td>操作</td>
	</tr>



<c:forEach items="${cartItems }" var="cartItem">
	<tr align="center">
		<td align="left">
			<input value="${cartItem.cartItemId }" type="checkbox" name="checkboxBtn" checked="checked"/>
		</td>
		<td align="left" width="70px">
			<a class="linkImage" href="<c:url value='/jsps/book/desc.jsp'/>"><img border="0" width="54" align="top" src="<c:url value='${cartItem.book.image_b }'/>"/></a>
		</td>
		<td align="left" width="400px">
		    <a href="<c:url value='/jsps/book/desc.jsp'/>"><span>${cartItem.book.bname }</span></a>
		</td>
		<td><span>&yen;<span class="currPrice" id="12345CurrPrice">${cartItem.book.currPrice }</span></span></td>
		<td>
			<a class="jian" id="${cartItem.cartItemId }Jian"></a><input class="quantity" readonly="readonly" id="${cartItem.cartItemId }Quantity" type="text" value="${cartItem.quantity }"/><a class="jia" id="${cartItem.cartItemId }Jia"></a>
		</td>
		<td width="100px">
			<span class="price_n">&yen;<span class="subTotal" id="${cartItem.cartItemId }Subtotal">${cartItem.subtotal }</span></span>
		</td>
		<td>
			<a href="<c:url value='/CartItemServlet?method=bathchDelete&cartItemIds=${cartItem.cartItemId }'/>">删除</a>
		</td>
	</tr>

</c:forEach>






	<tr>
		<td colspan="4" class="tdBatchDelete">
			<a href="javascript:bathchDelete()">批量删除</a>
		</td>
		<td colspan="3" align="right" class="tdTotal">
			<span>总计：</span><span class="price_t">&yen;<span id="total"></span></span>
		</td>
	</tr>
	<tr>
		<td colspan="7" align="right">
			<a href="javascript:loadCartItems()" id="jiesuan" class="jiesuan"></a>
		</td>
	</tr>
</table>
	<form id="jiesuanform" action="<c:url value='/CartItemServlet'/>" method="post">
		<input type="hidden" name="cartItemIds" id="cartItemIds"/>
		<input type="hidden" name="total" id="formTotal"/>
		<input type="hidden" name="method" value="loadCartItems"/>
	</form>
	</c:otherwise>
</c:choose>

  </body>
</html>
