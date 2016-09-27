package com.rshare.service.wf;

import org.springframework.web.bind.annotation.RequestMethod;

public class WfApiConfig {
	
//	public static final String WF_SERVER_URL = "http://localhost:8081/wf";
	public static final String WF_SERVER_URL = "http://192.168.10.15:8383/rs-bpm-server";

	public static enum WfApiOptions{
		GetAwt("/wfapi/awt", RequestMethod.POST),
		StartWF("/wfapi/start", RequestMethod.POST),
		GetNextTask("/wfapi/tasks", RequestMethod.POST),
		GetTaskOptions("/wfapi/options", RequestMethod.POST),
		OperateTask("/wfapi/operate", RequestMethod.POST),
		OpenOperateTask("/wfapi/operate", RequestMethod.GET),
		GetHistory("/wfapi/history", RequestMethod.POST);
		
		private String url;
		private RequestMethod httpMethod;
		private WfApiOptions(String url, RequestMethod httpMethod) {
			this.url = url;
			this.httpMethod = httpMethod;
		}
		public String getUrl() {
			return WF_SERVER_URL+url;
		}
		public RequestMethod getHttpMethod() {
			return httpMethod;
		}
	}
	
	public static enum WfApiReturnCodes{
		Succ(0,"success"),
		Invalid(1,"Invalid parameters"),
		NoRecord(2,"No record found"),
		NotAllow(3,"Operation not allowed"),
		WFNotDefined(4,"WF not defined"),
		SysError(9,"Wf API system error");
		
		private int returnCode;
		private String returnMsg;
		private WfApiReturnCodes(int returnCode, String returnMsg) {
			this.returnCode = returnCode;
			this.returnMsg = returnMsg;
		}
		public int getReturnCode() {
			return returnCode;
		}
		public String getReturnMsg() {
			return returnMsg;
		}
		
		public static WfApiReturnCodes parseCode(int returnCode){
			switch (returnCode) {
			case 1:
				return Invalid;
			case 2:
				return NoRecord;
			case 3:
				return NotAllow;
			case 4:
				return WFNotDefined;
			case 9:
				return SysError;
			default:
				return SysError;
			}
		}
		
	}
}
