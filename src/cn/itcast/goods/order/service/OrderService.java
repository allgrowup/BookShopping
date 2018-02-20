package cn.itcast.goods.order.service;

import java.sql.SQLException;


import cn.itcast.goods.order.dao.OrderDao;
import cn.itcast.goods.order.domain.Order;
import cn.itcast.goods.page.PageBean;
import cn.itcast.jdbc.JdbcUtils;

public class OrderService {

	private OrderDao orderDao = new OrderDao();
	
	/**
	 * 更改订单的状态
	 * @param oid
	 */
	public void updateStatus(String oid,int status){
		try {
			orderDao.updateStatus(oid, status);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 查询订单的状态
	 * @param oid
	 * @return
	 */
	public int findStatusByOid(String oid){
		try {
			return orderDao.findStatusByOid(oid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 查询所有订单
	 * @param pagecurrent
	 * @return
	 */
	public PageBean<Order> findAll(int pagecurrent) {
		try {
			JdbcUtils.beginTransaction();
			PageBean<Order> pb = orderDao.findAll(pagecurrent);
			JdbcUtils.commitTransaction();
			return pb;
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {}
			throw new RuntimeException(e);
		}
	}
	/**
	 * 查询所有订单
	 * @param pagecurrent
	 * @return
	 */
	public PageBean<Order> findByStatus(int status,int pagecurrent) {
		try {
			JdbcUtils.beginTransaction();
			PageBean<Order> pb = orderDao.findByStatus(status, pagecurrent);
			JdbcUtils.commitTransaction();
			return pb;
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {}
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 通过订单id查询订单
	 * @param oid
	 * @return
	 */
	public Order findByOid(String oid){
		try {
			return orderDao.findByOid(oid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * 通过用户id查询pageBean
	 * @param uid
	 * @param pagecurrent
	 * @return
	 */
	public PageBean<Order> findByUid(String uid,int pagecurrent){
		try {
			JdbcUtils.beginTransaction();
			PageBean<Order> pb = orderDao.findByUid(uid, pagecurrent);
			JdbcUtils.commitTransaction();
			return pb;
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {}
			throw new RuntimeException(e);
		}
	}
	/**
	 * 添加订单
	 * @param order
	 */
	public void add(Order order){
		try {
			JdbcUtils.beginTransaction();
			orderDao.add(order);
			JdbcUtils.commitTransaction();
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {}
		    throw new RuntimeException(e);
		}
	}
	
	
	
	
}
