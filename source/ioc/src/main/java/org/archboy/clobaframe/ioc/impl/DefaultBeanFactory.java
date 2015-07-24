package org.archboy.clobaframe.ioc.impl;

import org.archboy.clobaframe.ioc.BeanDefinition;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Stack;
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
import org.archboy.clobaframe.setting.application.impl.DefaultApplicationSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
public class DefaultBeanFactory implements BeanFactory { //, BeanDefinitionBuilder {

	private static final String placeholderRegex = "^\\$\\{([\\w\\.-]+)(\\:([\\w\\.\\:\\/-]*))?\\}$";
	private static final Pattern placeholderPattern = Pattern.compile(placeholderRegex);
	
	private ResourceLoader resourceLoader;
	private String beanDefineFileName;
	private boolean requiredPlaceholderValue;
	private PlaceholderValueResolver placeholderValueResolver;
	
	private List<BeanDefinition> beanDefinitions = new ArrayList<BeanDefinition>();
	
	private Collection<Object> prebuildObjects; // the object that builded outside this factory.
	
	private Collection<BeanFactoryCloseEventListener> closeEventListeners = new ArrayList<BeanFactoryCloseEventListener>();
	
	// keep the current initializing bean id
	// to prevent infinite loop
	private Stack<String> initializingBeanIds = new Stack<String>();
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {};
	
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
		this.placeholderValueResolver = placeholderValueResolver;
		this.requiredPlaceholderValue = requiredPlaceholderValue;
		this.prebuildObjects = prebuildObjects;
		this.resourceLoader = resourceLoader;
		init();
	}
	
	public DefaultBeanFactory(
			ResourceLoader resourceLoader,
			ApplicationSetting applicationSetting) throws Exception {
		
		Assert.notNull(resourceLoader);
		Assert.notNull(applicationSetting);

		this.beanDefineFileName = (String)applicationSetting.getValue(
				ApplicationSettingPlaceholderValueResolver.SETTING_KEY_BEAN_DEFINE_FILE_NAME);
		this.placeholderValueResolver = new ApplicationSettingPlaceholderValueResolver(applicationSetting);
		this.requiredPlaceholderValue = Boolean.parseBoolean(applicationSetting.getValue(
				ApplicationSettingPlaceholderValueResolver.SETTING_KEY_REQUIRED_PLACEHOLDER_VALUE,
				ApplicationSettingPlaceholderValueResolver.DEFAULT_REQUIRED_PLACEHOLDER_VALUE).toString());
		this.prebuildObjects = Arrays.asList(resourceLoader, applicationSetting);
		this.resourceLoader = resourceLoader;
		
		init();
	}

	public DefaultBeanFactory(String... applicationConfigFileName) throws Exception{
		Assert.notNull(applicationConfigFileName);
		
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		ApplicationSetting applicationSetting = new DefaultApplicationSetting(
				resourceLoader, null, applicationConfigFileName);
		
		this.beanDefineFileName = (String)applicationSetting.getValue(
				ApplicationSettingPlaceholderValueResolver.SETTING_KEY_BEAN_DEFINE_FILE_NAME);
		this.placeholderValueResolver = new ApplicationSettingPlaceholderValueResolver(applicationSetting);
		this.requiredPlaceholderValue = Boolean.parseBoolean(applicationSetting.getValue(
				ApplicationSettingPlaceholderValueResolver.SETTING_KEY_REQUIRED_PLACEHOLDER_VALUE,
				ApplicationSettingPlaceholderValueResolver.DEFAULT_REQUIRED_PLACEHOLDER_VALUE).toString());
		this.prebuildObjects = Arrays.asList(resourceLoader, applicationSetting);
		this.resourceLoader = resourceLoader;
		
		init();
	}

	public void setBeanDefineFileName(String beanDefineFileName) {
		this.beanDefineFileName = beanDefineFileName;
	}

	public void setRequiredPlaceholderValue(boolean requiredPlaceholderValue) {
		this.requiredPlaceholderValue = requiredPlaceholderValue;
	}

	public void setPlaceholderValueResolver(PlaceholderValueResolver placeholderValueResolver) {
		this.placeholderValueResolver = placeholderValueResolver;
	}

	public void setPrebuildObjects(Collection<Object> prebuildObjects) {
		this.prebuildObjects = prebuildObjects;
	}
	
	@Override
	public void addCloseEventListener(BeanFactoryCloseEventListener eventListener) {
		closeEventListeners.add(eventListener);
	}
	
	public void init() throws Exception {
		loadPreBuildObject(this);
		loadPreBuildObjects();
		loadBeanDefine(resourceLoader);
	}
	
	private void loadPreBuildObjects() {
		if (prebuildObjects == null || prebuildObjects.isEmpty()) {
			return;
		}
		
		for (Object object : prebuildObjects) {
			loadPreBuildObject(object);
		}
	}
	
	private void loadPreBuildObject(Object object) {
		Class<?> clazz = object.getClass();
		Class<?>[] interfaces = clazz.getInterfaces();
		Annotation[] annotations = clazz.getDeclaredAnnotations();
		Method[] methods = clazz.getDeclaredMethods();
		String id = StringUtils.uncapitalize(clazz.getSimpleName());
		BeanDefinition bean = new BeanDefinition(id, clazz, object, 
				interfaces, annotations, 
				methods, null, null, true);
		beanDefinitions.add(bean);
	}
	
	private void loadBeanDefine(ResourceLoader resourceLoader) throws Exception {
		if (StringUtils.isEmpty(beanDefineFileName)) {
			return;
		}
		
		Resource resource = resourceLoader.getResource(beanDefineFileName);
		if (!resource.exists()) {
			throw new FileNotFoundException(beanDefineFileName);
		}
		
		Collection<Map<String, Object>> defineClassNames = getDefineClassNames(resource);
		List<BeanDefinition> uninitBeans = buildUninitBeans(defineClassNames);
		beanDefinitions.addAll(uninitBeans);
	}

	private List<BeanDefinition> buildUninitBeans(Collection<Map<String, Object>> defineClassNames) throws 
			ClassNotFoundException, InstantiationException, IllegalAccessException {
		
		List<BeanDefinition> uninitBeans = new ArrayList<BeanDefinition>();
		
		for (Map<String, Object> defineClassName : defineClassNames) {
			Class<?> clazz = Class.forName((String)defineClassName.get("class"));
			String id = (String)defineClassName.get("id");
			
			if (StringUtils.isEmpty(id)) {
				id = StringUtils.uncapitalize(clazz.getSimpleName());
			}
			
			Object object = clazz.newInstance();
			
			Class<?>[] interfaces = clazz.getInterfaces();
			Method[] methods = clazz.getDeclaredMethods();
			Annotation[] annotations = clazz.getDeclaredAnnotations();
			
			String initMethodName = null;
			String disposeMethodName = null;
			
			for(Method method : methods) {
				if (method.getAnnotation(PostConstruct.class) != null){
					initMethodName = method.getName();
				}else if (method.getAnnotation(PreDestroy.class) != null){
					disposeMethodName = method.getName();
				}
			}
			
			BeanDefinition bean = new BeanDefinition(id, clazz, object, 
					interfaces, annotations, 
					methods, initMethodName, disposeMethodName, false);
			uninitBeans.add(bean);
		}
		
		return uninitBeans;
	}
	
	private Collection<Map<String, Object>> getDefineClassNames(Resource resource) throws IOException {
		List<Map<String, Object>> defineClassNames = null;
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
	public Object get(String id) {
		Assert.hasText(id);
		for(BeanDefinition bean : beanDefinitions){
			if (id.equals(bean.getId())){
				if (!bean.isInitialized()){
					try{
						initBean(bean);
					}catch(ClassNotFoundException | 
							IllegalAccessException | 
							IllegalArgumentException | 
							InvocationTargetException e){
						throw new IllegalArgumentException(
								String.format("Can not initialize bean [%s], message: %s", 
										bean.getClazz().getName(), e.getMessage()),
								e);
					}
				}
				return bean.getObject();
			}
		}
		
		return null;
	}

//	@Override
//	public BeanDefinition getDefinition(String id) {
//		Assert.hasText(id);
//		for(BeanDefinition bean : beans){
//			if (id.equals(bean.getId())){
//				return bean;
//			}
//		}
//		return null;
//	}
	
	@Override
	public <T> T get(Class<T> clazz) {
		Assert.notNull(clazz);
		Collection<T> objects = list(clazz);
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
	public <T> Collection<T> list(Class<T> clazz) {
		Assert.notNull(clazz);
		
		Collection<BeanDefinition> matchBeans = new ArrayList<BeanDefinition>();
		for(BeanDefinition bean : beanDefinitions) {
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
		
		for(BeanDefinition bean : matchBeans) {
			if (!bean.isInitialized()) {
				try{
					initBean(bean);
				}catch(ClassNotFoundException | 
						IllegalAccessException | 
						IllegalArgumentException |
						InvocationTargetException e){
					throw new IllegalArgumentException(
							String.format("Can not initialize bean [%s], message: %s",
									bean.getClazz().getName(), e.getMessage()), 
							e);
				}
			}

			objects.add((T)bean.getObject());
		}
		
		return objects;
	}

	@Override
	public Collection<Object> listByAnnotation(Class<? extends Annotation> clazz) {
		Assert.notNull(clazz);
		
		Collection<BeanDefinition> matchBeans = new ArrayList<BeanDefinition>();
		for(BeanDefinition bean : beanDefinitions) {
			for(Annotation c : bean.getAnnotations()){
				if (clazz.equals(c.annotationType())) {
					matchBeans.add(bean);
				}
			}
		}
		
		Collection<Object> objects = new ArrayList<Object>();
		
		for(BeanDefinition bean : matchBeans) {
			if (!bean.isInitialized()) {
				try{			
					initBean(bean);
				}catch(ClassNotFoundException | 
							IllegalAccessException | 
							IllegalArgumentException |
							InvocationTargetException e) {
					throw new IllegalArgumentException(
							String.format("Can not initialize bean [%s], message: %s",
									clazz.getName(), e.getMessage()), 
							e);
				}
			}
			objects.add(bean.getObject());
		}
		
		return objects;
	}
	
	private void initBean(BeanDefinition bean) throws 
			ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		
		if (!initializingBeanIds.empty() && initializingBeanIds.contains(bean.getId())){
			return;
		}
		
		initializingBeanIds.push(bean.getId());
		
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
					Collection<?> targetObjects = list(dataType);
					
					if (targetObjects.isEmpty()){
						if (autowired == null || autowired.required()) {
							throw new IllegalArgumentException(
									String.format("There is no match type to inject field [%s#%s]",
											clazz.getName(), field.getName()));
						}
					}else{
						field.set(obj, targetObjects);
					}
				}else{
					Object targetObject = get(dataType);
					
					if (targetObject == null) {
						if (autowired == null || autowired.required()) {
							throw new IllegalArgumentException(
									String.format("There is no match type to inject field [%s#%s]",
											clazz.getName(), field.getName()));
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
				
				if (targetValue == null || 
						(targetValue instanceof String && 
						targetValue.equals(StringUtils.EMPTY))) {
					if (requiredPlaceholderValue) {
						throw new IllegalArgumentException(
								String.format("Can not resolve the placeholder [%s] for field [%s#%s]",
										placeholder, clazz.getName(), field.getName()));
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
						throw new IllegalArgumentException(
								String.format("Can not resolve the placeholder [%s] for field [%s#%s], type cast error.",
										placeholder, clazz.getName(), field.getName()));
					}
				}else if(dataType.equals(Long.class)){
					if (targetValue instanceof Long){
						field.setLong(obj, (Long)targetValue);
					}else if (targetValue instanceof String) {
						field.setLong(obj, Long.parseLong((String)targetValue));
					}else {
						throw new IllegalArgumentException(
								String.format("Can not resolve the placeholder [%s] for field [%s#%s], type cast error.",
										placeholder, clazz.getName(), field.getName()));
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
		
		String initMethodName = bean.getInitMethodName();
		if (initMethodName != null) {
			Method initMethod = getMethod(bean.getMethods(), initMethodName);
			initMethod.invoke(obj);
		}
		
		bean.setInitialized(true);
		
		initializingBeanIds.pop();
	}
	
	@Override
	public void close() throws Exception {
		for(BeanDefinition bean : beanDefinitions) {
			String disposeMethodName = bean.getDisposeMethodName();
			if (disposeMethodName != null) {
				Method disposeMethod = getMethod(bean.getMethods(), disposeMethodName);
				disposeMethod.invoke(bean.getObject());
			}
		}
		
		// notify event listeners
		for(BeanFactoryCloseEventListener eventListener : closeEventListeners) {
			eventListener.onClose();
		}
	}
	
	private Method getMethod(Method[] methods, String name) {
		for (Method method : methods) {
			if (name.equals(method.getName())) {
				return method;
			}
		}
		return null;
	}
}
