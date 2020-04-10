package cn.com.dyg.work.common;


public class BusinessException extends RuntimeException{
	/**
	 *
	 */
	private static final long serialVersionUID = -996132662084014271L;
	private String code;
	private String message;

	public BusinessException(Throwable cause) {
	    super(cause);
	}

	public BusinessException(String message) {
		super(message);
		this.message = message;
	}

	public BusinessException(String message, Throwable cause) {

		super(message, cause);
		this.message = message;
	}



	public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
	}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}



}
