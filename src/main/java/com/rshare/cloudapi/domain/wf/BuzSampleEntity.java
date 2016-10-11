package com.rshare.cloudapi.domain.wf;

import com.rshare.service.wf.annotations.FuncEntity;
import com.rshare.service.wf.annotations.FuncVar;

/**
 * 业务关联对象实体
 * 
 * @author yuxiaobin
 *
 */
@FuncEntity(tableName = "Test", refMkid = "erpTest")
public class BuzSampleEntity {
	
	private String id;
	private String name;
	@FuncVar(value="数量", dbColunm = "test_amount")
	private Integer amount;
	@FuncVar(dbColunm = "test_age")
	private Integer size;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	
}
