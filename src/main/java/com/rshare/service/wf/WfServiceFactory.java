package com.rshare.service.wf;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.io.ClassPathResource;

public class WfServiceFactory implements BeanFactoryAware {
	
	private static final String ERROR_NO_BEAN_NAME_DEFINED = "Cannot find beanName for class=%s,Pls use @Component or @Service(value=\"%s\") or value=\"%s\"";
	private static final String ERROR_NO_BEAN_DEFINED_FOR_GNMKID = "No Bean defined in wf-config.properties for gnmkId=%s";
	
	private static final Logger log = Logger.getLogger(WfServiceFactory.class);
	private static final Map<String, WfBaseService> map = new HashMap<String, WfBaseService>();

	private BeanFactory beanFactory;

	@Override
	public void setBeanFactory(BeanFactory arg0) throws BeansException {
		beanFactory = arg0;
	}

	@PostConstruct
	public void init() throws WfException {
		Properties prop = new Properties();
		map.clear();
		try (InputStream is = new ClassPathResource("wf-config.properties").getInputStream();) {
			if(is==null){
				throw new WfException("configNotFoundError", "wf-config.properties not found in classpath");
			}
			prop.load(is);
			Iterator<String> it = prop.stringPropertyNames().iterator();
			while (it.hasNext()) {
				String entry = it.next();
				String beanName = prop.getProperty(entry);
				if(beanFactory.containsBean(beanName)){
					map.put(entry, (WfBaseService) beanFactory.getBean(beanName));
				}else if(beanFactory.containsBean(entry)){
					map.put(entry, (WfBaseService) beanFactory.getBean(entry));
				}else{
					String errorMsg = String.format(ERROR_NO_BEAN_NAME_DEFINED, beanName,beanName,entry);
					log.error(errorMsg);
					throw new WfException("NoBeanDefinedError",errorMsg);
				}
			}
		} catch (IOException e) {
			log.error(e);
			throw new WfException(e.getMessage());
		}
	}
	
	public WfBaseService getBean(String gnmkId) throws WfException{
		if(map.containsKey(gnmkId)){
			return map.get(gnmkId);
		}else{
			throw new WfException("NoBeanDefinedError",String.format(ERROR_NO_BEAN_DEFINED_FOR_GNMKID, gnmkId));
		}
	}

}
