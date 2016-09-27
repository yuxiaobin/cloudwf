package com.rshare.service.wf;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rshare.service.wf.WfException;

public interface WfService {

	
	/**
	 * 启动工作流
	 * @param parm: {userId:xxx, gnmkId:xxx}
	 * @return {return_code:0, wfInstNum:3}
	 * @throws WfException > errorCode:
	 * 		1：非法参数
	 * 		2：查询时返回，表明没有找到对应的记录
	 * 		3：操作不被允许（没有权限之类）
	 * 		4：工作流没有定义
	 * 		9：API系统调用出现错误
	 * 	
	 */
	public Integer startWorkflow(JSONObject parm) throws WfException;
	
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
			            "gnmkId": "xxxid"
			        }
    			]
	 * 
	 * 		} 
	 * @throws WfException 
	 */
	public JSONObject getAwt(JSONObject parm) throws WfException;

	/**
	 * 获取下一步操作的事务ID
	 * 
	 * @param parm : {gnmkId:xx, wfInstNum:3, optCode:C}
	 * @return		json : {return_code:0, records:[{taskId:"99834c2363b948d3b98e08966ae7f56f", taskType:"user-task",taskDescp:"task descp1"},{taskId:"...", taskType:"user-task",taskDescp:"task descp2"}]} 
	 * @throws WfException
	 */
	public JSONArray getNextTaskList(JSONObject parm) throws WfException;
	
	/**
	 * 获取事务可操作的功能列表
	 * @param parm ： {“userId”:"manager1",“wfInstNum”:" 1",“gnmkId”:”abc”}
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
	public JSONArray getTaskOptions(JSONObject parm) throws WfException;
	
	/**
	 * 操作事务：包括提交，撤回，退回，我来处理，转交等, 失败会抛异常
	 * @param currLoginUserId
	 * @param parm : {
	 * 		"userId": "staff1",
	 * 		"gnmkId": "abc",
	 * 		"wfInstNum": 1,
	 * 		"optCode": "C",
	 * 		"comments": "your comments here",
	 * 		"nextTaskId": "abcd1234",
	 * 		"nextUserIds": "manager1,manager2,manager3"
	 * 		}
	 * @return JSONObject		返回业务状态等数据
	 * @throws WfException 
	 */
	public JSONObject operateTask(String currLoginUserId, JSONObject parm) throws WfException;
	
	/**
	 * 提交事务
	 * @param currLoginUserId
	 * @param parm : {
	 * 		"gnmkId": "abcId",
	 * 		"wfInstNum": 1,
	 * 		"comments": "your comments here",
	 * 		"nextUserIds": "manager1,manager2,manager3"
	 * 		}
	 * @return NIL
	 * @throws WfException 
	 */
	public JSONObject commitTask(String currLoginUserId, JSONObject parm) throws WfException;
	
	/**
	 * 提交事务
	 * @param currLoginUserId
	 * @param parm : {
	 * 		"gnmkId": "abcId",
	 * 		"wfInstNum": 1,
	 * 		"comments": "your comments here",
	 * 		"back2First": "false",
	 * 		"nextUserIds": "manager1,manager2,manager3"
	 * 		}
	 * 如果需要指定退回到第一步，back2First="true"
	 * @return NIL
	 * @throws WfException 
	 */
	public JSONObject rejectTask(String currLoginUserId, JSONObject parm) throws WfException;
	
	
	public void letMeDoTask(String currLoginUserId, JSONObject parm) throws WfException;
	public void forwardTask(String currLoginUserId, JSONObject parm) throws WfException;
	public JSONObject recallTask(String currLoginUserId, JSONObject parm) throws WfException;
	
	/**
	 * 获取工作流历史记录
	 * @param parm : {
	 * 		“gnmkId”:”abc”
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
	public JSONArray getWfHistory(JSONObject parm) throws WfException;
}
