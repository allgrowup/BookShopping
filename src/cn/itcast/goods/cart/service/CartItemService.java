package cn.itcast.goods.cart.service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.cart.dao.CartItemDao;
import cn.itcast.goods.cart.domain.CartItem;

public class CartItemService {

	private CartItemDao cartItemDao = new CartItemDao();
	/**
	 * 批量删除
	 * @param cartItemIds
	 */
	public void bathchDelete(String cartItemIds){
		try {
			cartItemDao.batchDelete(cartItemIds);
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}
	/**
	 * 根据id查询
	 * @param cartItemId
	 * @return
	 */
	public CartItem fingByid(String cartItemId){
		try {
			return cartItemDao.fingByid(cartItemId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * 查询多个条目的信息，多表查询
	 * @param cartItemIds
	 * @return
	 * @throws SQLException
	 */
	public List<CartItem> findByCartItemIds(String cartItemIds){
		try {
			return cartItemDao.findByCartItemIds(cartItemIds);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 通过用户查询用户的购物车
	 * @param uid
	 * @return
	 */
	public List<CartItem> findByUser(String uid){
		try {
			return cartItemDao.findByUser(uid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 添加条目
	 * @param cartItem
	 */
	public void updateQuantity(CartItem cartItem){
		try {
			//通过数据库查询出的条目
			CartItem _cartItem = cartItemDao.findByUidAndCartItemId(cartItem.getUser().getUid(), cartItem.getBook().getBid());
			if(_cartItem == null){
				cartItem.setCartItemId(CommonUtils.uuid());
				cartItemDao.addCartItem(cartItem);
			}else{
				int quantity = cartItem.getQuantity() + _cartItem.getQuantity();
				cartItemDao.updateQuantity(quantity, _cartItem.getCartItemId());
			}
		} catch (SQLException e) {
			
		}
		
	}
	public void updateQuantity(int quantity,String cartItemId){
		try {
			cartItemDao.updateQuantity(quantity, cartItemId);
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}
	
	
}
