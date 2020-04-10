package cn.com.dyg.work.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


@Component
public class SpringContextUtil implements ApplicationContextAware{

	private static ApplicationContext applicationContext = null;

	/**
	 * 获取applicationContext
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (SpringContextUtil.applicationContext == null) {
			SpringContextUtil.applicationContext = applicationContext;
		}
	}

	/**
	 * 通过name获取 Bean
	 */
	public static Object getBean(String name) {
		return getApplicationContext().getBean(name);
	}
}
