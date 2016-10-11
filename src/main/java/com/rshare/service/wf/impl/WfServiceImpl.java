package com.rshare.service.wf.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rshare.service.wf.WfApiConfig;
import com.rshare.service.wf.WfException;
import com.rshare.service.wf.WfService;

@Service
public class WfServiceImpl implements WfService{
	
	public static final String OPTION_COMMIT 		= "C";		//流程提交
	public static final String OPTION_REJECT 			= "RJ";		//流程退回
	public static final String OPTION_FORWARD 	= "F";		//流程转交
	public static final String OPTION_LETMEDO 		= "LMD";	//我来处理
	public static final String OPTION_RECALL 			= "RC";		//流程撤回
	public static final String OPTION_TRACK 			= "TK";		//流程跟踪
	public static final String OPTION_DISPATCH 	= "DP";		//流程调度
	public static final String OPTION_VETO 			= "V";		//流程否决
	
	private static final String PARM_USER_ID = "userId";
	
	/**
	 * 启动工作流
	 * @param parm: {userId:xxx, refMkid:xxx}
	 * @return {return_code:0, wfInstNum:3}
	 * @throws WfException
	 */
	public Integer startWorkflow(JSONObject parm) throws WfException{
		JSONObject result = postRequest(parm.getString(PARM_USER_ID), WfApiConfig.WfApiOptions.StartWF.getUrl(), parm);
		int returnCode = result.getIntValue("return_code");
		if(returnCode== WfApiConfig.WfApiReturnCodes.Succ.getReturnCode()){
			return result.getInteger("wfInstNum");
		}else{
			throw new WfException(WfApiConfig.WfApiReturnCodes.parseCode(returnCode));
		}
	}
	
	/**
	 * 获取待办事宜
	 * @param parm : {userId:"manager1"}
	 * @return	 json : 
	 * 		{
	   			count:1,
	    		"records": [
			        {
			            "wfInstNum": 1,
			            "awtBegin": 1473822561000,
			            "taskCreater": "aaa",
			            "taskOwner": "user1Id,user2Id,user3Id",
			            "awtEnd": 1473908961000,
			            "taskDescp": "tx1",
			            "preOperator": "userIdXXX",
			            "refMkid": "xxxid"
			        }
    			]
	 * 
	 * 		} 
	 * @throws WfException 
	 */
	public JSONObject getAwt(JSONObject parm) throws WfException{
		return postRequest(parm.getString(PARM_USER_ID), WfApiConfig.WfApiOptions.StartWF.getUrl(), parm);
	}

	/**
	 * 获取下一步操作的事务ID
	 * 
	 * @param parm : {refMkid:xx, wfInstNum:3, optCode:C}
	 * @return		json : {return_code:0, records:[{taskId:"99834c2363b948d3b98e08966ae7f56f", taskType:"user-task",taskDescp:"task descp1"},{taskId:"...", taskType:"user-task",taskDescp:"task descp2"}]} 
	 * @throws WfException
	 */
	public JSONArray getNextTaskList(JSONObject parm) throws WfException{
		return getJSONArray(WfApiConfig.WfApiOptions.GetNextTask.getUrl(), parm);
	}
	
	/**
	 * 获取事务可操作的功能列表
	 * @param parm ： {“userId”:"manager1",“wfInstNum”:" 1",“refMkid”:”abc”}
	 * @return array : [
        {
            "disflag": false,
            "value": "C"
        },
        {
            "disflag": false,
            "value": "RJ"
        }]

	 * @throws WfException
	 */
	public JSONArray getTaskOptions(JSONObject parm) throws WfException{
		return getJSONArray(WfApiConfig.WfApiOptions.GetTaskOptions.getUrl(), parm);
	}
	
	/**
	 * 操作事务：包括提交，撤回，退回，我来处理，转交等, 失败会抛异常
	 * @param currLoginUserId
	 * @param parm : {
	 * 		"userId": "staff1",
	 * 		"refMkid": "abc",
	 * 		"wfInstNum": 1,
	 * 		"optCode": "C",
	 * 		"comments": "your comments here",
	 * 		"nextTaskId": "abcd1234",//可以不指定
	 * 		"nextUserIds": "manager1,manager2,manager3"
	 * 		}
	 * @return JSONObject : {buzStatusBefore:"xx", buzStatusAfter:"xx"}
	 * @throws WfException 
	 */
	public JSONObject operateTask(String currLoginUserId, JSONObject parm) throws WfException{
		return postRequest(currLoginUserId, WfApiConfig.WfApiOptions.OperateTask.getUrl(), parm);
	}
	
	/**
	 * 获取工作流历史记录
	 * @param parm : {
	 * 		“refMkid”:”abc”
	 * 		“wfInstNum”:1
	 * 		}
	 * @return array: [
	 * {
            "taskBegin": 1473822377000,			//开始时间
            "optUser": "staff1",
            "optCode": "C",
            "taskEnd": null,								//计划完成时间
            "taskOwner": "staff1",						//待处理人
            "taskRend": 1473822377000,			//实际完成时间
            "taskDescp": "Start Task Disp",
            "comments": "junitTest: staff1 commit"
        },...
        ]
	 * @throws WfException
	 */
	public JSONArray getWfHistory(JSONObject parm) throws WfException{
		return getJSONArray(WfApiConfig.WfApiOptions.GetHistory.getUrl(), parm);
	}
	
	private JSONArray getJSONArray(String url, JSONObject parm) throws WfException{
		return postRequest(parm.getString(PARM_USER_ID), url, parm).getJSONArray("records");
	}
	
	
	
	@Autowired
	RestTemplate restTemplate;
	
	private JSONObject postRequest(String loginUserId, String url, JSONObject param) throws WfException{
		HttpHeaders headers = new HttpHeaders();
		headers.set("loginUserId", loginUserId);
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(param, headers);
		JSONObject result  = restTemplate.exchange(url, HttpMethod.POST, entity, JSONObject.class).getBody();
		int returnCode = result.getIntValue("return_code");
		if(returnCode== WfApiConfig.WfApiReturnCodes.Succ.getReturnCode() || returnCode== WfApiConfig.WfApiReturnCodes.NoRecord.getReturnCode()){
			return result;
		}else{
			throw new WfException(WfApiConfig.WfApiReturnCodes.parseCode(returnCode));
		}
	}

	@Override
	public JSONObject commitTask(String currLoginUserId, JSONObject parm) throws WfException {
		parm.put(PARM_USER_ID, currLoginUserId);
		parm.put("optCode", OPTION_COMMIT);
		return operateTask(currLoginUserId, parm);
	}

	@Override
	public JSONObject rejectTask(String currLoginUserId, JSONObject parm) throws WfException {
		parm.put(PARM_USER_ID, currLoginUserId);
		parm.put("optCode", OPTION_REJECT);
		return operateTask(currLoginUserId, parm);
	}

}
