package cn.itcast.goods.page;

import java.util.List;

/**
 * 封装数据到PageBean
 * @author Administrator
 *
 */
public class PageBean<T> {

	private int pagecurrent;//当前页
	private int totalrecord;//总记录数
	private int pagesize;//每页的记录数
	private List<T> beanlist;
	private String url;//请求的路径
	
	
	public int getTotalPage(){
	    int totalPage = totalrecord / pagesize;
		return totalrecord % pagesize == 0 ? totalPage : totalPage + 1;	
	}	
	
	public int getPagecurrent() {
		return pagecurrent;
	}
	public void setPagecurrent(int pagecurrent) {
		this.pagecurrent = pagecurrent;
	}
	public int getTotalrecord() {
		return totalrecord;
	}
	public void setTotalrecord(int totalrecord) {
		this.totalrecord = totalrecord;
	}
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	public List<T> getBeanlist() {
		return beanlist;
	}
	public void setBeanlist(List<T> beanlist) {
		this.beanlist = beanlist;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
}