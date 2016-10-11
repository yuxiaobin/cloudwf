package com.rshare.service.wf;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;

public abstract class WfBaseService {
	
	private static final Logger log = Logger.getLogger(WfBaseService.class);
	
	public static class WfOptionTypes{
		public static final String OPT_TYPE_OPTIONS = "options";
		public static final String OPT_TYPE_START = "start";
		public static final String OPT_TYPE_COMMIT = WFConstants.OptTypes.COMMIT;
		public static final String OPT_TYPE_REJECT = WFConstants.OptTypes.REJECT;
		public static final String OPT_TYPE_FORWARD = WFConstants.OptTypes.FORWARD;
		public static final String OPT_TYPE_LETMEDO = WFConstants.OptTypes.LET_ME_DO;
		public static final String OPT_TYPE_RECALL = WFConstants.OptTypes.RECALL;
	}
	
	private static final String PARAM_OPT_CODE = "optCode";
	private static final String PARAM_WF_INST_NUM = "wfInstNum";
	private static final String PARAM_REFMK_ID = "refMkid";
	
	@Autowired
	WfService wfService;
	
	public Map<String,Object> doBeforeStart(Map<String,Object> parm) throws WfException{
		log.info("===========doBeforeStart===========");
		return parm;
	}
	public Map<String,Object> doAfterStart(Map<String,Object> parm) throws WfException{
		log.info("===========doAfterStart===========");
		return parm;
	}
	
	public Map<String,Object> doBeforeCommit(Map<String,Object> parm) throws WfException{
		log.info("===========doBeforeCommit===========");
		return parm;
	}
	public Map<String,Object> doAfterCommit(Map<String,Object> parm) throws WfException{
		log.info("===========doAfterCommit===========");
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
	
	/**
	 * 启动工作流
	 * @param parm
	 * @return JSON: {wfInstNum:3}
	 * @throws WfException
	 */
	public JSONObject startWorkflow(Map<String,Object> parm) throws WfException{
		parm.put(PARAM_OPT_CODE, WfOptionTypes.OPT_TYPE_START);
		return execute(parm);
	}
	
	public JSONObject viewWfHistory(Map<String,Object> parm) throws WfException{
		JSONObject result = new JSONObject();
		result.put("history", wfService.getWfHistory((JSONObject)JSONObject.toJSON(parm)));
		return result;
	}
	/**
	 * 工作流调用总入口
	 * @param parm
	 * @return
	 * @throws WfException
	 */
	public JSONObject execute(Map<String,Object> parm) throws WfException{
		String optCode = (String) parm.get(PARAM_OPT_CODE);
		String refMkid = (String) parm.get(PARAM_REFMK_ID);
		Integer wfInstNum = (Integer) parm.get(PARAM_WF_INST_NUM);
		String userId = (String) parm.get("userId");
		Map<String,Object> befResult = null;
		JSONObject json = null;
		JSONObject result = null;
		switch (optCode) {
		case WfOptionTypes.OPT_TYPE_START:
			befResult = doBeforeStart(parm);
			if(befResult!=null){
				json = (JSONObject) JSONObject.toJSON(befResult);
				json.put(PARAM_REFMK_ID, refMkid);
			}else{
				json = (JSONObject) JSONObject.toJSON(parm);
			}
			json.put(PARAM_WF_INST_NUM, wfService.startWorkflow(json));
			doAfterStart(json);
			return json;
		case WfOptionTypes.OPT_TYPE_COMMIT:
			befResult = doBeforeCommit(parm);
			if(befResult!=null){
				json = (JSONObject) JSONObject.toJSON(befResult);
				json.put(PARAM_REFMK_ID, refMkid);
				json.put(PARAM_WF_INST_NUM, wfInstNum);
			}else{
				json = (JSONObject) JSONObject.toJSON(parm);
			}
			result = wfService.commitTask(userId, json);
			json.putAll(result);
			doAfterCommit(json);
			break;
		case WfOptionTypes.OPT_TYPE_REJECT:
			befResult = doBeforeReject(parm);
			if(befResult!=null){
				json = (JSONObject) JSONObject.toJSON(befResult);
				json.put(PARAM_REFMK_ID, refMkid);
				json.put(PARAM_WF_INST_NUM, wfInstNum);
			}else{
				json = (JSONObject) JSONObject.toJSON(parm);
			}
			json.put(PARAM_OPT_CODE, WfOptionTypes.OPT_TYPE_REJECT);
			result = wfService.operateTask(userId, json);
			json.putAll(result);
			doAfterReject(json);
			break;
		case WfOptionTypes.OPT_TYPE_FORWARD:
			befResult = doBeforeForward(parm);
			if(befResult!=null){
				json = (JSONObject) JSONObject.toJSON(befResult);
				json.put(PARAM_REFMK_ID, refMkid);
				json.put(PARAM_WF_INST_NUM, wfInstNum);
			}else{
				json = (JSONObject) JSONObject.toJSON(parm);
			}
			json.put(PARAM_OPT_CODE, WfOptionTypes.OPT_TYPE_FORWARD);
			result = wfService.operateTask(userId, json);
			json.putAll(result);
			doAfterForward(json);
			break;
		case WfOptionTypes.OPT_TYPE_LETMEDO:
			json = (JSONObject) JSONObject.toJSON(parm);
			json.put(PARAM_OPT_CODE, WfOptionTypes.OPT_TYPE_LETMEDO);
			result = wfService.operateTask(userId, json);
			break;
		case WfOptionTypes.OPT_TYPE_RECALL:
			befResult = doBeforeRecall(parm);
			if(befResult!=null){
				json = (JSONObject) JSONObject.toJSON(befResult);
				json.put(PARAM_REFMK_ID, refMkid);
				json.put(PARAM_WF_INST_NUM, wfInstNum);
			}else{
				json = (JSONObject) JSONObject.toJSON(parm);
			}
			json.put(PARAM_OPT_CODE, WfOptionTypes.OPT_TYPE_RECALL);
			result = wfService.operateTask(userId, json);
			json.putAll(result);
			doAfterRecall(json);
			break;
		case WfOptionTypes.OPT_TYPE_OPTIONS:
			json = (JSONObject) JSONObject.toJSON(parm);
			result = new JSONObject();
			result.put("array", wfService.getTaskOptions(json));//array
			break;
		default:
			String errorMsg = "unrecognized optCode="+optCode;
			log.error(errorMsg);
			throw new WfException("InvalidOptCode", errorMsg);
		}
		return result;
	}
}
