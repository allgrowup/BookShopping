package cn.itcast.goods.category.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.jdbc.TxQueryRunner;

public class CategoryDao {

	private QueryRunner qr = new TxQueryRunner();
	/*
	 * 把一个Map中的数据映射到Category中
	 */
	private Category toCategory(Map<String,Object> map){
		Category category = CommonUtils.toBean(map, Category.class);
		String pid = (String) map.get("pid");
		if(pid != null){
			Category parent = new Category();//创建一个父分类
			parent.setCid(pid);
			category.setParent(parent);
		}
		return category;
	}
	/*
	 * 可以把多个Map(List<Map>)映射成多个Category(List<Category>)
	 */
	private List<Category> toCategoryList(List<Map<String,Object>> mapList){
		List<Category> categoryList = new ArrayList<Category>();
		for(Map<String,Object> map : mapList){
			Category category = toCategory(map);
			categoryList.add(category);
		}
		return categoryList;
	}
	/**
	 * 换回所有分类
	 * @return
	 * @throws SQLException
	 */
	public List<Category> findAll() throws SQLException{
		String sql = "select * from t_category where pid is null";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler());
		List<Category> parents = toCategoryList(mapList);
		for(Category parent : parents){
			List<Category> children = findBYParent(parent.getCid());
			parent.setChildren(children);
		}
		return parents;
	}
	/**
	 * 通过父id查找二级分类
	 * @param pid
	 * @return
	 * @throws SQLException
	 */
	public List<Category> findBYParent(String pid) throws SQLException{
		String sql = "select * from t_category where pid=?";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(),pid);
		List<Category> categoryList = toCategoryList(mapList);
		return categoryList;
	}	
	/**
	 * 删除一级分类
	 * @param pid
	 * @throws SQLException
	 */
	public void deleteParent(String pid) throws SQLException{
		String sql = "delete from t_category where cid=?";
		qr.update(sql,pid);
	}
	/**
	 * 添加一二级分类的方法
	 * @param category
	 * @throws SQLException
	 */
	public void addCategory(Category category) throws SQLException{
		String sql = "insert into t_category(cid,cname,pid,`desc`) values(?,?,?,?)";
		String pid = null;
		if(category.getParent() != null){
			pid = category.getParent().getCid();
		}
		Object[] params = {category.getCid(),category.getCname(),pid,category.getDesc()};
		qr.update(sql,params);
		
	}
	/**
	 * 修改一二级分类的方法
	 * @param category
	 * @throws SQLException
	 */
	public void updateCategory(Category category) throws SQLException{
		String sql = "update t_category set cname=?,pid=?,`desc`=? where cid=?";
		String pid = null;
		if(category.getParent() != null){
			pid = category.getParent().getCid();
		}
		Object[] params = {category.getCname(),pid,category.getDesc(),category.getCid()};
		qr.update(sql,params);
		
	}
	/**
	 * 查询所有一级分类不包括二级分类
	 * @return
	 * @throws SQLException
	 */
	public List<Category> findCategory() throws SQLException{
		String sql = "select * from t_category where pid is null";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler());
		List<Category> parents = toCategoryList(mapList);
		return parents;
	}
	/**
	 * 根据id查询所有一级分类
	 * @param cid
	 * @return
	 * @throws SQLException
	 */
	public Category findById(String cid) throws SQLException{
		String sql = "select * from t_category where cid=?";
		return qr.query(sql, new BeanHandler<Category>(Category.class),cid);
	}
	
	
	
}
