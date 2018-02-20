package cn.itcast.goods.admin.book;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.book.service.BookService;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.goods.category.service.CategoryService;


public class AdminAddServlet extends HttpServlet {


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		
		//创建工厂对象
		FileItemFactory faactory = new DiskFileItemFactory();
		//创建解析器
		ServletFileUpload upload = new ServletFileUpload(faactory);
		upload.setFileSizeMax(80*1024);
		List<FileItem> filelist = null;
		try {
			filelist = upload.parseRequest(request);
		} catch (FileUploadException e) {
			error("上传的文件太大了",request,response);
			return;
		}
		Map<String,Object> map = new HashMap<String,Object>();
		for(FileItem fileItem : filelist){
			if(fileItem.isFormField()){//如果是普通字段
				map.put(fileItem.getFieldName(),fileItem.getString("utf-8"));
			}
		}
		Book book = CommonUtils.toBean(map, Book.class);
		Category category = CommonUtils.toBean(map, Category.class);
		book.setCategory(category);
		//获取文件名
		FileItem fileItem = filelist.get(1);
		String filename = fileItem.getName();
		int index = filename.lastIndexOf("\\");
		if(index != -1){
			filename = filename.substring(index+1);
		}
		System.out.println(filename);
		if(!filename.toLowerCase().endsWith(".jpg")){
			error("文件格式不对,必须是jpg",request,response);
			return;
		}
		String savePath = this.getServletContext().getRealPath("/book_img");
		filename = CommonUtils.uuid() + "_" + filename;
		//System.out.println(savePath);
		File file = new File(savePath,filename);
		try{
			fileItem.write(file);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		ImageIcon icon = new ImageIcon(file.getAbsolutePath());
		Image image = icon.getImage();
		if(image.getHeight(null) > 350 || image.getWidth(null) > 350){
			error("上传的图片大小要350*350",request,response);
			file.delete();
			return;
		}
		book.setImage_w("book_img/" + filename);
		
		
		//获取文件名
	    fileItem = filelist.get(2);
		filename = fileItem.getName();
		index = filename.lastIndexOf("\\");
		if(index != -1){
			filename = filename.substring(index+1);
		}
		filename = CommonUtils.uuid() + "_" + filename;
		//System.out.println(filename);
		if(!filename.toLowerCase().endsWith(".jpg")){
			error("文件格式不对,必须是jpg",request,response);
			return;
		}
		savePath = this.getServletContext().getRealPath("/book_img");
		file = new File(savePath,filename);
		try{
			fileItem.write(file);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	    icon = new ImageIcon(file.getAbsolutePath());
		image = icon.getImage();
		if(image.getHeight(null) > 350 || image.getWidth(null) > 350){
			error("上传的图片大小要350*350",request,response);
			file.delete();
			return;
		}
		book.setImage_b("book_img/" + filename);
		
		book.setBid(CommonUtils.uuid());
		BookService bookService = new BookService();
		bookService.add(book);
		request.setAttribute("msg", "添加成功");
		request.getRequestDispatcher("/adminjsps/msg.jsp").forward(request, response);
	}
	
	
	protected void error(String msg,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("msg", msg);
		request.setAttribute("parents", new CategoryService().findCategory());
		request.getRequestDispatcher("/adminjsps/admin/book/add.jsp").forward(request, response);
	}

}
