package cn.itcast.goods.order.web.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.cart.domain.CartItem;
import cn.itcast.goods.cart.service.CartItemService;
import cn.itcast.goods.order.domain.Order;
import cn.itcast.goods.order.domain.OrderItem;
import cn.itcast.goods.order.service.OrderService;
import cn.itcast.goods.page.PageBean;
import cn.itcast.goods.user.domain.User;
import cn.itcast.servlet.BaseServlet;



public class OrderServlet extends BaseServlet {
	
	private OrderService orderService = new OrderService();
	private CartItemService cartitemService = new CartItemService();
	/**
	 * 得到当前页
	 * @param request
	 * @return
	 */
	private int getPagecurrent(HttpServletRequest request){
		int pagecurrent = 1;
		String param = request.getParameter("pc");
		if(param != null && !param.trim().isEmpty()){
			try{
				pagecurrent = Integer.parseInt(param);
			}catch(RuntimeException e){}
		}
		return pagecurrent;
	}
	
	/**
	 * 得到url
	 * @param request
	 * @return
	 */
	private String getUrl(HttpServletRequest request){
		String uri = request.getRequestURI();//比如/goods/userServlet
		
		String params = null;
		try {
			 params = URLDecoder.decode(request.getQueryString(),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = uri + "?" + params;
		//String url = uri + "?" + request.getQueryString();
		int index = url.lastIndexOf("&pc=");
		if(index != -1){
			url = url.substring(0,index);
		}
		return url;
	}
	
	
	/**
	 * 查看订单
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1、获取当前页
    	int pagecurrent = getPagecurrent(request);
    	//2、获取url
    	String url = getUrl(request);
    	
    	User user = (User) request.getSession().getAttribute("sessionUser");
    	if(user == null){
    		return "f:/jsps/user/login.jsp";
    	}
    	PageBean<Order> pageBean = orderService.findByUid(user.getUid(), pagecurrent);
    	pageBean.setPagecurrent(pagecurrent);
    	pageBean.setUrl(url);
    	request.setAttribute("pageBean", pageBean);
    	return "f:/jsps/order/list.jsp";
		
	}
	/**
	 * 生成订单
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String createOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String cartItemIds = request.getParameter("cartItemIds");
		List<CartItem> cartitemList = cartitemService.findByCartItemIds(cartItemIds);
		String address = request.getParameter("address");
		//创建order对象
		Order order = new Order();
		order.setOid(CommonUtils.uuid());
		order.setOrdertime(String.format("%tF %<tT", new Date()));
		order.setAddress(address);
		order.setStatus(1);
		User user = (User) request.getSession().getAttribute("sessionUser");
		order.setOwner(user);
		BigDecimal total = new BigDecimal("0");
		for(CartItem cartitem :cartitemList){
			total = total.add(new BigDecimal(cartitem.getSubtotal() + ""));
		}
		order.setTotal(total.doubleValue());
		
		List<OrderItem> orderItemlist = new ArrayList<OrderItem>();
		for(CartItem cartitem :cartitemList){
			OrderItem orderitem = new OrderItem();
			orderitem.setOrderItemId(CommonUtils.uuid());
			orderitem.setQuantity(cartitem.getQuantity());
			orderitem.setSubtotal(cartitem.getSubtotal());
			orderitem.setBook(cartitem.getBook());
			orderitem.setBname(cartitem.getBook().getBname());
			orderitem.setCurrPrice(cartitem.getBook().getCurrPrice());
			orderitem.setImage_b(cartitem.getBook().getImage_b());
			orderitem.setOrder(order);
			orderItemlist.add(orderitem);
		}
		order.setOrderItemList(orderItemlist);
		orderService.add(order);
		request.setAttribute("order", order);
		cartitemService.bathchDelete(cartItemIds);
		return "f:/jsps/order/ordersucc.jsp";
	}
	/**
	 * 查看订单详情
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String orderdesc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String oid = request.getParameter("oid");
		String btn = request.getParameter("btn");
		Order order = orderService.findByOid(oid);
		request.setAttribute("order", order);
		request.setAttribute("btn", btn);
		return "f:/jsps/order/desc.jsp";
	
	}

	/**
	 * 取消订单
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String cancle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String oid = request.getParameter("oid");
		int status = orderService.findStatusByOid(oid);
		if(status != 1){
			request.setAttribute("code", "error");
			request.setAttribute("msg", "订单状态不对！！");
			return "f:/jsps/msg.jsp";
		}
		orderService.updateStatus(oid, 5);
		request.setAttribute("code", "success");
		request.setAttribute("msg", "订单已取消，请向我们建议你宝贵的意见");
		return "f:/jsps/msg.jsp";
	}
	/**
	 * 确认收货
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String canfirm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String oid = request.getParameter("oid");
		int status = orderService.findStatusByOid(oid);
		if(status != 3){
			request.setAttribute("code", "error");
			request.setAttribute("msg", "订单状态不对！！");
			return "f:/jsps/msg.jsp";
		}
		orderService.updateStatus(oid, 4);
		request.setAttribute("code", "success");
		request.setAttribute("msg", "交易完成，感谢你对我们的支持");
		return "f:/jsps/msg.jsp";
	}
	
	/**
	 * 到支付页面
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String paymentPre(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String oid = request.getParameter("oid");
		Order order = orderService.findByOid(oid);
		request.setAttribute("order", order);
		return "f:/jsps/order/pay.jsp";
	}
	
	
	
	
	/**
	 * 到银行页面
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String payment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("payment.properties"));
		/*
		 * 1. 准备13个参数
		 */
		String p0_Cmd = "Buy";//业务类型，固定值Buy
		String p1_MerId = props.getProperty("p1_MerId");//商号编码，在易宝的唯一标识
		String p2_Order = request.getParameter("oid");//订单编码
		String p3_Amt = "0.01";//支付金额
		String p4_Cur = "CNY";//交易币种，固定值CNY
		String p5_Pid = "";//商品名称
		String p6_Pcat = "";//商品种类
		String p7_Pdesc = "";//商品描述
		String p8_Url = props.getProperty("p8_Url");//在支付成功后，易宝会访问这个地址。
		String p9_SAF = "";//送货地址
		String pa_MP = "";//扩展信息
		String pd_FrpId = request.getParameter("yh");//支付通道
		String pr_NeedResponse = "1";//应答机制，固定值1
		
		/*
		 * 2. 计算hmac
		 * 需要13个参数
		 * 需要keyValue
		 * 需要加密算法
		 */
		String keyValue = props.getProperty("keyValue");
		String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt,
				p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP,
				pd_FrpId, pr_NeedResponse, keyValue);
		
		/*
		 * 3. 重定向到易宝的支付网关
		 */
		StringBuilder sb = new StringBuilder("https://www.yeepay.com/app-merchant-proxy/node");
		sb.append("?").append("p0_Cmd=").append(p0_Cmd);
		sb.append("&").append("p1_MerId=").append(p1_MerId);
		sb.append("&").append("p2_Order=").append(p2_Order);
		sb.append("&").append("p3_Amt=").append(p3_Amt);
		sb.append("&").append("p4_Cur=").append(p4_Cur);
		sb.append("&").append("p5_Pid=").append(p5_Pid);
		sb.append("&").append("p6_Pcat=").append(p6_Pcat);
		sb.append("&").append("p7_Pdesc=").append(p7_Pdesc);
		sb.append("&").append("p8_Url=").append(p8_Url);
		sb.append("&").append("p9_SAF=").append(p9_SAF);
		sb.append("&").append("pa_MP=").append(pa_MP);
		sb.append("&").append("pd_FrpId=").append(pd_FrpId);
		sb.append("&").append("pr_NeedResponse=").append(pr_NeedResponse);
		sb.append("&").append("hmac=").append(hmac);
		
		response.sendRedirect(sb.toString());
		return null;
	
	}
	
}
