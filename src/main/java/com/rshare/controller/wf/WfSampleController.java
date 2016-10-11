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
	
}
