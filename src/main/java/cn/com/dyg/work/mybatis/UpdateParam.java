package cn.com.dyg.work.mybatis;



import java.util.Map;

public class UpdateParam {
    private Map<String,Object> cols;
    private Map<String,Object> whereparam;
	public Map<String, Object> getCols() {
		return cols;
	}
	public void setCols(Map<String, Object> cols) {
		this.cols = cols;
	}
	public Map<String, Object> getWhereparam() {
		return whereparam;
	}
	public void setWhereparam(Map<String, Object> whereparam) {
		this.whereparam = whereparam;
	}

}
