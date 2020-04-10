package cn.com.dyg.work.common;


public enum RespCodeEnum {
	
	
	REQUEST_SUCCESS("0","请求成功"),

	UNKNOWN_ERROR("1","未知错误"),
	
	SERVER_UNAVAILABLE("2","服务暂不可用"),

	UNSUPPORTED_METHOD("3","未知的方法"),

	REQUEST_LIMIT("4","接口调用次数已达到设定的上限"),
	
	UNAUTHORIZED_IP("5","未经授权的ip地址"),
	
	NOPERMISSION_ACCESS("6","无权限访问"),
	
	INVALID_PARAMETER("100","请求参数无效"),
	
	INVALID_APIKEY("101","APIKEY无效"),
	
	INCORRECT_SIGNTURE("102","无效签名"),

	TOOMANY_PARAMETERS("103","请求参数过多"),
	
	UNSUPPORTED_SIGNATUREMETHOD("106","未知的签名方法"),

	INVALIDPARAMETER_TIMESTAMP("107","timestamp参数无效"),

	INVALID_TOKEN("110","无效的token"),
	
	EXPIRED_TOKEN("111","token已过期"),
	EXPIRED_SIGNTURE("112","签名已过期"),
	REQUEST_FAIL("201","请求失败"),
  
	INVALID_OPERATION("801","无效的操作"),
	
	DATABASE_ERROR("802","数据库操作失败");
   
    private String code;  
    private String message;  
   
    RespCodeEnum( String code, String message) {   
        setCode(code);  
        setMessage(message);  
    }  
  
   
  
    public String getCode() {  
        return this.code;  
    }  
  
    public void setCode(String code) {  
        this.code = code;  
    }  
  
    public String getMessage() {  
        return this.message;  
    }  
  
    public void setMessage(String message) {  
        this.message = message;  
    }  
}
