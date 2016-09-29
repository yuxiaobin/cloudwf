package com.rshare.controller.wf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rshare.service.wf.WfService;

@Controller
@RequestMapping(value="/wfsample")
public class WfSampleController {
	
	@Autowired
	WfService wfService;
	
	@RequestMapping(value="/")
	public String wfIndex(){
		return "wfsample/wf-sample";
	}
	@RequestMapping(value="")
	public String testWfHello(){
		return wfIndex();
	}
	
	/*@RequestMapping(value="/operate")
	@ResponseBody
	public Object testWf(@RequestBody JSONObject reqParm, HttpSession session, HttpServletRequest req) throws WfException{
		String gnmkId = reqParm.getString("gnmkId");
		if(StringUtils.isEmpty(gnmkId)){
			gnmkId = "erpTest";
		}
		String optType = reqParm.getString("optType");
		String operator = reqParm.getString("userId");
		if(operator==null || operator.isEmpty()){
			operator = "staff1";
		}
		String nextUserIds = reqParm.getString("nextUserIds");
		if(nextUserIds==null || nextUserIds.isEmpty()){
			nextUserIds = "staff1";
		}
		Integer wfInstNum = null;
		String instNumStr = reqParm.getString("wfInstNum");
		if(instNumStr!=null){
			wfInstNum = Integer.parseInt(instNumStr); 
		}else{
			wfInstNum = (Integer) session.getAttribute("wfInstNum");
			if(wfInstNum==null){
				wfInstNum = 1;
			}
		}
		JSONObject parm = new JSONObject();
		switch (optType) {
		case "start":
			parm.put("userId", operator);
			parm.put("gnmkId", gnmkId);
			wfInstNum = wfService.startWorkflow(parm);
			session.setAttribute("wfInstNum", wfInstNum);
			session.setAttribute("currAssigner", operator);
			return wfInstNum;
		case "commit":
			parm.put("userId", operator);
			parm.put("gnmkId", gnmkId);
			parm.put("optCode", "C");
			parm.put("wfInstNum", wfInstNum);
			parm.put("comments", operator+" commit task");
			parm.put("nextUserIds", nextUserIds);
			wfService.operateTask(operator, parm);
			break;
		default:
			break;
		}
		return "success";
	}*/
}
