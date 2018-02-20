package cn.itcast.goods.book.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.goods.page.Expression;
import cn.itcast.goods.page.PageBean;
import cn.itcast.goods.page.PageConstants;
import cn.itcast.jdbc.TxQueryRunner;

public class BookDao {
	
	
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * 根据分类查询图书个数
	 * @param cid
	 * @return
	 * @throws SQLException
	 */
	public int findByCategory(String cid) throws SQLException{
		String sql = "select count(*) from t_book where cid = ?";
		Number num = (Number) qr.query(sql, new ScalarHandler(),cid);
		return num == null ? 0 : num.intValue();
	}
	
	/**
	 * 根据书的id查询书的详细信息
	 * @param bid
	 * @return
	 * @throws SQLException
	 */
	public Book findByBid(String bid) throws SQLException{
		String sql = "select * from t_book b,t_category c where b.cid = c.cid and b.bid = ?";
		Map<String,Object> map = qr.query(sql, new MapHandler(),bid);
		Book book = CommonUtils.toBean(map, Book.class);
		Category category = CommonUtils.toBean(map, Category.class);
		if(map.get("pid") != null){
			Category parent = new Category();
			parent.setCid((String)map.get("pid"));
			category.setParent(parent);
		}
		book.setCategory(category);
		return book;
	}
	
	/**
	 * 根据分类查询
	 * @param bid
	 * @param pagecurrent
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByCategor(String cid,int pagecurrent) throws SQLException{
		List<Expression> expreList = new ArrayList<Expression>();
		Expression exp = new Expression("cid","=",cid);
		expreList.add(exp);
		return findByCriteria(expreList,pagecurrent);
	}
	/**
	 * 按照书名模糊插询
	 * @param bid
	 * @param pagecurrent
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByBname(String bname,int pagecurrent) throws SQLException{
		List<Expression> expreList = new ArrayList<Expression>();
		Expression exp = new Expression("bname","like","%" + bname + "%");
		expreList.add(exp);
		return findByCriteria(expreList,pagecurrent);
	}
	/**
	 * 按照作者模糊插询
	 * @param author
	 * @param pagecurrent
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByAuthor(String author,int pagecurrent) throws SQLException{
		List<Expression> expreList = new ArrayList<Expression>();
		Expression exp = new Expression("author","like","%" + author + "%");
		expreList.add(exp);
		return findByCriteria(expreList,pagecurrent);
	}
	/**
	 * 按照出版社模糊插询
	 * @param author
	 * @param pagecurrent
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByPress(String press,int pagecurrent) throws SQLException{
		List<Expression> expreList = new ArrayList<Expression>();
		Expression exp = new Expression("press","like","%" + press + "%");
		expreList.add(exp);
		return findByCriteria(expreList,pagecurrent);
	}
	/**
	 * 多条件组合插询
	 * @param author
	 * @param pagecurrent
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByCombination(Book criteria,int pagecurrent) throws SQLException{
		List<Expression> expreList = new ArrayList<Expression>();
		if(criteria.getBname() != null){
			Expression exp = new Expression("bname","like","%" + criteria.getBname() + "%");
			expreList.add(exp);
		}
		
		if(criteria.getAuthor() != null){
			Expression exp1 = new Expression("author","like","%" + criteria.getAuthor() + "%");
			expreList.add(exp1);
		}
		if(criteria.getPress() != null){
			Expression exp2 = new Expression("press","like","%" + criteria.getPress() + "%");
			expreList.add(exp2);
		}
		return findByCriteria(expreList,pagecurrent);
	}
	
	
	
	/**
	 * 普遍查询方法
	 * @param expression
	 * @return
	 */
	
	private PageBean<Book> findByCriteria(List<Expression> expression,int pagecurrent) throws SQLException{
		//1、封装pagesize
		int pagesize = PageConstants.BOOK_PAGE_SIZE;
		PageBean<Book> pageBean = new PageBean<Book>();
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
		String sql = "select count(*) from t_book" + whereSQL;
		try {
			Number totalrecord = (Number) qr.query(sql, new ScalarHandler(),params.toArray());
			pageBean.setTotalrecord(totalrecord.intValue());
		} catch (RuntimeException e) {}
		//3、封装beanlist
		String sql1 = "select * from t_book" + whereSQL + " order by orderBy limit ?,?";
		params.add((pagecurrent-1)*pagesize);
		params.add(pagesize);
		List<Book> beanlist = qr.query(sql1, new BeanListHandler<Book>(Book.class),params.toArray());
		pageBean.setBeanlist(beanlist);
		pageBean.setPagecurrent(pagecurrent);
		return pageBean;
	}


	/**
	 * 添加图书
	 * @param book
	 * @throws SQLException
	 */
	public void add(Book book) throws SQLException {
		String sql = "insert into t_book(bid,bname,author,price,currPrice," +
				"discount,press,publishtime,edition,pageNum,wordNum,printtime," +
				"booksize,paper,cid,image_w,image_b)" +
				" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] params = {book.getBid(),book.getBname(),book.getAuthor(),
				book.getPrice(),book.getCurrPrice(),book.getDiscount(),
				book.getPress(),book.getPublishtime(),book.getEdition(),
				book.getPageNum(),book.getWordNum(),book.getPrinttime(),
				book.getBooksize(),book.getPaper(), book.getCategory().getCid(),
				book.getImage_w(),book.getImage_b()};
		qr.update(sql, params);
		
	}

	/**
	 * 编辑book
	 * @param book
	 * @throws SQLException
	 */
	public void edit(Book book) throws SQLException {
		String sql = "update t_book set bname=?,author=?,price=?,currPrice=?,"
				+ "discount=?,press=?,publishtime=?,edition=?,pageNum=?,wordNum=?,"
				+ "printtime=?,booksize=?,paper=?,cid=? where bid=?";
		Object[] params = {book.getBname(),book.getAuthor(),
				book.getPrice(),book.getCurrPrice(),book.getDiscount(),
				book.getPress(),book.getPublishtime(),book.getEdition(),
				book.getPageNum(),book.getWordNum(),book.getPrinttime(),
				book.getBooksize(),book.getPaper(), book.getCategory().getCid(),
				book.getBid()};
		qr.update(sql, params);
		
	}

	/**
	 * 删除book
	 * @param book
	 * @throws SQLException 
	 */
	public void delete(Book book) throws SQLException {
		String sql = "delete from t_book where bid = ?";
		qr.update(sql,book.getBid());
	}
  
	
	
	
}
