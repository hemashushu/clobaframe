package org.archboy.clobaframe.ioc;

import java.lang.annotation.Annotation;
import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.archboy.clobaframe.ioc.impl.DefaultBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Bean factory.
 * 
 * The default bean factory implement {@link DefaultBeanFactory} is a simple and
 * function limited bean factory that focus on the startup/boot performance for
 * desktop or mobile application.
 * 
 * Background: commonly the complete functional IoC container such Spring IoC will spends about 300-500ms
 * for booting (i5@3.4GHz CPU/12G RAM), by using the XML bean define instead of the package base component scan
 * will improve the boot performance, but it maybe still not suitable for the desktop application that
 * needs fast boot and show the UI immediately.
 * 
 * {@link DefaultBeanFactory} can handle the bean inject by class type and the value inject by placeholder.
 * It lookups the {@link Inject} and {@link Autowired} and {@link Value} annotations that on the fields, and 
 * maintains bean life cycle by the {@link PostConstruct} and {@link PreDestroy} annotations.
 *  
 * All bean class name must be listed on a JSON file that named 'bean.json' or defined 
 * in the application settings with key 'clobaframe.ioc.beanDefineFileName'.
 * 
 * @author yang
 */
public interface BeanFactory {
	
	/**
	 * List by class or interface.
	 * 
	 * @param <T>
	 * @param clazz
	 * @return 
	 */
	<T> Collection<T> list(Class<T> clazz);
	
	/**
	 * 
	 * @param clazz
	 * @return 
	 */
	Collection<Object> listByAnnotation(Class<? extends Annotation> clazz);
	
	/**
	 * 
	 * @param id
	 * @return 
	 */
	Object get(String id);
	
	/**
	 * Get by class or interface.
	 * 
	 * @param <T>
	 * @param clazz
	 * @return 
	 */
	<T> T get(Class<T> clazz);
	
	/**
	 * 
	 * @throws Exception 
	 */
	void close() throws Exception;
	
	/**
	 * 
	 * @param eventListener 
	 */
	void addCloseEventListener(BeanFactoryCloseEventListener eventListener);
}
