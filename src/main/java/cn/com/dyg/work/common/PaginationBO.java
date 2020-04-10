package cn.com.dyg.work.common;


import java.util.List;

public class PaginationBO<T>{
	
    private Integer currentPage;
    private Integer totalPage;
    private Integer pagenum;
    private Boolean nextPage;
    private Boolean prePage;
    private Integer totalNum;
    private List<T> datas;
   
    public PaginationBO() {
		
	}
    
    public PaginationBO(Integer pageindex,Integer pagenum,Integer totalcount) {
    	this.totalPage = totalcount % pagenum==0? totalcount / pagenum : totalcount / pagenum + 1;
    	this.totalNum = totalcount;
    	this.pagenum = pagenum;
    	this.currentPage = pageindex;
    	if(this.currentPage>=this.totalPage) {
    		this.nextPage=false;	
    	}else {
    		this.nextPage=true;
    	}
    	if(this.currentPage>1) {
    		this.prePage = true;
    	}else {
    		this.prePage = false;
    	}
   	}
    
	public Integer getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
	}
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
	public Integer getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}
	public Boolean getNextPage() {
		return nextPage;
	}
	public void setNextPage(Boolean nextPage) {
		this.nextPage = nextPage;
	}
	public Boolean getPrePage() {
		return prePage;
	}
	public void setPrePage(Boolean prePage) {
		this.prePage = prePage;
	}
	public List<T> getDatas() {
		return datas;
	}
	public void setDatas(List<T> datas) {
		this.datas = datas;
	}

	public Integer getPagenum() {
		return pagenum;
	}

	public void setPagenum(Integer pagenum) {
		this.pagenum = pagenum;
	}
    
    
}
