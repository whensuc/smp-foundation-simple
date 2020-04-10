package cn.com.dyg.work.mybatis;



import java.util.Map;


public class QueryListParam {
	private Integer pageindex=1;
	private Integer pagenum=10;
	private String ordercol;
	private Map<String,Object> whereparam;
	private String wheresql;



	public String getWheresql() {
		return wheresql;
	}
	public QueryListParam setWheresql(String wheresql) {
		this.wheresql = wheresql;
		return this;
	}
	public Integer getPageindex() {
		return pageindex;
	}
	public QueryListParam setPageindex(Integer pageindex) {
		this.pageindex = pageindex;
		return this;
	}
	public Integer getPagenum() {
		return pagenum;
	}
	public QueryListParam setPagenum(Integer pagenum) {
		this.pagenum = pagenum;
		return this;
	}
	public Map<String, Object> getWhereparam() {
		return whereparam;
	}
	public void setWhereparam(Map<String, Object> whereparam) {
		this.whereparam = whereparam;
	}
	public String getOrdercol() {
		return ordercol;
	}

	public QueryListParam setOrdercol(String ordercol) {
		this.ordercol = ordercol;
		return this;
	}

}
