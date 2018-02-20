package cn.itcast.goods.admin.order;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.goods.order.domain.Order;
import cn.itcast.goods.order.service.OrderService;
import cn.itcast.goods.page.PageBean;
import cn.itcast.servlet.BaseServlet;


public class AdminOrderServlet extends BaseServlet {


	private OrderService orderService = new OrderService();
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
	public String findAllOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1、获取当前页
    	int pagecurrent = getPagecurrent(request);
    	//2、获取url
    	String url = getUrl(request);
    	PageBean<Order> pageBean = orderService.findAll(pagecurrent);
    	pageBean.setPagecurrent(pagecurrent);
    	pageBean.setUrl(url);
    	request.setAttribute("pageBean", pageBean);
    	return "f:/adminjsps/admin/order/list.jsp";
		
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
		return "f:/adminjsps/admin/order/desc.jsp";
	
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
			request.setAttribute("msg", "订单状态不对！！");
			return "f:/adminjsps/msg.jsp";
		}
		orderService.updateStatus(oid, 5);
		request.setAttribute("msg", "订单已取消，请向我们建议你宝贵的意见");
		return "f:/adminjsps/msg.jsp";
	}

	
	/**
	 * 发货
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String deliver(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String oid = request.getParameter("oid");
		int status = orderService.findStatusByOid(oid);
		if(status != 2){
			request.setAttribute("msg", "订单状态不对！！");
			return "f:/adminjsps/msg.jsp";
		}
		orderService.updateStatus(oid, 3);
		request.setAttribute("msg", "已经发货，请注意查看物流！");
		return "f:/adminjsps/msg.jsp";
	}
	/**
	 * 按状态查询
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1、获取当前页
    	int pagecurrent = getPagecurrent(request);
    	//2、获取url
    	String url = getUrl(request);
    	int status = Integer.parseInt(request.getParameter("status"));
    	PageBean<Order> pageBean = orderService.findByStatus(status, pagecurrent);
    	pageBean.setPagecurrent(pagecurrent);
    	pageBean.setUrl(url);
    	request.setAttribute("pageBean", pageBean);
    	return "f:/adminjsps/admin/order/list.jsp";
	}
	
		
}
