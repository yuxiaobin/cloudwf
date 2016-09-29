package com.rshare.controller.wf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rshare.cloudapi.domain.sys.Users;
import com.rshare.service.wf.WfApiConfig;
import com.rshare.service.wf.WfBaseService;
import com.rshare.service.wf.WfBaseService.WfOptionTypes;
import com.rshare.service.wf.WfException;
import com.rshare.service.wf.WfServiceFactory;

@Controller
@RequestMapping(value="/wf")
public class WfCommonController {
	
	private static final Logger log = Logger.getLogger(WfCommonController.class);
	
	private static final String PARM_NAME_GNMKID = "gnmkId";
	private static final String PARM_NAME_WFINSTNUM = "wfInstNum";
	private static final String PARM_NAME_OPTCODE = "optCode";
	private static final String PARM_NAME_USERID = "userId";
	private static final String PARM_NAME_BASE_PATH = "basePath";
	private static final String PARM_NAME_COMMENTS = "comments";
	private static final String PARM_NAME_NEXTTASKID = "nextTaskId";
	private static final String PARM_NAME_NEXTUSERIDS = "nextUserIds";
	private static final String PARM_NAME_CALLBACK_URL = "callback_url";
	
	@Autowired
	WfServiceFactory serviceFactory;
	
	/**
	 * 
	 * @param param	{gnmkId:"abc", wfInstNum:1}
	 * @param session
	 * @return
	 * @throws WfException
	 */
	@RequestMapping(value="/options")
	@ResponseBody
	public Object getOptions(@RequestBody JSONObject param, HttpSession session) {
		String gnmkId = param.getString(PARM_NAME_GNMKID);
		Integer wfInstNum = param.getInteger(PARM_NAME_WFINSTNUM);
		if(StringUtils.isEmpty(gnmkId) || wfInstNum==null){
			log.warn("gnmkId or wfInstNum is empty, load options ignored");
			return new JSONArray(0);
		}
		param.put(PARM_NAME_OPTCODE, WfBaseService.WfOptionTypes.OPT_TYPE_OPTIONS);
		Users user = (Users) session.getAttribute("userinfo");
		if(user!=null){
			param.put("userId", user.getId());
		}
		try{
			WfBaseService wfService = serviceFactory.getBean(gnmkId);
			return wfService.execute(param).getJSONArray("array");
		}catch(WfException e){
			log.error("getOptions(): error when get options.", e);
			return new JSONArray(0);
		}
	}
	
	/**
	 * Open subpage to show Operation page: commit/reject/recall/...
	 * 
	 * @param req
	 * @param session
	 * @return
	 * @throws WfException
	 */
	@RequestMapping(value="/task")
	public String loadOperatePage(HttpServletRequest req,HttpSession session) throws WfException{
		String gnmkId = req.getParameter(PARM_NAME_GNMKID);
		String wfInstNumStr = req.getParameter(PARM_NAME_WFINSTNUM);
		String optCode = req.getParameter(PARM_NAME_OPTCODE);
		String userId = req.getParameter(PARM_NAME_USERID);
		String basePath = req.getParameter(PARM_NAME_BASE_PATH);//http://[ip:localhost]:8080/clouderp
		Users user = (Users) session.getAttribute("userinfo");
		if(user!=null && StringUtils.isEmpty(userId)){
			userId = user.getId();
		}
		if(StringUtils.isEmpty(gnmkId) || StringUtils.isEmpty(wfInstNumStr) 
				|| StringUtils.isEmpty(optCode) || StringUtils.isEmpty(userId)){
			throw new WfException("InvalidParameter","参数为空");
		}
		StringBuilder sb = new StringBuilder(WfApiConfig.WfApiOptions.OpenOperateTask.getUrl());
		sb.append("?")
			.append(PARM_NAME_GNMKID).append("=").append(gnmkId)
			.append("&").append(PARM_NAME_WFINSTNUM).append("=").append(wfInstNumStr)
			.append("&").append(PARM_NAME_OPTCODE).append("=").append(optCode)
			.append("&").append(PARM_NAME_USERID).append("=").append(userId);
		if(!StringUtils.isEmpty(basePath)){
			String full_path = basePath+"/wf/task/operate.do";
			sb.append("&").append(PARM_NAME_CALLBACK_URL).append("=").append(HtmlUtils.htmlEscape(full_path));
		}
		return "redirect:"+sb.toString();
	}
	
	/**
	 * 
	 * @param req
	 * @return
	 * @throws WfException 
	 */
	@RequestMapping(value="/task/operate")
	@ResponseBody
	public Object operateTask(HttpServletRequest req) throws WfException{
		String gnmkId = req.getParameter(PARM_NAME_GNMKID);
		WfBaseService wfService = serviceFactory.getBean(gnmkId);
		String optCode = req.getParameter(PARM_NAME_OPTCODE);
		if(StringUtils.isEmpty(optCode)){
			throw new WfException("InvalidOptCode","Invalid optCode null");
		}
		String wfInstNumStr = req.getParameter(PARM_NAME_WFINSTNUM);
		if(StringUtils.isEmpty(wfInstNumStr)){
			throw new WfException("InvalidWfInstNum","Invalid wfInstNum : null");
		}
		Integer wfInstNum = null;
		try{
			wfInstNum = Integer.parseInt(wfInstNumStr);
		}catch(NumberFormatException e){
			throw new WfException("InvalidWfInstNum","Invalid wfInstNum : "+wfInstNumStr+" is not a number");
		}
		JSONObject parm = new JSONObject();
		parm.put(PARM_NAME_USERID, req.getParameter(PARM_NAME_USERID));
		parm.put(PARM_NAME_GNMKID, gnmkId);
		parm.put(PARM_NAME_WFINSTNUM, wfInstNum);
		parm.put(PARM_NAME_OPTCODE, req.getParameter(PARM_NAME_OPTCODE));
		parm.put(PARM_NAME_COMMENTS, req.getParameter(PARM_NAME_COMMENTS));
		parm.put(PARM_NAME_NEXTTASKID, req.getParameter(PARM_NAME_NEXTTASKID));
		parm.put(PARM_NAME_NEXTUSERIDS, req.getParameter(PARM_NAME_NEXTUSERIDS));
		JSONObject result = wfService.execute(parm);
		String callback = req.getParameter("callback");
		if(StringUtils.isEmpty(callback)){
			return result;
		}else{
			StringBuilder sb = new StringBuilder();
			sb.append(callback).append("(").append(result).append(")");
			return sb.toString();
		}
	}
	
	@RequestMapping(value="/task/start")
	@ResponseBody
	public Object testStartWf(@RequestBody JSONObject parm,HttpSession session) throws WfException{
		String userId = parm.getString(PARM_NAME_USERID);
		String gnmkId = parm.getString(PARM_NAME_GNMKID);
		String optCode = parm.getString(PARM_NAME_OPTCODE);
		Users user = (Users) session.getAttribute("userinfo");
		if(user!=null && StringUtils.isEmpty(userId)){
			userId = user.getId();
		}
		if(userId==null){
			userId = "staff1";
		}
		if(gnmkId ==null){
			gnmkId = "erpTest";
		}
		if(optCode==null){
			optCode = WfOptionTypes.OPT_TYPE_START;
		}
		WfBaseService wfService = serviceFactory.getBean(gnmkId);
		parm.put(PARM_NAME_OPTCODE, optCode);
		parm.put(PARM_NAME_GNMKID, gnmkId);
		parm.put(PARM_NAME_USERID, userId);
		return wfService.execute(parm);
	}
	
}
