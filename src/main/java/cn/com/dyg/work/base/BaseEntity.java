package cn.com.dyg.work.base;


import cn.com.dyg.work.annotation.ZColumn;
import cn.com.dyg.work.annotation.ZTable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;




@SuppressWarnings("serial")
public abstract  class BaseEntity implements Serializable{
	@ZColumn(columnType="smallint",columnLength="1")
	private Integer dr;
	@ZColumn(columnType="varchar",columnLength="19")
	private String ts;

	public BaseEntity() {
		dr = 0;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime date = LocalDateTime.now();
		ts = date.format(formatter);
	}
	public Integer getDr() {
		return dr;
	}
	public void setDr(Integer dr) {
		this.dr = dr;
	}
	public String getTs() {
		return ts;
	}
	public void setTs(String ts) {
		this.ts = ts;
	}


}
