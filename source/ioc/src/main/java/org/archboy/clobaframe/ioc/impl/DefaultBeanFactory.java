package org.archboy.clobaframe.ioc.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Closeable;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.ioc.BeanFactoryClosedEvent;
import org.archboy.clobaframe.ioc.PlaceholderValueResolver;
import org.archboy.clobaframe.setting.application.ApplicationSetting;
import org.archboy.clobaframe.setting.application.impl.DefaultApplicationSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
public class DefaultBeanFactory implements ListableBeanFactory, Closeable, ApplicationEventPublisher  { //, BeanDefinitionBuilder {

	//private static final String placeholderRegex = "^\\$\\{([\\w\\.-]+)(\\:([\\w\\.\\:\\/-]*))?\\}$";
	private static final String placeholderRegex = "^\\$\\{([\\w\\.-]+)(\\:(.*))?\\}$";
	private static final Pattern placeholderPattern = Pattern.compile(placeholderRegex);
	
	//private ResourceLoader resourceLoader;
	private String beanDefineFileName;
	private boolean requiredPlaceholderValue;
	private PlaceholderValueResolver placeholderValueResolver;
	
	private List<BeanDefinition> beanDefinitions = new ArrayList<BeanDefinition>();
	
	//private Collection<Object> prebuildObjects; // the object that builded outside this factory.
	
	//private Collection<BeanFactoryCloseEventListener> closeEventListeners = new ArrayList<BeanFactoryCloseEventListener>();
	private Collection<ApplicationListener> applicationListeners = new ArrayList<>();
	
	private ObjectMapper objectMapper = new ObjectMapper();
	private TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {};
	
	// keep the current initializing bean id
	// to prevent infinite loop
	private Stack<String> initializingBeanIds = new Stack<String>();
	
	
	private final Logger logger = LoggerFactory.getLogger(DefaultBeanFactory.class);
	
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
		//this.prebuildObjects = prebuildObjects;
		//this.resourceLoader = resourceLoader;
		init(resourceLoader, prebuildObjects);
	}
	
	public DefaultBeanFactory(
			ResourceLoader resourceLoader,
			ApplicationSetting applicationSetting) throws Exception {
		
		Assert.notNull(resourceLoader);
		Assert.notNull(applicationSetting);

		this.beanDefineFileName = (String)applicationSetting.getValue(
				ApplicationSettingPlaceholderValueResolver.SETTING_KEY_BEAN_DEFINE_FILE_NAME,
				ApplicationSettingPlaceholderValueResolver.DEFAULT_BEAN_DEFINE_FILE_NAME);
		
		this.requiredPlaceholderValue = Boolean.parseBoolean(applicationSetting.getValue(
				ApplicationSettingPlaceholderValueResolver.SETTING_KEY_REQUIRED_PLACEHOLDER_VALUE,
				ApplicationSettingPlaceholderValueResolver.DEFAULT_REQUIRED_PLACEHOLDER_VALUE).toString());
		
		this.placeholderValueResolver = new ApplicationSettingPlaceholderValueResolver(applicationSetting);
		Collection<Object> prebuildObjects = Arrays.asList(resourceLoader, applicationSetting);
		//this.resourceLoader = resourceLoader;
		
		init(resourceLoader, prebuildObjects);
	}

	public DefaultBeanFactory(String... applicationConfigFileName) throws Exception{
		Assert.notNull(applicationConfigFileName);
		
		ResourceLoader resLoader = new DefaultResourceLoader();
		ApplicationSetting applicationSetting = new DefaultApplicationSetting(
				resLoader, null, applicationConfigFileName);
		
		this.beanDefineFileName = (String)applicationSetting.getValue(
				ApplicationSettingPlaceholderValueResolver.SETTING_KEY_BEAN_DEFINE_FILE_NAME,
				ApplicationSettingPlaceholderValueResolver.DEFAULT_BEAN_DEFINE_FILE_NAME);
		
		this.requiredPlaceholderValue = Boolean.parseBoolean(applicationSetting.getValue(
				ApplicationSettingPlaceholderValueResolver.SETTING_KEY_REQUIRED_PLACEHOLDER_VALUE,
				ApplicationSettingPlaceholderValueResolver.DEFAULT_REQUIRED_PLACEHOLDER_VALUE).toString());
		
		this.placeholderValueResolver = new ApplicationSettingPlaceholderValueResolver(applicationSetting);
		Collection<Object> prebuildObjects = Arrays.asList(resLoader, applicationSetting);
		//this.resourceLoader = resLoader;
		
		init(resLoader, prebuildObjects);
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

//	public void setPrebuildObjects(Collection<Object> prebuildObjects) {
//		this.prebuildObjects = prebuildObjects;
//	}
	
	private void init(ResourceLoader resourceLoader, Collection<Object> prebuildObjects) throws Exception {
		loadPreBuildObject(this);
		loadPreBuildObjects(prebuildObjects);
		loadBeanDefinition(resourceLoader);
	}
	
	private void loadPreBuildObjects(Collection<Object> prebuildObjects) {
		if (prebuildObjects == null || prebuildObjects.isEmpty()) {
			return;
		}
		
		for (Object object : prebuildObjects) {
			loadPreBuildObject(object);
		}
	}
	
	private void loadPreBuildObject(Object object) {
		Class<?> clazz = object.getClass();
		//Class<?>[] interfaces = clazz.getInterfaces();
		Annotation[] annotations = clazz.getDeclaredAnnotations();
		Field[] fields = clazz.getDeclaredFields();
		Method[] methods = clazz.getMethods();
		
		// build bean id
		String id = null;
		
		//if (clazz.isAnnotationPresent(Named.class)){
			// get id from @Named
			Named named = getAnnotationByType(annotations, Named.class);
			if (named != null) {
				String nid = named.value();
				if (StringUtils.isNotEmpty(nid)){
					id = nid;
				}
			}
		//}
		
		if (StringUtils.isEmpty(id)) {
			StringUtils.uncapitalize(clazz.getSimpleName());
		}
		
		BeanDefinition bean = new BeanDefinition(id, clazz, object, 
				//interfaces, 
				annotations, fields, methods, 
				null, null, 
				null,
				true);
		
		beanDefinitions.add(bean);
	}
	
	private void loadBeanDefinition(ResourceLoader resourceLoader) throws Exception {
		if (StringUtils.isEmpty(beanDefineFileName)) {
			return;
		}
		
		Resource resource = resourceLoader.getResource(beanDefineFileName);
		if (!resource.exists()) {
			throw new FileNotFoundException(beanDefineFileName);
		}
		
		Collection<Map<String, Object>> defineClassNames = loadBeanDefinition(resource);
		List<BeanDefinition> uninitBeans = buildBeans(defineClassNames);
		beanDefinitions.addAll(uninitBeans);
	}

	private Collection<Map<String, Object>> loadBeanDefinition(Resource resource) throws IOException {
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
	
	private List<BeanDefinition> buildBeans(Collection<Map<String, Object>> defineClassNames) throws 
			ClassNotFoundException, InstantiationException, IllegalAccessException {
		
		List<BeanDefinition> uninitBeans = new ArrayList<BeanDefinition>();
		
		for (Map<String, Object> defineClassName : defineClassNames) {
			
			Class<?> clazz = Class.forName((String)defineClassName.get("class"));
			//Class<?>[] interfaces = clazz.getInterfaces();
			Annotation[] annotations = clazz.getDeclaredAnnotations();
			Field[] fields = clazz.getDeclaredFields();
			Method[] methods = clazz.getMethods();
			
			// create instance
			Object object = clazz.newInstance();

			// generate id
			String id = (String)defineClassName.get("id");
			
			if (StringUtils.isEmpty(id)) {
				//if (clazz.isAnnotationPresent(Named.class)){
					// get id from @Named
					Named named = getAnnotationByType(annotations, Named.class);
					if (named != null) {
						String nid = named.value();
						if (StringUtils.isNotEmpty(nid)){
							id = nid;
						}
					}
				//}
			}
			
			if (StringUtils.isEmpty(id)) {
				// auto generate id
				id = StringUtils.uncapitalize(clazz.getSimpleName());
			}
			
			// get properties define
			Collection<Map<String, Object>> props = (Collection<Map<String, Object>>)defineClassName.get("props");
			
			// get the initial and dispose method.
			String initMethodName = null;
			String disposeMethodName = null;
			
			// check declare methods only!
			Method[] declaredMethods = clazz.getDeclaredMethods();
			for(Method method : declaredMethods) {
				if (method.getAnnotation(PostConstruct.class) != null){
					initMethodName = method.getName();
				}else if (method.getAnnotation(PreDestroy.class) != null){
					disposeMethodName = method.getName();
				}
			}
			
			BeanDefinition bean = new BeanDefinition(id, clazz, object, 
					//interfaces, 
					annotations, fields, methods, 
					initMethodName, disposeMethodName, 
					props,
					false);
			uninitBeans.add(bean);
		}
		
		return uninitBeans;
	}

	private void initBean(BeanDefinition bean) throws 
			ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		// prevent loop init
		if (!initializingBeanIds.empty() && initializingBeanIds.contains(bean.getId())){
			return;
		}
		
		initializingBeanIds.push(bean.getId());
		
		// start init
		Class<?> clazz = bean.getClazz();
		Object obj = bean.getObject();
		
		logger.info("Initializing bean {}.", clazz.getName());
		
		// handle define set
		Set<String> settledProps = new HashSet<>(); // remember all settled field/propertie names.
		Collection<Map<String, Object>> props = bean.getProps();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : bean.getProps()){
				String name = (String)prop.get("name");
				Object value = null;

				String methodName = "set" + StringUtils.capitalize(name);
				Method method = getMethodByName(bean.getMethods(), methodName);
				if (method == null) {
					throw new NoSuchMethodException(
							String.format("Can not found method [%s#%s].", clazz.getName(), methodName));
				}

				if (prop.containsKey("bean")){
					// inject bean
					Object defineValue = prop.get("bean");
					if (defineValue instanceof Collection){
						Collection<Object> os = new ArrayList<>();
						for(String id : (Collection<String>)defineValue){
							Object o = getBean(id);
//							if (o == null) {
//								throw new IllegalArgumentException(
//										String.format("Can not found bean [%s].", id));
//								throw new NoSuchBeanDefinitionException(id);
//							}
							os.add(o);
						}
						value = os;
					}else{
						Object o = getBean((String)defineValue);
//						if (o == null) {
//							throw new IllegalArgumentException(
//									String.format("Can not found bean [%s].", defineValue));
//							throw new NoSuchBeanDefinitionException((String)defineValue);
//						}
						value = o;
					}
				}else if (prop.containsKey("value")){
					// inject value
					
					Object defineValue = prop.get("value");
					if (defineValue instanceof Collection){
						Collection<Object> os = new ArrayList<>();
						for(Object o : (Collection)defineValue){
							if (o instanceof String){
								o = resolveValue((String)o);
							}
							os.add(o);
						}
						value = os;
					}else{
						Object o = defineValue;
						if (o instanceof String){
							o = resolveValue((String)o);
						}
						value = o;
					}
				}

				// set value
				method.invoke(obj, value);
				
				// remember
				settledProps.add(name);
			}
		}
		
		// process field inject
		for(Field field : bean.getDeclaredFields()){
			
			if (settledProps.contains(field.getName())){
				continue; // skip field that already settled.
			}
			
			// inject by @Inject and @Autowired annotation
			Inject inject = field.getAnnotation(Inject.class);
			Autowired autowired = field.getAnnotation(Autowired.class);
			
			if (inject != null || autowired != null) {
				field.setAccessible(true);
				
				Class<?> dataType = field.getType();
				
				if (dataType.equals(Collection.class) || dataType.equals(List.class)){
					// inject collection field.
					ParameterizedType pType = (ParameterizedType)field.getGenericType();
					dataType = (Class<?>)pType.getActualTypeArguments()[0];
					Map<String, ?> nameObjects = getBeansOfType(dataType);
					
					// convert Map.Value into List
					Collection<Object> targetObjects = new ArrayList<Object>(nameObjects.values());
					
					if (targetObjects.isEmpty()){
						if (autowired == null || autowired.required()) {
//							throw new IllegalArgumentException(
//									String.format("There is no match type to inject field [%s#%s]",
//											clazz.getName(), field.getName()));
							logger.error("There is no match type [{}] to inject field [{}#{}]",
									dataType.getName(), clazz.getName(), field.getName());
							
							throw new NoSuchBeanDefinitionException(dataType);
						}
					}else{
						field.set(obj, targetObjects);
					}
				}else{
					// inject object field
					Object targetObject = null;
					
					Named named = field.getAnnotation(Named.class);
					
					try{
						if (named != null) {
							targetObject = getBean(named.value());
						}else{
							targetObject = getBean(dataType);
						}
					}catch(NoSuchBeanDefinitionException e){
						// check @Autowired and required later.
					}
					
					if (targetObject == null) {
						if (autowired == null || autowired.required()) {
//							throw new IllegalArgumentException(
//									String.format("There is no match type to inject field [%s#%s]",
//											clazz.getName(), field.getName()));
							if (named != null) {
								logger.error("There is no match id [{}] to inject field [{}#{}]",
										named.value(), clazz.getName(), field.getName());
								
								throw new NoSuchBeanDefinitionException(named.value());
							}else{
								logger.error("There is no match type [{}] to inject field [{}#{}]",
										dataType.getName(), clazz.getName(), field.getName());
								
								throw new NoSuchBeanDefinitionException(dataType);
							}
						}
					}else{
						field.set(obj, targetObject);
					}
				}
				
				continue;
			}
			
			// inject by @Value annotation
			Value value = field.getAnnotation(Value.class);
			if (value != null) {
				field.setAccessible(true);
				
				String placeholder = value.value();
				Object targetValue = resolveValue(placeholder);
				
//				if (targetValue == null || 
//						(targetValue instanceof String && 
//						targetValue.equals(StringUtils.EMPTY))) {
				if (targetValue == null) {
					if (requiredPlaceholderValue) {
						throw new IllegalArgumentException(
								String.format("Can not resolve the placeholder [%s] for field [%s#%s]",
										placeholder, clazz.getName(), field.getName()));
					}else{
						continue;
					}
				}
				
				if (targetValue instanceof String && 
						targetValue.equals(StringUtils.EMPTY)) {
					// convert EMPTY string to null.
					// because the Java annotation can not express NULL value.
					//targetValue = null; 
					continue;
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
						throw new IllegalArgumentException(
								String.format("Can not resolve the placeholder [%s] for field [%s#%s], type cast error.",
										placeholder, clazz.getName(), field.getName()));
					}
				}
			}
		}
		
		// invoke initial method
		String initMethodName = bean.getInitMethodName();
		if (initMethodName != null) {
			Method initMethod = getMethodByName(bean.getMethods(), initMethodName);
			initMethod.invoke(obj);
		}
		
		// set flag
		bean.setInitialized(true);
		
		// prevent loop
		initializingBeanIds.pop();
	}
	
	private Method getMethodByName(Method[] methods, String name) {
		for (Method method : methods) {
			if (name.equals(method.getName())) {
				return method;
			}
		}
		return null;
	}
	
	private boolean existInterface(Class<?>[] interfaces, Class<?> clazz) {
		for (Class<?> i : interfaces) {
			if (i.equals(clazz)){
				return true;
			}
		}
		return false;
	}
	
	private <T> T getAnnotationByType(Annotation[] annotations, Class<T> clazz) {
		for(Annotation a : annotations) {
			if (a.annotationType().equals(clazz)) {
				return (T)a;
			}
		}
		return null;
	}
	
	private Object resolveValue(String placeholder) {
		Object targetValue = null;
		Matcher matcher = placeholderPattern.matcher(placeholder);
		
		if (matcher.matches()) {

			// found placeholder
			if (matcher.groupCount()== 3){
				// plachholder with default value set
				targetValue  = placeholderValueResolver.getValue(matcher.group(1), matcher.group(3));
			}else{
				// placholder without default value set
				targetValue  = placeholderValueResolver.getValue(matcher.group(1));
			}
		}else{
			// direct value
			targetValue = placeholder;
		}
		
		return targetValue;
	}

	@Override
	public boolean containsBeanDefinition(String beanName) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getBeanDefinitionCount() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String[] getBeanDefinitionNames() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
		Assert.notNull(type);
		
		Collection<BeanDefinition> matchBeans = new ArrayList<BeanDefinition>();
		for(BeanDefinition bean : beanDefinitions) {
//			if (bean.getClazz().equals(type)){
//				matchBeans.add(bean);
//			}else{
//				for(Class<?> c : bean.getInterfaces()){
//					if (type.equals(c)) {
//						matchBeans.add(bean);
//					}
//				}
//			}
			if (type.isAssignableFrom(bean.getClazz())) {
				matchBeans.add(bean);
			}
		}
		
		Map<String, T> objects = new HashMap<>();
		
		for(BeanDefinition bean : matchBeans) {
			if (!bean.isInitialized()) {
				try{
					initBean(bean);
				}catch(ClassNotFoundException | 
						IllegalAccessException | 
						IllegalArgumentException |
						NoSuchMethodException e){
					throw new IllegalArgumentException(
							String.format("Can not initialize bean [%s], message: %s",
									bean.getClazz().getName(), e.getMessage()), 
							e);
				}catch(InvocationTargetException e){
					Throwable te = ((InvocationTargetException)e).getTargetException();
					throw new IllegalArgumentException(
							String.format("Can not initialize bean [%s], message: %s", 
									bean.getClazz().getName(), te.getMessage()),
							te);
				}
			}

			objects.put(bean.getId(), (T)bean.getObject());
		}
		
		return objects;
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
		Assert.notNull(annotationType);
		
		Collection<BeanDefinition> matchBeans = new ArrayList<BeanDefinition>();
		for(BeanDefinition bean : beanDefinitions) {
			for(Annotation c : bean.getDeclaredAnnotations()){
				if (annotationType.equals(c.annotationType())) {
					matchBeans.add(bean);
				}
			}
		}
		
		Map<String, Object> objects = new HashMap<>();
		
		for(BeanDefinition bean : matchBeans) {
			if (!bean.isInitialized()) {
				try{			
					initBean(bean);
				}catch(ClassNotFoundException | 
							IllegalAccessException | 
							IllegalArgumentException |
							NoSuchMethodException e) {
					throw new IllegalArgumentException(
							String.format("Can not initialize bean [%s], message: %s",
									annotationType.getName(), e.getMessage()), 
							e);
				}catch(InvocationTargetException e){
					Throwable te = ((InvocationTargetException)e).getTargetException();
					throw new IllegalArgumentException(
							String.format("Can not initialize bean [%s], message: %s", 
									bean.getClazz().getName(), te.getMessage()),
							te);
				}
			}
			objects.put(bean.getId(), bean.getObject());
		}
		
		return objects;
	}

	@Override
	public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Object getBean(String name) throws BeansException {
		Assert.hasText(name);
		
		for(BeanDefinition bean : beanDefinitions){
			if (name.equals(bean.getId())){
				if (!bean.isInitialized()){
					try{
						initBean(bean);
					}catch(ClassNotFoundException | 
							IllegalAccessException | 
							IllegalArgumentException |
							NoSuchMethodException e){
						throw new IllegalArgumentException(
								String.format("Can not initialize bean [%s], message: %s", 
										bean.getClazz().getName(), e.getMessage()),
								e);
					}catch(InvocationTargetException e){
						Throwable te = ((InvocationTargetException)e).getTargetException();
						throw new IllegalArgumentException(
								String.format("Can not initialize bean [%s], message: %s", 
										bean.getClazz().getName(), te.getMessage()),
								te);
					}
				}
				return bean.getObject();
			}
		}
		
		throw new NoSuchBeanDefinitionException(name);
	}

	@Override
	public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public <T> T getBean(Class<T> requiredType) throws BeansException {
		Assert.notNull(requiredType);
		
		Map<String, T> objects = getBeansOfType(requiredType);
		if (objects.isEmpty()) {
			throw new NoSuchBeanDefinitionException(requiredType);
		}
		
		if (objects.size() > 1) {
			throw new NoUniqueBeanDefinitionException(requiredType, objects.keySet());
					//"More than one object are assign from this class: " + requiredType.getName());
		}
		
		return objects.values().iterator().next();
	}

	@Override
	public Object getBean(String name, Object... args) throws BeansException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean containsBean(String name) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isTypeMatch(String name, Class<?> targetType) throws NoSuchBeanDefinitionException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String[] getAliases(String name) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void addApplicationListener(ApplicationListener<?> listener) {
		applicationListeners.add(listener);
	}
	
	@Override
	public void close() throws IOException {
		for(BeanDefinition bean : beanDefinitions) {
			String disposeMethodName = bean.getDisposeMethodName();
			if (disposeMethodName != null) {
				Method disposeMethod = getMethodByName(bean.getMethods(), disposeMethodName);
				try {
					disposeMethod.invoke(bean.getObject());
				} catch (IllegalAccessException | 
						IllegalArgumentException ex) {
					logger.error(
							String.format("Close bean [%s] error, message: %s.", bean.getId(), ex.getMessage()), 
							ex);
				} catch(InvocationTargetException ex){
					Throwable t = ((InvocationTargetException)ex).getTargetException();
					logger.error(
							String.format("Close bean [%s] error, message: %s.", bean.getId(), t.getMessage()), 
							t);
				}
			}
		}
		
		// notify event listeners
		publishEvent(new BeanFactoryClosedEvent(this));
	}

	@Override
	public void publishEvent(ApplicationEvent event) {
		for(ApplicationListener listener : applicationListeners) {
			listener.onApplicationEvent(event);
		}
	}
}
