package cn.itcast.goods.book.web.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.book.service.BookService;
import cn.itcast.goods.page.PageBean;
import cn.itcast.servlet.BaseServlet;

/**
 * Servlet implementation class BookServlet
 */
public class BookServlet extends BaseServlet {
	
	private BookService bookService = new BookService();
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
	 * 根据分类查询
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
    public String findByCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1、获取当前页
    	int pagecurrent = getPagecurrent(request);
    	//2、获取url
    	String url = getUrl(request);
    	//得到cid
    	String cid = request.getParameter("cid");
    	PageBean<Book> pageBean = bookService.findByCategor(cid, pagecurrent);
    	pageBean.setPagecurrent(pagecurrent);
    	pageBean.setUrl(url);
    	request.setAttribute("pageBean", pageBean);
    	return "f:/jsps/book/list.jsp";
    }
    /**
     * 按作者查询
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findByAuthor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	//1、获取当前页
    	int pagecurrent = getPagecurrent(request);
    	//2、获取url
    	 
    	String url = getUrl(request);
    	//得到cid
    	//String author = new String(request.getParameter("author").getBytes("iso-8859-1"), "utf-8"); 
    	String author = url;
    	//得到author的值
    	int index = author.lastIndexOf("author=");
    	if(index != -1){
    		author = author.substring(index + 7);
    	}
    	//author = URLDecoder.decode(author,"utf-8");
    	PageBean<Book> pageBean = bookService.findByAuthor(author, pagecurrent);
    	pageBean.setPagecurrent(pagecurrent);
    	pageBean.setUrl(url);
    	request.setAttribute("pageBean", pageBean);
    	return "f:/jsps/book/list.jsp";
    }
    /**
     * 通过出版社查询
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findByPress(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1、获取当前页
    	int pagecurrent = getPagecurrent(request);
    	//2、获取url
    	String url = getUrl(request);
    	//得到cid
    	String press = url;
    	int index = press.lastIndexOf("press=");
    	if(index != -1){
    		press = press.substring(index + 6);
    	}
    	PageBean<Book> pageBean = bookService.findByPress(press, pagecurrent);
    	pageBean.setPagecurrent(pagecurrent);
    	pageBean.setUrl(url);
    	request.setAttribute("pageBean", pageBean);
    	return "f:/jsps/book/list.jsp";
    }
    /**
     * 通过书名模糊查询
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findByBname(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1、获取当前页
    	int pagecurrent = getPagecurrent(request);
    	//2、获取url
    	String url = getUrl(request);
    	//得到cid
    	String bname = url;
    	int index = bname.lastIndexOf("bname=");
    	if(index != -1){
    		bname = bname.substring(index + 6);
    	}
    	PageBean<Book> pageBean = bookService.findByBname(bname, pagecurrent);
    	pageBean.setPagecurrent(pagecurrent);
    	pageBean.setUrl(url);
    	request.setAttribute("pageBean", pageBean);
    	return "f:/jsps/book/list.jsp";
    }
    
    public String findByCombination(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	//request.setCharacterEncoding("utf-8");
    	//1、获取当前页
    	int pagecurrent = getPagecurrent(request);
    	//2、获取url
    	String url = getUrl(request);
    	//得到cid
    	//Book book = CommonUtils.toBean(request.getParameterMap(), Book.class);
    	String params = url;
    	int indexBname = params.lastIndexOf("&bname=");
    	int indexAuthor = params.lastIndexOf("&author=");
    	int indexPress = params.lastIndexOf("&press=");
    	Book book = new Book();
    	if(indexBname + 7 != indexAuthor){
    		String bname = url.substring(indexBname + 7, indexAuthor);
    		book.setBname(bname);
    	}
    	if(indexAuthor + 8 != indexPress){
    		String author = url.substring(indexAuthor + 8, indexPress);
    		book.setAuthor(author);
    	}
    	if(indexPress + 7 != url.length()){
    		String press = url.substring(indexPress + 7);
    		book.setPress(press);
    	}
    	/*
    	String bname = request.getParameter("bname");
    	String author = request.getParameter("author");
    	String press = request.getParameter("press");
    	Book book = new Book();
    	book.setBname(bname);
    	book.setAuthor(author);
    	book.setPress(press);*/
    	PageBean<Book> pageBean = bookService.findByCombination(book, pagecurrent);
    	pageBean.setPagecurrent(pagecurrent);
    	pageBean.setUrl(url);
    	request.setAttribute("pageBean", pageBean);
    	return "f:/jsps/book/list.jsp";
    }
    
    
    	
    	public String load(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    		String bid = request.getParameter("bid");
    		Book book = bookService.load(bid);
    		request.setAttribute("book", book);
    		return "f:/jsps/book/desc.jsp";
    		
    	}
    
    
    
}
