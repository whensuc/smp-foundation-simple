package cn.com.dyg.work.common;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public class TResult<T> {
	
	private String resultCode;
	private String resultMsg;
	
	private T data;

	

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
	public TResult<T> reponseResult(TResult<T> result,String code,String msg){
		result.setResultCode(code);
		result.setResultMsg(msg);
		return result;
	}
	
	/**
	 * 
	* 标题: validParam <br/>
	* 描述: 参数校验<br/>    
	* 作者 : wzy<br/>
	* 版本号: 1.0<br/>
	 */
	public boolean validParam(TResult<T> result,BindingResult bresult){
		try{
			StringBuffer error = new StringBuffer(
					RespCodeEnum.INVALID_PARAMETER.getMessage() + ":");
			if (bresult.hasErrors()) {
				List<ObjectError> allErrors = bresult.getAllErrors();

				for (ObjectError objectError : allErrors) {
					error.append(objectError.getDefaultMessage());
				}
				result = result
						.reponseResult(result,
								RespCodeEnum.INVALID_PARAMETER.getCode(),
								error.toString());
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}

}
