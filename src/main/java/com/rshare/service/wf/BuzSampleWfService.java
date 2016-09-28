package com.rshare.service.wf;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service(value="erpTest")
public class BuzSampleWfService extends WfBaseService{

	@Override
	public Map<String, Object> doBeforeStart(Map<String, Object> parm) throws WfException {
		return super.doBeforeStart(parm);
	}

	@Override
	public Map<String, Object> doAfterStart(Map<String, Object> parm) throws WfException {
		// TODO Auto-generated method stub
		return super.doAfterStart(parm);
	}
	
	
}
