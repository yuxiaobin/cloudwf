package com.rshare.service.wf;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;

public abstract class WfBaseService {
	
	public static class WfOptionTypes{
		public static final String OPT_TYPE_OPTIONS = "options";
		public static final String OPT_TYPE_START = "start";
		public static final String OPT_TYPE_COMMIT = "C";
		public static final String OPT_TYPE_REJECT = "RJ";
		public static final String OPT_TYPE_FORWARD = "F";
		public static final String OPT_TYPE_LETMEDO = "LMD";
		public static final String OPT_TYPE_RECALL = "RC";
	}
	
	@Autowired
	WfService wfService;
	
	public Map<String,Object> doBeforeStart(Map<String,Object> parm) throws WfException{
		return parm;
	}
	public Map<String,Object> doAfterStart(Map<String,Object> parm) throws WfException{
		return parm;
	}
	
	public Map<String,Object> doBeforeCommit(Map<String,Object> parm) throws WfException{
		return parm;
	}
	public Map<String,Object> doAfterCommit(Map<String,Object> parm) throws WfException{
		return parm;
	}
	
	public Map<String,Object> doBeforeReject(Map<String,Object> parm) throws WfException{
		return parm;
	}
	public Map<String,Object> doAfterReject(Map<String,Object> parm) throws WfException{
		return parm;
	}
	
	public Map<String,Object> doBeforeRecall(Map<String,Object> parm) throws WfException{
		return parm;
	}
	public Map<String,Object> doAfterRecall(Map<String,Object> parm) throws WfException{
		return parm;
	}
	
	public Map<String,Object> doBeforeForward(Map<String,Object> parm) throws WfException{
		return parm;
	}
	public Map<String,Object> doAfterForward(Map<String,Object> parm) throws WfException{
		return parm;
	}
	

	public JSONObject execute(Map<String,Object> parm) throws WfException{
		String optType = (String) parm.get("optType");
		String gnmkId = (String) parm.get("gnmkId");
		Integer wfInstNum = (Integer) parm.get("wfInstNum");
		String userId = (String) parm.get("userId");
		Map<String,Object> befResult = null;
		JSONObject json = null;
		JSONObject result = null;
		switch (optType) {
		case WfOptionTypes.OPT_TYPE_START:
			befResult = doBeforeStart(parm);
			if(befResult!=null){
				json = (JSONObject) JSONObject.toJSON(befResult);
				json.put("gnmkId", gnmkId);
			}else{
				json = (JSONObject) JSONObject.toJSON(parm);
			}
			json.put("wfInstNum", wfService.startWorkflow(json));
			doAfterStart(json);
			break;
		case WfOptionTypes.OPT_TYPE_COMMIT:
			befResult = doBeforeCommit(parm);
			if(befResult!=null){
				json = (JSONObject) JSONObject.toJSON(befResult);
				json.put("gnmkId", gnmkId);
				json.put("wfInstNum", wfInstNum);
			}else{
				json = (JSONObject) JSONObject.toJSON(parm);
			}
			result = wfService.commitTask(userId, json);
			json.putAll(result);
			doAfterCommit(json);
			break;
		case WfOptionTypes.OPT_TYPE_REJECT:
			//TODO:
			break;
		case WfOptionTypes.OPT_TYPE_FORWARD:
			//TODO:
			break;
		case WfOptionTypes.OPT_TYPE_LETMEDO:
			//TODO:
			break;
		case WfOptionTypes.OPT_TYPE_RECALL:
			//TODO:
			break;
		case WfOptionTypes.OPT_TYPE_OPTIONS:
			JSONObject jsonOpts = (JSONObject) JSONObject.toJSON(parm);
			result = new JSONObject();
			result.put("array", wfService.getTaskOptions(jsonOpts));//array
			return result;
		default:
			break;
		}
		return null;
	}
}
