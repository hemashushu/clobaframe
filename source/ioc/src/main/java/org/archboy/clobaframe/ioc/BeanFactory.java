package org.archboy.clobaframe.ioc;

import java.util.Collection;

/**
 *
 * @author yang
 */
public interface BeanFactory {
	
	//Object getBean(String id);
	
	<T> Collection<T> listBeans(Class<T> clazz);
	
	<T> T getBean(Class<T> clazz);
	
	void close() throws Exception;
}
