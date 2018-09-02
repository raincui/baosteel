package com.android.yl.baowu.basebusiness.http;

/**
 * Title: BaseResponse.java<br>
 * Description: <br>
 * Create DateTime: 2015-12-04 下午3:55:45<br>
 * 
 */
public class BaseResponse {

	private String result;
	private int count;
	private String msg;
	private String code;
	private int httpStatus;

	/**
	 * 网络请求是否成功
	 * 
	 * @return
	 */
	public boolean isSuccess() {
		return "success".equals(result);
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}
}
