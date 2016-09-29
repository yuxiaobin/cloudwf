package com.rshare.service.wf;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service(value="com.rshare.service.wf.BuzSampleWfService")
public class BuzSampleWfService extends WfBaseService{

	@Override
	public Map<String, Object> doBeforeStart(Map<String, Object> parm) throws WfException {
		System.out.println("before start, do something here");
		return super.doBeforeStart(parm);
	}

	@Override
	public Map<String, Object> doAfterStart(Map<String, Object> parm) throws WfException {
		System.out.println("after start, do something here, update wfInstNum");
		Integer wfInstNum = (Integer) parm.get("wfInstNum");
		System.out.println("wfInstNum is "+wfInstNum);
		return super.doAfterStart(parm);
	}
	
	
}
