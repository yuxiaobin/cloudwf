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

import com.alibaba.fastjson.JSONObject;
import com.rshare.cloudapi.domain.sys.Users;
import com.rshare.service.wf.WfApiConfig;
import com.rshare.service.wf.WfBaseService;
import com.rshare.service.wf.WfException;
import com.rshare.service.wf.WfServiceFactory;

@Controller
@RequestMapping(value="/wf")
public class WfCommonController {
	
	Logger log = Logger.getLogger(WfCommonController.class);
	
	private static final String PARM_NAME_GNMKID = "gnmkId";
	private static final String PARM_NAME_WFINSTNUM = "wfInstNum";
	private static final String PARM_NAME_OPTCODE = "optCode";
	private static final String PARM_NAME_USERID = "userId";
	private static final String PARM_NAME_CALLBACK_URL = "callbackUrl";
	
	

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
	public Object getOptions(@RequestBody JSONObject param, HttpSession session) throws WfException{
		String gnmkId = param.getString(PARM_NAME_GNMKID);
		WfBaseService wfService = serviceFactory.getBean(gnmkId);
		param.put("optType", WfBaseService.WfOptionTypes.OPT_TYPE_OPTIONS);
		Users user = (Users) session.getAttribute("userinfo");
		if(user!=null){
			param.put("userId", user.getId());
		}
		JSONObject result = wfService.execute(param);
		return result.getJSONArray("array");
	}
	
	@RequestMapping(value="/task")
	public String loadOperatePage(HttpServletRequest req,HttpSession session) throws WfException{
		String gnmkId = req.getParameter(PARM_NAME_GNMKID);
		String wfInstNumStr = req.getParameter(PARM_NAME_WFINSTNUM);
		String optCode = req.getParameter(PARM_NAME_OPTCODE);
		String callbackUrl = req.getParameter(PARM_NAME_CALLBACK_URL);
		Users user = (Users) session.getAttribute("userinfo");
		String userId = null;
		if(user!=null){
			userId = user.getId();
		}
		if(StringUtils.isEmpty(gnmkId) || StringUtils.isEmpty(wfInstNumStr) 
				|| StringUtils.isEmpty(optCode) || StringUtils.isEmpty(userId)){
			throw new WfException("InvalidParameter","参数为空");
		}
		StringBuilder sb = new StringBuilder(WfApiConfig.WF_SERVER_URL);
		sb.append("?")
			.append(PARM_NAME_GNMKID).append("=").append(gnmkId)
			.append("&").append(PARM_NAME_WFINSTNUM).append("=").append(wfInstNumStr)
			.append("&").append(PARM_NAME_OPTCODE).append("=").append(optCode)
			.append("&").append(PARM_NAME_USERID).append("=").append(userId)
			.append("&").append(PARM_NAME_CALLBACK_URL).append("=").append(callbackUrl)
			;
		return "redirect:"+sb.toString();
	}
	
	
	
}
