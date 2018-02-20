package cn.itcast.goods.order.dao;



import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.order.domain.Order;
import cn.itcast.goods.order.domain.OrderItem;
import cn.itcast.goods.page.Expression;
import cn.itcast.goods.page.PageBean;
import cn.itcast.goods.page.PageConstants;
import cn.itcast.jdbc.TxQueryRunner;

public class OrderDao {
	
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * 根据oid查询
	 * @param oid
	 * @return
	 * @throws SQLException
	 */
	public Order findByOid(String oid) throws SQLException{
		String sql = "select * from t_order where oid = ?";
		Order order = qr.query(sql, new BeanHandler<Order>(Order.class),oid);
		load(order);
		return order;
	}
	/**
	 * 修改订单的状态
	 * @param oid
	 * @throws SQLException
	 */
	public void updateStatus(String oid,int status) throws SQLException{
		String sql = "update t_order set status = ? where oid = ?";
		qr.update(sql,status,oid);
	}
	/**
	 * 查询订单的状态
	 * @param oid
	 * @return
	 * @throws SQLException
	 */
	public int findStatusByOid(String oid) throws SQLException{
		String sql = "select status from t_order where oid = ?";
		Number status = (Number) qr.query(sql, new ScalarHandler(),oid);
		return status.intValue();
	}
	
	
	/**
	 * 按照用户id插询
	 * @param bid
	 * @param pagecurrent
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Order> findByUid(String uid,int pagecurrent) throws SQLException{
		List<Expression> expreList = new ArrayList<Expression>();
		Expression exp = new Expression("uid","=",uid);
		expreList.add(exp);
		return findByCriteria(expreList,pagecurrent);
	}
	/**
	 * 查询所有订单
	 * @param pagecurrent
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Order> findAll(int pagecurrent) throws SQLException{
		List<Expression> expreList = new ArrayList<Expression>();
		return findByCriteria(expreList,pagecurrent);
	}
	/**
	 * 按订单状态查询
	 * @param status
	 * @param pagecurrent
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Order> findByStatus(int status,int pagecurrent) throws SQLException{
		List<Expression> expreList = new ArrayList<Expression>();
		Expression exp = new Expression("status","=",status + "");
		expreList.add(exp);
		return findByCriteria(expreList,pagecurrent);
	}
	
	/**
	 * 普遍查询方法
	 * @param expression
	 * @return
	 */
	
	private PageBean<Order> findByCriteria(List<Expression> expression,int pagecurrent) throws SQLException{
		//1、封装pagesize
		int pagesize = PageConstants.ORDER_PAGE_SIZE;
		PageBean<Order> pageBean = new PageBean<Order>();
		pageBean.setPagesize(pagesize);
		//2、封装总记录数 
		//where 1=1 and name = value
		List<Object> params = new ArrayList<Object>();
		StringBuilder whereSQL = new StringBuilder();
		whereSQL.append(" where 1 = 1");
		for(Expression expre : expression){
			whereSQL.append(" and ").append(expre.getName()).append(" ");
			//逻辑运算符不是is null 如果是sql语句后面不要value
			if(!expre.getOperator().equals("is null")){
				whereSQL.append(expre.getOperator()).append(" ").append("?");
				params.add(expre.getValue());
			}
		}
		String sql = "select count(*) from t_order" + whereSQL;
		try {
			Number totalrecord = (Number) qr.query(sql, new ScalarHandler(),params.toArray());
			pageBean.setTotalrecord(totalrecord.intValue());
		} catch (RuntimeException e) {}
		//3、封装beanlist
		String sql1 = "select * from t_order" + whereSQL + " order by ordertime desc limit ?,?";
		params.add((pagecurrent-1)*pagesize);
		params.add(pagesize);
		List<Order> beanlist = qr.query(sql1, new BeanListHandler<Order>(Order.class),params.toArray());
		for(Order order:beanlist){
			//加载orderItem到order
			load(order);
		}
		pageBean.setBeanlist(beanlist);
		pageBean.setPagecurrent(pagecurrent);
		return pageBean;
	}

	/**
	 * 查询OrderItem
	 * @param order
	 * @throws SQLException 
	 */
	private void load(Order order) throws SQLException {
		String sql = "select * from t_orderitem where oid = ?";
		List<Map<String,Object>> maplist = qr.query(sql, new MapListHandler(),order.getOid());
		List<OrderItem> orderItemlist = toOrderItemList(maplist);
		order.setOrderItemList(orderItemlist);
	}

	/**
	 * 把List<map>变成List<OrderItem>
	 * @param maplist
	 * @return
	 */
	private List<OrderItem> toOrderItemList(List<Map<String, Object>> maplist) {
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for(Map<String,Object> map :maplist){
			OrderItem orderItem = toOrderItem(map);
			orderItemList.add(orderItem);
		}
		return orderItemList;
	}


	/**
	 * 把map变成orderItem对象
	 * @param map
	 * @return
	 */
	private OrderItem toOrderItem(Map<String, Object> map) {
		OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		orderItem.setBook(book);
		return orderItem;
	}
	
	/**
	 * 添加订单到数据库
	 * @param order
	 * @throws SQLException 
	 */
	public void add(Order order) throws SQLException{
		//添加order到数据库
		String sql = "insert into t_order values(?,?,?,?,?,?)";
		Object[] params = {order.getOid(),order.getOrdertime(),order.getTotal(),order.getStatus(),
				order.getAddress(),order.getOwner().getUid()};
		qr.update(sql,params);
		sql = "insert into t_orderitem values(?,?,?,?,?,?,?,?)";
		List<OrderItem> orderitemList = order.getOrderItemList();
		int len = orderitemList.size();
		Object[][] objs = new Object[len][];
		for(int i=0;i < len;i++){
			OrderItem orderitem = orderitemList.get(i);
			objs[i] = new Object[]{orderitem.getOrderItemId(),orderitem.getQuantity(),
					orderitem.getSubtotal(),orderitem.getBook().getBid(),
					orderitem.getBname(),orderitem.getCurrPrice(),orderitem.getImage_b(),
					order.getOid()};
		}
		qr.batch(sql, objs);
	}
	
	
	
	
}
