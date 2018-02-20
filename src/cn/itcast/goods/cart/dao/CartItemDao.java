package cn.itcast.goods.cart.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.cart.domain.CartItem;
import cn.itcast.goods.user.domain.User;
import cn.itcast.jdbc.TxQueryRunner;

public class CartItemDao {
	
	QueryRunner qr = new TxQueryRunner();
	/**
	 * where子句
	 * @param len
	 * @return
	 */
	private String whereSql(int len){
		StringBuilder buffer = new StringBuilder(" where cartItemId in (");
		for(int i = 0;i < len;i++){
			buffer.append("?");
			if(i < len - 1){
				buffer.append(",");
			}
		}
		buffer.append(")");
		return buffer.toString();
	}
	
	/**
	 * 批量删除
	 * @param cartItemIds
	 * @throws SQLException
	 */
	public void batchDelete(String cartItemIds) throws SQLException{
		Object[] cartArray = cartItemIds.split(",");
		String whereSql = whereSql(cartArray.length);
		String sql = "delete from t_cartitem" + whereSql;
		qr.update(sql,cartArray);
	}
	
	/**
	 * 查询多个条目的信息，多表查询
	 * @param cartItemIds
	 * @return
	 * @throws SQLException
	 */
	public List<CartItem> findByCartItemIds(String cartItemIds) throws SQLException{
		Object[] cartArray = cartItemIds.split(",");
		String whereSql = whereSql(cartArray.length);
		String sql = "select * from t_cartitem c,t_book b" + whereSql + " and c.bid = b.bid";
		List<Map<String,Object>> maplist = qr.query(sql, new MapListHandler(),cartArray);
		List<CartItem> cartItemlist = toListCartItem(maplist);
		return cartItemlist;
	}
	
	
	/**
	 * 通过用户id和条目id来查询订单是否存在
	 * @param uid
	 * @param cartItemId
	 * @return
	 * @throws SQLException
	 */
	public CartItem findByUidAndCartItemId(String uid,String bid) throws SQLException{
		String sql = "select * from t_cartitem where uid=? and bid=?";
		Map<String,Object> map = qr.query(sql, new MapHandler(),uid,bid);
		return toCartItem(map);
	}
	/**
	 * 根据id多表查询
	 * @param cartItemId
	 * @return
	 * @throws SQLException
	 */
	public CartItem fingByid(String cartItemId) throws SQLException{
		String sql = "select * from t_cartitem c,t_book b where c.bid = b.bid and cartItemId = ?";
		Map<String,Object> map = qr.query(sql, new MapHandler(),cartItemId);
		return toCartItem(map);
	}
	
	
	/**
	 * 更新条目的数量
	 * @param quantity
	 * @param cartItemId
	 * @throws SQLException
	 */
	public void updateQuantity(int quantity,String cartItemId) throws SQLException{
		String sql = "update t_cartItem set quantity = ? where cartItemId = ?";
		qr.update(sql,quantity,cartItemId);
	}
	/**
	 * 添加条目
	 * @param cartItem
	 * @throws SQLException
	 */
	public void addCartItem(CartItem cartItem) throws SQLException{
		String sql = "insert into t_cartitem(cartItemId,quantity,bid,uid) values(?,?,?,?)";
		Object[] params = {cartItem.getCartItemId(),cartItem.getQuantity(),cartItem.getBook().getBid(),cartItem.getUser().getUid()};
		qr.update(sql, params);
	}
	
	
	/**
	 * 把map映射到CartItem
	 * @param map
	 * @return
	 */
	private CartItem toCartItem(Map<String,Object> map){
		if(map == null || map.size() == 0){
			return null;
		}
		CartItem cartItem = CommonUtils.toBean(map, CartItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		User user = CommonUtils.toBean(map, User.class);
		cartItem.setBook(book);
		cartItem.setUser(user);
		return cartItem;
	}
	/**
	 * 把List<Map<String,Object>>转换成 List<CartItem>
	 * @param maplist
	 * @return
	 */
	private List<CartItem> toListCartItem(List<Map<String,Object>> maplist){
		List<CartItem> listCartItem = new ArrayList<CartItem>();
		for(Map<String,Object> map : maplist){
			CartItem cartItem = toCartItem(map);
			listCartItem.add(cartItem);
		}
		return listCartItem;
	}
	
	
	
	/**
	 * 通过用户查询用户的购物车
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public List<CartItem> findByUser(String uid) throws SQLException{
		String sql = "select * from t_cartitem c, t_book b where c.bid=b.bid and uid=? order by c.orderBy";
		//String sql = "select * from t_cartitem c,t_book b where c.bid = b.bid and uid = ? order by c.orderBy";
		List<Map<String,Object>> maplist = qr.query(sql, new MapListHandler(),uid);
		List<CartItem> listCartItem = toListCartItem(maplist);
		return listCartItem;
	}
}
