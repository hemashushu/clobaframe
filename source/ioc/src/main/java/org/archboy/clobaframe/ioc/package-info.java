/**
 * Bean factory.
 * 
 * The default bean factory implement {@link DefaultBeanFactory} is a simple and
 * function limited bean factory that focus on the startup/boot performance for
 * desktop or mobile application.
 * 
 * {@link DefaultBeanFactory} can handle the bean inject by class type and the value inject by placeholder.
 * It lookups the {@link Inject} and {@link Autowired} and {@link Value} annotations that on the fields, and 
 * maintains bean life cycle by the {@link PostConstruct} and {@link PreDestroy} annotations. But does not support
 * inherit annotation (suck as @Inject and @Value).
 *  
 * All bean class name must be listed on a JSON file that named 'bean.json' or defined 
 * in the application settings with key 'clobaframe.ioc.beanDefineFileName'.
 * 
 * The {@link DefaultBeanFactory} implements the Spring {@link BeanFactory} and {@link ListableBeanFactory} 
 * and {@link ApplicationEventMulticaster} and {@link Closeable} interfaces,
 * 
 * Note: only a few methods are implemented:
 *		list by class
 *		list by annotation
 *		get by id
 *		get by class
 *		close()
 *		addApplicationListener
 * 
 */
package org.archboy.clobaframe.ioc;
