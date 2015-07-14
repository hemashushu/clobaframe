package org.archboy.clobaframe.ioc.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.ioc.BeanFactory;
import org.archboy.clobaframe.ioc.BeanFactoryCloseEventListener;
import org.archboy.clobaframe.ioc.PlaceholderValueResolver;
import org.archboy.clobaframe.setting.application.ApplicationSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
public class DefaultBeanFactory implements BeanFactory {

	private static final String placeholderRegex = "^\\$\\{([\\w\\.-]+)(\\:([\\w\\.-]+))?\\}$";
	private static final Pattern placeholderPattern = Pattern.compile(placeholderRegex);
	
	private ResourceLoader resourceLoader;
	private String beanDefineFileName;
	private boolean requiredPlaceholderValue;
	private PlaceholderValueResolver placeholderValueResolver;
	
	private List<Bean> beans = new ArrayList<Bean>();
	
	private Collection<Object> prebuildObjects; // the object that builded outside this factory.
	
	private Collection<BeanFactoryCloseEventListener> closeEventListeners = new ArrayList<BeanFactoryCloseEventListener>();
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private TypeReference<List<String>> typeReference = new TypeReference<List<String>>() {};
	
	/**
	 * 
	 * @param resourceLoader
	 * @param placeholderValueResolver
	 * @param beanDefineFileName
	 * @param requiredPlaceholderValue
	 * @param prebuildObjects This factory does NOT maintain these object's life cycle.
	 * @throws Exception 
	 */
	public DefaultBeanFactory(
			ResourceLoader resourceLoader,
			PlaceholderValueResolver placeholderValueResolver,
			String beanDefineFileName,
			boolean requiredPlaceholderValue,
			Collection<Object> prebuildObjects) throws Exception {
		
		Assert.notNull(resourceLoader);
		Assert.hasText(beanDefineFileName);
		Assert.notNull(placeholderValueResolver);
		
		this.beanDefineFileName = beanDefineFileName;
		this.resourceLoader = resourceLoader;
		this.placeholderValueResolver = placeholderValueResolver;
		this.requiredPlaceholderValue = requiredPlaceholderValue;
		this.prebuildObjects = prebuildObjects;
		
		init();
	}
	
	public DefaultBeanFactory(
			ResourceLoader resourceLoader,
			ApplicationSetting applicationSetting) throws Exception {
		
		Assert.notNull(resourceLoader);
		Assert.notNull(applicationSetting);

		this.resourceLoader = resourceLoader;
		this.beanDefineFileName = (String)applicationSetting.getValue(
				ApplicationSettingPlaceholderValueResolver.SETTING_KEY_BEAN_DEFINE_FILE_NAME);
		this.placeholderValueResolver = new ApplicationSettingPlaceholderValueResolver(applicationSetting);
		this.requiredPlaceholderValue = (Boolean)applicationSetting.getValue(
				ApplicationSettingPlaceholderValueResolver.SETTING_KEY_REQUIRED_PLACEHOLDER_VALUE,
				ApplicationSettingPlaceholderValueResolver.DEFAULT_REQUIRED_PLACEHOLDER_VALUE);
		this.prebuildObjects = Arrays.asList(resourceLoader, applicationSetting);
		
		init();
	}

	
	@Override
	public void addCloseEventListener(BeanFactoryCloseEventListener eventListener) {
		closeEventListeners.add(eventListener);
	}
	
	private void init() throws Exception {
		loadPreBuildObjects();
		loadBeanDefine();
	}
	
	private void loadPreBuildObjects() {
		if (prebuildObjects == null || prebuildObjects.isEmpty()) {
			return;
		}
		
		for (Object object : prebuildObjects) {
			Class<?> clazz = object.getClass();
			
			Class<?>[] interfaces = clazz.getInterfaces();
			
			Bean bean = new Bean(clazz, object, interfaces, null, null, false);
			beans.add(bean);
		}
	}
	
	private void loadBeanDefine() throws Exception {
		if (StringUtils.isEmpty(beanDefineFileName)) {
			return;
		}
		
		Resource resource = resourceLoader.getResource(beanDefineFileName);
		if (!resource.exists()) {
			throw new FileNotFoundException(beanDefineFileName);
		}
		
		Collection<String> defineClassNames = getDefineClassNames(resource);
		List<Bean> uninitBeans = buildUninitBeans(defineClassNames);
		beans.addAll(uninitBeans);
	}

	private List<Bean> buildUninitBeans(Collection<String> defineClassNames) throws 
			ClassNotFoundException, InstantiationException, IllegalAccessException {
		
		List<Bean> uninitBeans = new ArrayList<Bean>();
		
		for (String defineClassName : defineClassNames) {
			Class<?> clazz = Class.forName(defineClassName);
			Object object = clazz.newInstance();
			
			Class<?>[] interfaces = clazz.getInterfaces();
			Method initMethod = null;
			Method closeMethod = null;
			
			for(Method m : clazz.getDeclaredMethods()) {
				if (m.getAnnotation(PostConstruct.class) != null){
					initMethod = m;
				}else if (m.getAnnotation(PreDestroy.class) != null){
					closeMethod = m;
				}
			}
			
			Bean bean = new Bean(clazz, object, interfaces, initMethod, closeMethod, false);
			uninitBeans.add(bean);
		}
		
		return uninitBeans;
	}
	
	private Collection<String> getDefineClassNames(Resource resource) throws IOException {
		List<String> defineClassNames = null;
		InputStream in = null;
		try{
			in = resource.getInputStream();
			defineClassNames = objectMapper.readValue(in, typeReference);
		}finally{
			IOUtils.closeQuietly(in);
		}
		return defineClassNames;
	}

	@Override
	public <T> T getBean(Class<T> clazz) {
		Assert.notNull(clazz);
		Collection<T> objects = listBeans(clazz);
		if (objects.isEmpty()) {
			return null;
		}
		
		if (objects.size() > 1) {
			throw new IllegalArgumentException(
					"More than one object are assign from this class: " + clazz.getName());
		}
		
		return objects.iterator().next();
	}

	@Override
	public <T> Collection<T> listBeans(Class<T> clazz) {
		Assert.notNull(clazz);
		
		Collection<Bean> matchBeans = new ArrayList<Bean>();
		for(Bean bean : beans) {
			if (bean.getClazz().equals(clazz)){
				matchBeans.add(bean);
			}else{
				for(Class<?> c : bean.getInterfaces()){
					if (clazz.equals(c)) {
						matchBeans.add(bean);
					}
				}
			}
		}
		
		Collection<T> objects = new ArrayList<T>();
		
		try{
			for(Bean bean : matchBeans) {
				if (!bean.isInited()) {
					initBean(bean);
				}
				objects.add((T)bean.getObject());
			}
		}catch(Exception e){
			throw new RuntimeException("Can not initialize bean.", e);
		}
		
		return objects;
	}
	
	private void initBean(Bean bean) throws 
			ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Class<?> clazz = bean.getClazz();
		Object obj = bean.getObject();
		
		for(Field field : clazz.getDeclaredFields()){
			
			Inject inject = field.getAnnotation(Inject.class);
			Autowired autowired = field.getAnnotation(Autowired.class);
			
			if (inject != null || autowired != null) {
				field.setAccessible(true);
				
				Class<?> dataType = field.getType();
				
				if (dataType.equals(Collection.class) || dataType.equals(List.class)){
					// inject collection field.
					ParameterizedType pType = (ParameterizedType)field.getGenericType();
					dataType = (Class<?>)pType.getActualTypeArguments()[0];
					Collection<?> targetObjects = listBeans(dataType);
					
					if (targetObjects.isEmpty()){
						if (autowired == null || autowired.required()) {
							throw new IllegalArgumentException("No beans to inject field: " + field.getName());
						}
					}else{
						field.set(obj, targetObjects);
					}
				}else{
					Object targetObject = getBean(dataType);
					
					if (targetObject == null) {
						if (autowired == null || autowired.required()) {
							throw new IllegalArgumentException("No bean to inject field: " + field.getName());
						}
					}else{
						field.set(obj, targetObject);
					}
				}
				
				continue;
			}
			
			Value value = field.getAnnotation(Value.class);
			if (value != null) {
				field.setAccessible(true);
				
				String placeholder = value.value();
				Object targetValue = null;
				
				Matcher matcher = placeholderPattern.matcher(placeholder);
				if (matcher.matches()) {
					if (matcher.groupCount()== 3){
						targetValue  = placeholderValueResolver.getValue(matcher.group(1), matcher.group(3));
					}else{
						targetValue  = placeholderValueResolver.getValue(matcher.group(1));
					}
				}else{
					targetValue = placeholder;
				}
				
				if (targetValue == null) {
					if (requiredPlaceholderValue) {
						throw new IllegalArgumentException("Can not find the value of placeholder " + placeholder);
					}else{
						continue;
					}
				}
				
				Class<?> dataType = field.getType();
				if (dataType.equals(String.class)) {
					field.set(obj, targetValue.toString());
				}else if(dataType.equals(Integer.class)){
					if (targetValue instanceof Integer){
						field.setInt(obj, (Integer)targetValue);
					}else if (targetValue instanceof String) {
						field.setInt(obj, Integer.parseInt((String)targetValue));
					}else{
						throw new ClassCastException("Can not cast the value of placeholder " + placeholder + " to integer." );
					}
				}else if(dataType.equals(Long.class)){
					if (targetValue instanceof Long){
						field.setLong(obj, (Long)targetValue);
					}else if (targetValue instanceof String) {
						field.setLong(obj, Long.parseLong((String)targetValue));
					}else {
						throw new ClassCastException("Can not cast the value of placeholder " + placeholder + " to long." );
					}
				}else if(dataType.equals(Boolean.class)){
					if (targetValue instanceof Boolean){
						field.setBoolean(obj, (Boolean)targetValue);
					}else if (targetValue instanceof String) {
						field.setBoolean(obj, Boolean.parseBoolean((String)targetValue));
					}else{
						throw new ClassCastException("Can not cast the value of placeholder " + placeholder + " to boolean." );
					}
				}
			}
		}
		
		Method initMethod = bean.getInitMethod();
		if (initMethod != null) {
			initMethod.invoke(obj);
		}
		
		bean.finishInited();
	}
	
	@Override
	public void close() throws Exception {
		for(Bean bean : beans) {
			Method m = bean.getCloseMethod();
			if (m != null) {
				m.invoke(bean.getObject());
			}
		}
		
		// notify event listeners
		for(BeanFactoryCloseEventListener eventListener : closeEventListeners) {
			eventListener.onClose();
		}
	}
}
