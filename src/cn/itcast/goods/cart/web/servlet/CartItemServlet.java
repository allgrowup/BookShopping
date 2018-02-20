package cn.itcast.goods.cart.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.cart.domain.CartItem;
import cn.itcast.goods.cart.service.CartItemService;
import cn.itcast.goods.user.domain.User;
import cn.itcast.servlet.BaseServlet;

/**
 * Servlet implementation class CartItemServlet
 */
public class CartItemServlet extends BaseServlet {
	
	private CartItemService cartItemService = new CartItemService();
	/**
	 * 通过用户查找购物车
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("sessionUser");
		List<CartItem> cartItems = cartItemService.findByUser(user.getUid());
		request.setAttribute("cartItems", cartItems);
		return "f:/jsps/cart/list.jsp";
	}
	/**
	 * 添加商品到购物车
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String addCartItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		CartItem cartItem = CommonUtils.toBean(request.getParameterMap(), CartItem.class);
		Book book = CommonUtils.toBean(request.getParameterMap(), Book.class);
		User user = (User) request.getSession().getAttribute("sessionUser");
		cartItem.setBook(book);
		cartItem.setUser(user);
		cartItemService.updateQuantity(cartItem);		
		return findByUser(request,response);
	}
	/**
	 * 批量删除
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String bathchDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String cartItemIds = request.getParameter("cartItemIds");
		cartItemService.bathchDelete(cartItemIds);
		return findByUser(request,response);
	}
	/**
	 * 修改书本的数量
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String updateQuantity(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String cartItemId = request.getParameter("cartItemId");
		int quantity = Integer.parseInt(request.getParameter("quantity"));
		cartItemService.updateQuantity(quantity, cartItemId);
	    CartItem cartItem = cartItemService.fingByid(cartItemId);
	    StringBuilder builder = new StringBuilder("{");
	    builder.append("\"quantity\"").append(":").append(cartItem.getQuantity()).append(",");
	    builder.append("\"subtotal\"").append(":").append(cartItem.getSubtotal());
	    builder.append("}");
	    //System.out.println(builder);
	    response.getWriter().println(builder);
		return null;
	}
	/**
	 * 用勾选的条目来生成订单
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String loadCartItems(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String cartItemIds = request.getParameter("cartItemIds");
		double total = Double.parseDouble(request.getParameter("total"));
		List<CartItem> cartitemList = cartItemService.findByCartItemIds(cartItemIds);
		request.setAttribute("cartItems", cartitemList);
		request.setAttribute("total", total);
		request.setAttribute("cartItemIds", cartItemIds);
		return "f:/jsps/cart/showitem.jsp";
	}
	
		
}
