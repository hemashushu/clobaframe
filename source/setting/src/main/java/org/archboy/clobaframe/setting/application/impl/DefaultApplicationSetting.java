package org.archboy.clobaframe.setting.application.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.setting.SettingProvider;
import org.archboy.clobaframe.setting.support.Utils;
import org.archboy.clobaframe.setting.application.ApplicationSetting;
import org.archboy.clobaframe.setting.application.ApplicationSettingProvider;
import org.archboy.clobaframe.setting.application.ApplicationSettingRepository;
import org.archboy.clobaframe.setting.application.PostApplicationSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/**
 *
 * Consider using (Apache Commons Configuration)[http://commons.apache.org/proper/commons-configuration/] 
 * to read/write the properties file.
 * @author yang
 */
public class DefaultApplicationSetting implements ApplicationSetting, ResourceLoaderAware, InitializingBean {

	public static final String ROOT_KEY_APPLICATION_NAME = "clobaframe.setting.applicationName";
	public static final String ROOT_KEY_ROOT_CONFIG_FILE_NAME = "clobaframe.setting.rootConfigFileName";
	public static final String ROOT_KEY_DATA_FOLDER = "clobaframe.setting.dataFolder";
	public static final String ROOT_KEY_AUTO_CREATE_DATA_FOLDER = "clobaframe.setting.autoCreateDataFolder";
	public static final String ROOT_KEY_DEFAULT_SETTING_FILE_NAME = "clobaframe.setting.defaultSettingFileName";
	public static final String ROOT_KEY_CUSTOM_SETTING_FILE_NAME = "clobaframe.setting.customSettingFileName";
	public static final String ROOT_KEY_EXTRA_SETTING_FILE_NAME = "clobaframe.setting.extraSettingFileName";
	public static final String ROOT_KEY_SAVING_SETTING_FILE_NAME = "clobaframe.setting.savingSettingFileName";

	private Map<String, Object> rootSetting = new LinkedHashMap<String, Object>();
	private Map<String, Object> setting = new LinkedHashMap<String, Object>();

	// a base setting value.
	private String applicationName;
	
	// an other base setting value.
	private String rootConfigFileName;
	
	/**
	 * Base root setting values.
	 * these values can overwrite the 'applicationName' and 'rootConfigFileName',
	 * and can be overwrote by the values that resisted in the root configuration file.
	 */
	private Properties properties;
	
	/**
	 * Other build-in (in-jar-package) settings.
	 */
	private String[] locations;
	
	private ResourceLoader resourceLoader;
	
	private List<ApplicationSettingProvider> applicationSettingProviders;
	private ApplicationSettingRepository applicationSettingRepository;
	private Collection<PostApplicationSetting> postApplicationSettings;
	
	private final Logger logger = LoggerFactory.getLogger(DefaultApplicationSetting.class);
	
	@Override
	public void setApplicationName(String name) {
		this.applicationName = name;
	}

	@Override
	public String getApplicationName() {
		return applicationName;
	}
	
	@Override
	public void setRootConfigFileName(String rootConfigFileName) {
		this.rootConfigFileName = rootConfigFileName;
	}

	@Override
	public void setPostApplicationSettings(Collection<PostApplicationSetting> postApplicationSettings) {
		this.postApplicationSettings = postApplicationSettings;
	}

	@Override
	public void setProperties(Properties properties){
		this.properties = properties;
	}
	
	@Override
	public void setLocations(String... locations) {
		this.locations = locations;
	}
	
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public DefaultApplicationSetting() {
		// for manual build.
	}

	public DefaultApplicationSetting(ResourceLoader resourceLoader, 
			String applicationName,
			Properties properties, 
			String rootConfigFileName, 
			Collection<PostApplicationSetting> postApplicationSettings,
			String... locations) throws Exception {
		this.resourceLoader = resourceLoader;
		this.applicationName = applicationName;
		this.properties = properties;
		this.rootConfigFileName = rootConfigFileName;
		this.postApplicationSettings = postApplicationSettings;
		this.locations = locations;
		afterPropertiesSet();
	}
	
	public DefaultApplicationSetting(ResourceLoader resourceLoader, 
			Properties properties, 
			String... locations) throws Exception {
		this.resourceLoader = resourceLoader;
		this.properties = properties;
		this.locations = locations;
		afterPropertiesSet();
	}
	
	public DefaultApplicationSetting(String... locations) throws Exception {
		this.resourceLoader = new DefaultResourceLoader();
		this.locations = locations;
		afterPropertiesSet();
	}
	
	//@PostConstruct
	
	@Override
	public void afterPropertiesSet() throws Exception {
		loadRootConfigFromBeanDefine();
		loadRootConfigFromFile();
		loadProviders();
		executePostSetting();
	}

	@Override
	public void addProvider(SettingProvider settingProvider) {
		Assert.isInstanceOf(ApplicationSettingProvider.class, settingProvider);
		
		if (applicationSettingProviders == null) {
			applicationSettingProviders = new ArrayList<ApplicationSettingProvider>();
		}
		
		applicationSettingProviders.add((ApplicationSettingProvider)settingProvider);
		applicationSettingProviders.sort(new Comparator<ApplicationSettingProvider>() {

			@Override
			public int compare(ApplicationSettingProvider o1, ApplicationSettingProvider o2) {
				return o1.getOrder() - o2.getOrder();
			}
		});
	}

	@Override
	public void removeProvider(String providerName) {
		Assert.notNull(providerName);
		for (int idx=applicationSettingProviders.size() -1; idx>=0; idx--){
			ApplicationSettingProvider provider = applicationSettingProviders.get(idx);
			if (providerName.equals(provider.getName())){
				applicationSettingProviders.remove(idx);
				break;
			}
		}
	}
	
	private void loadRootConfigFromBeanDefine() {
		rootSetting.put(ROOT_KEY_APPLICATION_NAME, applicationName);
		rootSetting.put(ROOT_KEY_ROOT_CONFIG_FILE_NAME, rootConfigFileName);
		
		if (properties != null && !properties.isEmpty()) {
			rootSetting = Utils.merge(rootSetting, properties);
		}
	}
	
	private void loadRootConfigFromFile() throws IOException{
		String fileName = (String)rootSetting.get(ROOT_KEY_ROOT_CONFIG_FILE_NAME);
		
		if (StringUtils.isEmpty(fileName)){
			return;
		}
		
		Resource resource = resourceLoader.getResource(fileName);
		if (!resource.exists()) {
			throw new FileNotFoundException(
					String.format(
							"Application root setting resource [{}] not found.", 
							resource.getFilename()));
		}
		
		Map<String, Object> map = null;
		InputStream in = null;
		try{
			in = resource.getInputStream();
			map = Utils.readProperties(in);
		//}catch(IOException e) {
		//	logger.error("Load root application configuration failed: {}", e.getMessage());
		}finally {
			IOUtils.closeQuietly(in);
		}
		
		if (map == null || map.isEmpty()) {
			return;
		}

		rootSetting = Utils.merge(rootSetting, map);
	}

	private void loadProviders(){
		// build the temp setting, for resolve the default setting and other build-in (in-jar-package) settings filename
		Map<String, Object> tempSetting = new LinkedHashMap<String, Object>();
		tempSetting = Utils.merge(tempSetting, rootSetting);
		tempSetting = Utils.merge(tempSetting, new EnvironmentVariablesSettingProvider().list());
		tempSetting = Utils.merge(tempSetting, new SystemPropertiesSettingProvider().list());
		
		// add setting provider, from lower priority to higher priority.
		applicationSettingProviders = new ArrayList<ApplicationSettingProvider>();
		
		String defaultSettingFileName = (String)tempSetting.get(ROOT_KEY_DEFAULT_SETTING_FILE_NAME);
		if (StringUtils.isNotEmpty(defaultSettingFileName)){
			String fileName = (String)Utils.resolvePlaceholder(tempSetting, defaultSettingFileName);
			applicationSettingProviders.add(new PropertiesApplicationSettingProvider(resourceLoader, fileName));
		}
		
		applicationSettingProviders.add(new EnvironmentVariablesSettingProvider());
		applicationSettingProviders.add(new SystemPropertiesSettingProvider());
		
		// add other in-app application setting providers
		if (locations != null) {
			for(String location : locations){
				String fileName = (String)Utils.resolvePlaceholder(tempSetting, location);
				applicationSettingProviders.add(new PropertiesApplicationSettingProvider(resourceLoader, fileName));
			}
		}
		
		// merge root setting into application setting
		setting.clear();
		setting = Utils.merge(setting, rootSetting);
		
		// merge all provider settings.
		for(ApplicationSettingProvider provider : applicationSettingProviders){
			Map<String, Object> map = provider.list();
			setting = Utils.merge(setting, map);
		}
		
		// get the custom config and the extra config
		String dataFolder = (String)Utils.resolvePlaceholder(setting, setting.get(ROOT_KEY_DATA_FOLDER));
		String autoCreateDataFolder = (String)Utils.resolvePlaceholder(setting, setting.get(ROOT_KEY_AUTO_CREATE_DATA_FOLDER));
		String customFileName = (String)Utils.resolvePlaceholder(setting, setting.get(ROOT_KEY_CUSTOM_SETTING_FILE_NAME));
		String extraFileName = (String)Utils.resolvePlaceholder(setting, setting.get(ROOT_KEY_EXTRA_SETTING_FILE_NAME));
		String savingFileName = (String)Utils.resolvePlaceholder(setting, setting.get(ROOT_KEY_SAVING_SETTING_FILE_NAME));
		
		// try to create data folder
		if (StringUtils.isNotEmpty(autoCreateDataFolder) && Boolean.valueOf(autoCreateDataFolder)) {
			File file = new File(dataFolder);
			if (file.exists() && file.isDirectory()){
				// the specify folder already exists
			}else if (file.isFile()) {
				// duplicate name file exists
				logger.error("Application data folder duplicate name with a file [{}].", file.getAbsolutePath());
			}else{
				try{
					logger.info("Creating application data folder [{}].", file.getAbsolutePath());
					file.mkdirs();
				}catch(Exception e){
					logger.error("Fail to create application data folder [{}].", file.getAbsolutePath());
				}
			}
		}
		
		// add user custom application setting providers
		List<ApplicationSettingProvider> customApplicationSettingProviders = new ArrayList<ApplicationSettingProvider>();
		
		if (StringUtils.isNotEmpty(customFileName)){
			customApplicationSettingProviders.add(new JsonApplicationSettingProvider(new FileSystemResource(customFileName)));
		}
		
		if (StringUtils.isNotEmpty(extraFileName)){
			customApplicationSettingProviders.add(new JsonApplicationSettingProvider(new FileSystemResource(extraFileName)));
		}

		// merge all custom settings.
		for(ApplicationSettingProvider provider : customApplicationSettingProviders){
			Map<String, Object> map = provider.list();
			setting = Utils.merge(setting, map);
		}
		
		// merge custom providers
		applicationSettingProviders.addAll(customApplicationSettingProviders);
		
		// set setting repository
		if (StringUtils.isNotEmpty(savingFileName)){
			applicationSettingRepository = new JsonApplicationSettingRepository(new FileSystemResource(savingFileName));
		}
		
	}

	private void executePostSetting() {
		// execute post setting works.
		if (postApplicationSettings == null || postApplicationSettings.isEmpty()) {
			return;
		}
		
		for(PostApplicationSetting postApplicationSetting : postApplicationSettings) {
			try{
				postApplicationSetting.execute(setting);
			}catch(Exception e){
				logger.error("Fail to execute PostApplicationSetting [{}].", PostApplicationSetting.class.getSimpleName());
			}
		}
	}

	
	@Override
	public Object getValue(String key) {
		Object value = setting.get(key);
		return (value == null ? null : Utils.resolvePlaceholder(setting, value));
	}

	@Override
	public Object getValue(String key, Object defaultValue) {
		Object value = getValue(key);
		return (value == null ? defaultValue : value);
	}

	@Override
	public Object get(String key) {
		return setting.get(key);
	}
	
	@Override
	public void set(String key, Object value) {
		if (applicationSettingRepository == null){
			throw new NullPointerException("No application setting repository.");
		}
		
		applicationSettingRepository.update(key, value);
		setting = Utils.merge(setting, key, value);
	}

	@Override
	public void set(Map<String, Object> items) {
		if (applicationSettingRepository == null){
			throw new NullPointerException("No application setting repository.");
		}
		
		applicationSettingRepository.update(items);
		setting = Utils.merge(setting, items);
	}

	@Override
	public Map<String, Object> list() {
		return setting;
	}
	
	@Override
	public void refresh(){
		// clear setting
		setting.clear();
		
		// merge root setting
		setting = Utils.merge(setting, rootSetting);

		// merge all provider setting.
		for(ApplicationSettingProvider provider : applicationSettingProviders){
			Map<String, Object> map = provider.list();
			setting = Utils.merge(setting, map);
		}
	}

}
