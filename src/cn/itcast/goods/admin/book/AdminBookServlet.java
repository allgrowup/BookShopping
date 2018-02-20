package cn.itcast.goods.admin.book;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.book.service.BookService;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.goods.category.service.CategoryService;
import cn.itcast.goods.page.PageBean;
import cn.itcast.servlet.BaseServlet;


public class AdminBookServlet extends BaseServlet {
	private CategoryService categoryService = new CategoryService();
	private BookService bookService = new BookService();
	public String findCategoryAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<Category> parents = categoryService.findAll();
		request.setAttribute("parents", parents);
		return "f:/adminjsps/admin/book/left.jsp";
	}

	/**
	 * 添加图书的准备
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String addBookPre(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<Category> parents = categoryService.findCategory();
		request.setAttribute("parents", parents);
		return "f:/adminjsps/admin/book/add.jsp";
	}
	/**
	 * 添加图书的准备2
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String ajaxaddBookPre(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pid = request.getParameter("pid");
		List<Category> children = categoryService.findBYParent(pid);
		String arr = toArr(children);
		response.getWriter().println(arr);
		return null;
	}
	//把category对象变成{"cid":"aaaaaa"}的形式
	private String toChild(Category category){
		StringBuilder sb = new StringBuilder();
		sb.append("{").append("\"cid\"").append(":").append("\"").append(category.getCid()).append("\"");
		sb.append(",").append("\"cname\"").append(":").append("\"").append(category.getCname()).append("\"");
		sb.append("}");
		return sb.toString();
	}
	
	private String toArr(List<Category> children){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int i = 0;i < children.size();i++){
			sb.append(toChild(children.get(i)));
			if(i < children.size()-1){
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
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
    	return "f:/adminjsps/admin/book/list.jsp";
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
    	return "f:/adminjsps/admin/book/list.jsp";
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
    	return "f:/adminjsps/admin/book/list.jsp";
    }

    /**
     * 多条件查询
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
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
    
    	PageBean<Book> pageBean = bookService.findByCombination(book, pagecurrent);
    	pageBean.setPagecurrent(pagecurrent);
    	pageBean.setUrl(url);
    	request.setAttribute("pageBean", pageBean);
    	return "f:/adminjsps/admin/book/list.jsp";
    }
    
    
    	/**
    	 * 按ID查询
    	 * @param request
    	 * @param response
    	 * @return
    	 * @throws ServletException
    	 * @throws IOException
    	 */
    	public String load(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    		String bid = request.getParameter("bid");
    		Book book = bookService.load(bid);
    		request.setAttribute("book", book);
    		request.setAttribute("parents", categoryService.findCategory());
    		String pid = book.getCategory().getParent().getCid();
    		request.setAttribute("children", categoryService.findBYParent(pid));
    		return "f:/adminjsps/admin/book/desc.jsp";
    	}
    	/**
    	 * 修改book
    	 * @param request
    	 * @param response
    	 * @return
    	 * @throws ServletException
    	 * @throws IOException
    	 */
    	public String edit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    		Map map = request.getParameterMap();
    		Book book = CommonUtils.toBean(map, Book.class);
    		Category category = CommonUtils.toBean(map, Category.class);
    		Category parent = new Category();
    		parent.setCid(request.getParameter("pid"));
    		category.setParent(parent);
    		book.setCategory(category);
    		bookService.edit(book);
    		request.setAttribute("msg", "修改成功！");
    		return "f:/adminjsps/msg.jsp";
    	}
    	
    	/**
    	 * 删除book
    	 * @param request
    	 * @param response
    	 * @return
    	 * @throws ServletException
    	 * @throws IOException
    	 */
    	public String delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    		String bid = request.getParameter("bid");
    		Book book = bookService.load(bid);
    		String savePath = this.getServletContext().getRealPath("/");
    		new File(savePath,book.getImage_b()).delete();
    		new File(savePath,book.getImage_w()).delete();
    		bookService.delete(book);
    		request.setAttribute("msg", "删除成功！");
    		return "f:/adminjsps/msg.jsp";
    	}
    	
}
