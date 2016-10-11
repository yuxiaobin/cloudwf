package com.rshare.service.wf;

public class WfException extends Throwable{

	private static final long serialVersionUID = 1L;
	
	private String errorCode;
	
	private String errorMsg;
	
	public WfException(){
	}
	
	public WfException(String errorCode) {
		this.errorCode = errorCode;
	}
	public WfException(WfApiConfig.WfApiReturnCodes code) {
		this.errorCode = "WF_API_ERROR_"+code.getReturnCode();
		this.errorMsg = code.getReturnMsg();
	}
	
	public WfException(String errorCode, String errorMsg) {
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
//	@Override
//	public synchronized Throwable fillInStackTrace() {
//		return this;
//	}

	@Override
	public String toString() {
		return "WfException [errorCode=" + errorCode + ", errorMsg=" + errorMsg + "]";
	}
	
}
