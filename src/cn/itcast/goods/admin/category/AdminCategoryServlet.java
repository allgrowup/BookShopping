package cn.itcast.goods.admin.category;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.service.BookService;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.goods.category.service.CategoryService;
import cn.itcast.servlet.BaseServlet;

public class AdminCategoryServlet extends BaseServlet {
	private CategoryService categoryService = new CategoryService();
	private BookService bookService = new BookService();
	
	/**
	 * 显示所有
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<Category> parents = categoryService.findAll();
		request.setAttribute("parents", parents);
		return "f:/adminjsps/admin/category/list.jsp";
	}
	
	/**
	 * 添加一级分类
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String addParent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Category category = CommonUtils.toBean(request.getParameterMap(), Category.class);
		category.setCid(CommonUtils.uuid());
		categoryService.addCategory(category);
		return findAll(request,response);
	
	}

	/**
	 * 添加二级分类的第一步
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String addChildPre(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pid = request.getParameter("pid");
		List<Category> categorys = categoryService.findCategory();
		request.setAttribute("pid", pid);
		request.setAttribute("categorys", categorys);
		return "f:/adminjsps/admin/category/add2.jsp";
	}
	/**
	 * 添加二级分类
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String addChild(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pid = request.getParameter("pid");
		Category category = CommonUtils.toBean(request.getParameterMap(), Category.class);
		category.setCid(CommonUtils.uuid());
		Category parent = new Category();		
		parent.setCid(pid);
		category.setParent(parent);
		categoryService.addCategory(category);
		return findAll(request,response);
	
	}
	/**
	 * 修改一级分类的第一步
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String editParentPre(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String cid = request.getParameter("cid");
		Category category = categoryService.findById(cid);
		request.setAttribute("category", category);
		return "f:/adminjsps/admin/category/edit.jsp";
	}
	
	/**
	 * 修改一级分类
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String editParent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Category category = CommonUtils.toBean(request.getParameterMap(), Category.class);
		categoryService.updateCategory(category);
		return findAll(request,response);
	
	}
	/**
	 * 修改二级分类的第一步
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String editChildPre(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String cid = request.getParameter("cid");
		String pid = request.getParameter("pid");
		Category child = categoryService.findById(cid);
		request.setAttribute("pid",pid );
		List<Category> categorys = categoryService.findCategory();
		request.setAttribute("categorys", categorys);
		request.setAttribute("child",child );
		return "f:/adminjsps/admin/category/edit2.jsp";
	}
	/**
	 * 修改二级分类
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String editChild(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Category category = CommonUtils.toBean(request.getParameterMap(), Category.class);
		String pid = request.getParameter("pid");
		Category parent = new Category();
		parent.setCid(pid);
		category.setParent(parent);
		categoryService.updateCategory(category);
		return findAll(request,response);
	
	}
	/**
	 * 删除一级分类
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String deleteParent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pid = request.getParameter("pid");
		List<Category> categorys = categoryService.findBYParent(pid);
		if(categorys != null && categorys.size()>0){
			request.setAttribute("code", "error");
			request.setAttribute("msg", "一级分类下还有二级分类，不能删除");
			return "f:/jsps/msg.jsp";
		}
		categoryService.deleteParent(pid);
		return findAll(request,response);
	}
	
	/**
	 * 删除二级分类
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String deleteChild(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String cid = request.getParameter("cid");
		int count = bookService.findByCategory(cid);
		if(count != 0){
			request.setAttribute("msg", "该二级分类下有图书，不能删除");
			return "f:/adminjsps/msg.jsp";
		}else{
			categoryService.deleteParent(cid);
			return findAll(request,response);
		}
		
	}
	
}
