package com.rshare.service.wf.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 业务实体主表的功能变量
 * 
 * 具体哪个实体对应哪个WF，用wf-entity.properties维护
 * 
 * @author yuxiaobin
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FuncVar {
	
	/**
	 * 功能变量中文描述
	 * @return
	 */
	String value() default "功能变量";
	
	/**
	 * 数据库字段名
	 * @return
	 */
	String dbColunm();

}
