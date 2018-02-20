package cn.itcast.goods.category.service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.goods.category.dao.CategoryDao;
import cn.itcast.goods.category.domain.Category;

public class CategoryService {
	private CategoryDao categoryDao = new CategoryDao();
	
	public List<Category> findAll(){
		try {
			return categoryDao.findAll();
		} catch (SQLException e) {
		    throw new RuntimeException(e);
		}
	}
	/**
	 * 添加一二级分类
	 * @param category
	 */
	public void addCategory(Category category){
		try {
			categoryDao.addCategory(category);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 添加一二级分类
	 * @param category
	 */
	public void updateCategory(Category category){
		try {
			categoryDao.updateCategory(category);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 查询所有一级分类不包括二级分类
	 * @return
	 */
	public List<Category> findCategory(){
		try {
			return categoryDao.findCategory();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 根据id查询所有分类
	 * @param cid
	 * @return
	 */
	public Category findById(String cid){
		try {
			return categoryDao.findById(cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 通过父id查找二级分类
	 * @param pid
	 * @return
	 */
	public List<Category> findBYParent(String pid) {
		try {
			return categoryDao.findBYParent(pid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 *  删除一级分类
	 * @param pid
	 */
	public void deleteParent(String pid) {
		try {
			categoryDao.deleteParent(pid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
}
