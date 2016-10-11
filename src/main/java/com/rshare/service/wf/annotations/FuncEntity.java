package com.rshare.service.wf.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 业务实体主表注解
 * 
 * @author yuxiaobin
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FuncEntity {
	
	/**
	 * 业务主表的数据库表名
	 * @return
	 */
	String tableName();
	
	String refMkid();//TODO:

}
