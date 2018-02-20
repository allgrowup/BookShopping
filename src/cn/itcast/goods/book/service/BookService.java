package cn.itcast.goods.book.service;

import java.sql.SQLException;

import cn.itcast.goods.book.dao.BookDao;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.page.PageBean;

public class BookService {

	private BookDao bookDao = new BookDao();
	
	
	public int findByCategory(String cid){
		try {
			return bookDao.findByCategory(cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * 通过id查询
	 * @param bid
	 * @return
	 */
	public Book load(String bid){
		try {
			return bookDao.findByBid(bid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 根据分类查询
	 * @param bid
	 * @param pagecurrent
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByCategor(String cid,int pagecurrent) {
		try {
			return bookDao.findByCategor(cid, pagecurrent);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	/**
	 * 按照书名模糊插询
	 * @param bid
	 * @param pagecurrent
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByBname(String bname,int pagecurrent){
		try {
			return bookDao.findByBname(bname, pagecurrent);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按照作者模糊插询
	 * @param author
	 * @param pagecurrent
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByAuthor(String author,int pagecurrent){
		try {
			return bookDao.findByAuthor(author, pagecurrent);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	
	/**
	 * 按照出版社模糊插询
	 * @param author
	 * @param pagecurrent
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByPress(String press,int pagecurrent){
		try {
			return bookDao.findByPress(press, pagecurrent);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 多条件组合插询
	 * @param author
	 * @param pagecurrent
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByCombination(Book criteria,int pagecurrent){
		try {
			return bookDao.findByCombination(criteria, pagecurrent);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}


	public void add(Book book) {
		try {
		   bookDao.add(book);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}


	public void edit(Book book) {
		try {
			   bookDao.edit(book);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		
	}


	public void delete(Book book) {
		try {
			 
		    bookDao.delete(book);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	
	
}
