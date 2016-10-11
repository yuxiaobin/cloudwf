package com.rshare.service.wf.annotations;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Component
public class WfEntityBeanFactory{
	
	Logger log = Logger.getLogger(WfEntityBeanFactory.class);
	
	private static final String WARN_NO_FUNCENTITY_DEFINED = "This bean[%s] has not defined @FuncEntity, this record ignored";
	private static final String WARN_NO_TABLE_DEFINED = "This bean[%s] has defined @FuncEntity(tableName=\"\"), tableName should not be empty.  this record ignored";

	public static final String PARM_VAR_CODE = "varCode";
	public static final String PARM_VAR_DESCP = "varDescp";
	
	private String[] entityPackages = {"com.rshare.cloudapi.domain"};//default package
	
	public String[] getEntityPackages() {
		return entityPackages;
	}

	public void setEntityPackages(String[] entityPackages) {
		this.entityPackages = entityPackages;
	}

	/**
	 * refMkid: 
	 * [
	 * 		{ 	varCode:xxx(table column), 
	 * 			varDescp: ""
	 * 		}
	 * ]
	 */
	private static final Map<String, JSONArray> funcVarsMap = new HashMap<String,JSONArray>();
	private static final Map<String, String> entityTableMap = new HashMap<String,String>();

	private Set<Class<?>> scanEntities(){
		if(entityPackages==null || entityPackages.length==0){
			return null;
		}
		Set<Class<?>> entitySet = new HashSet<Class<?>>();
		for(String packagePath : entityPackages){
			Reflections reflections = new Reflections(packagePath);
			Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(FuncEntity.class);
			if(classSet!=null){
				entitySet.addAll(classSet);
			}
		}
		return entitySet;
	}
	
	
	@SuppressWarnings("rawtypes")
	@PostConstruct
	public void loadEntities(){
		Set<Class<?>> entitySet = scanEntities();
		JSONObject json = null;
		JSONArray funcVarArray = new JSONArray();
		for(Class beanClass:entitySet){
			try{
				@SuppressWarnings("unchecked")
				FuncEntity funcEntity = (FuncEntity) beanClass.getAnnotation(FuncEntity.class);
				if(funcEntity==null){
					log.warn(String.format(WARN_NO_FUNCENTITY_DEFINED, beanClass.getName()));
					continue;
				}
				String tableName = funcEntity.tableName();
				if(StringUtils.isEmpty(tableName)){
					log.warn(String.format(WARN_NO_TABLE_DEFINED, beanClass.getName()));
					continue;
				}
				String refMkid = funcEntity.refMkid();
				entityTableMap.put(refMkid, tableName);
				Field[] fields = beanClass.getDeclaredFields();
				for(Field field :fields){
					if(field.isAnnotationPresent(FuncVar.class)){
						FuncVar funcVarName = (FuncVar) field.getAnnotation(FuncVar.class);
						json = new JSONObject();
						json.put(PARM_VAR_CODE, funcVarName.dbColunm());
						json.put(PARM_VAR_DESCP, StringUtils.isEmpty(funcVarName.value())?field.getName():funcVarName.value());
						funcVarArray.add(json);
					}
				}
				funcVarsMap.put(refMkid, funcVarArray);
			}catch(Exception e){
				log.error("loadEntities()",e);
			}
		}
	}
	
	/*public void init() throws IOException, WfException {
		Properties prop = new Properties();
		funcVarsMap.clear();
		try (InputStream is = new ClassPathResource("wf-entity.properties").getInputStream();) {
			if (is == null) {
				log.warn("wf-entity.properties not found in classpath, init WfEntityBeanFactory ignored");
				return;
			}
			prop.load(is);
		}
		Iterator<String> it = prop.stringPropertyNames().iterator();
		JSONObject json = null;
		JSONArray funcVarArray = new JSONArray();
		String refMkid = null;
		while (it.hasNext()) {
			refMkid = it.next();
			String beanName = prop.getProperty(refMkid);
			try{
				Class beanClass = Class.forName(beanName);
				@SuppressWarnings("unchecked")
				FuncEntity funcEntity = (FuncEntity) beanClass.getAnnotation(FuncEntity.class);
				if(funcEntity==null){
					log.warn(String.format(WARN_NO_FUNCENTITY_DEFINED, beanName));
					continue;
				}
				String tableName = funcEntity.tableName();
				if(StringUtils.isEmpty(tableName)){
					log.warn(String.format(WARN_NO_TABLE_DEFINED, beanName));
					continue;
				}
				refMkid = funcEntity.refMkid();
				entityTableMap.put(refMkid, tableName);
				Field[] fields = beanClass.getDeclaredFields();
				for(Field field :fields){
		            if(field.isAnnotationPresent(FuncVar.class)){
		            	FuncVar funcVarName = (FuncVar) field.getAnnotation(FuncVar.class);
		            	json = new JSONObject();
		            	json.put(PARM_VAR_CODE, funcVarName.dbColunm());
		            	json.put(PARM_VAR_DESCP, StringUtils.isEmpty(funcVarName.value())?field.getName():funcVarName.value());
		            	funcVarArray.add(json);
		            }
				}
				funcVarsMap.put(refMkid, funcVarArray);
			}catch(ClassNotFoundException e){
				log.error(String.format(ERROR_NO_BEAN_NAME_DEFINED, refMkid));
			}
		}
	}*/

	/**
	 * 获取工作流功能变量
	 * @param refMkid
	 * @return
	 */
	public JSONArray getFuncVariables(String refMkid){
		return funcVarsMap.get(refMkid);
	}
	
	/**
	 * 获取实体映射到表名
	 * @param refMkid
	 * @return
	 */
	public String getFuncEntityTable(String refMkid){
		return entityTableMap.get(refMkid);
	}
	
}
