package org.archboy.clobaframe.setting.common.application.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.setting.support.Utils;
import org.archboy.clobaframe.setting.common.application.ApplicationSetting;
import org.archboy.clobaframe.setting.common.application.ApplicationSettingProvider;
import org.archboy.clobaframe.setting.common.application.ApplicationSettingRepository;
import org.archboy.clobaframe.setting.common.application.PostApplicationSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * About system wide config folder and user local config folder.
 * 
 * System wide:
 * linux and osx: /etc/APP_NAME
 * windows: %SystemDrive%\ProgramData\APP_NAME
 * 
 * User local:
 * linux: ~/.local/share/APP_NAME
 * osx: ~/Library/Application Support/APP_NAME
 * osx sandbox: ~/Library/Containers/APP_BUNDLE_ID/Data/Library/Application Support/APP_NAME
 * windows: ~\Application Data\Local|Roaming\APP_NAME
 *
 * Consider using (Apache Commons Configuration)[http://commons.apache.org/proper/commons-configuration/] 
 * to read/write the properties file.
 * @author yang
 */
public class ApplicationSettingImpl implements ApplicationSetting, ResourceLoaderAware, InitializingBean {

	private static final String ROOT_KEY_APP_NAME = "clobaframe.setting.appName";
	private static final String ROOT_KEY_ROOT_CONFIG_FILE_NAME = "clobaframe.setting.rootConfigFileName";
	private static final String ROOT_KEY_CONFIG_FOLDER = "clobaframe.setting.configFolder";
	private static final String ROOT_KEY_AUTO_CREATE_CONFIG_FOLDER = "clobaframe.setting.autoCreateConfigFolder";
	private static final String ROOT_KEY_DEFAULT_SETTING_FILE_NAME = "clobaframe.setting.defaultSettingFileName";
	private static final String ROOT_KEY_CUSTOM_SETTING_FILE_NAME = "clobaframe.setting.customSettingFileName";
	private static final String ROOT_KEY_EXTRA_SETTING_FILE_NAME = "clobaframe.setting.extraSettingFileName";

	private String rootConfigFileName;
	private String appName;
	
	private Map<String, Object> rootSetting = new LinkedHashMap<String, Object>();
	private Map<String, Object> setting = new LinkedHashMap<String, Object>();
	
	/**
	 * Root setting values.
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
	
	private final Logger logger = LoggerFactory.getLogger(ApplicationSettingImpl.class);

	@Override
	public void setApplicationName(String name) {
		this.appName = name;
	}

	@Override
	public String getApplicationName() {
		return appName;
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
	
	@Override
	public void afterPropertiesSet() throws Exception {
		//rootSetting.clear();
		rootSetting.put(ROOT_KEY_APP_NAME, appName);
		rootSetting.put(ROOT_KEY_ROOT_CONFIG_FILE_NAME, rootConfigFileName);
		
		loadRootConfigFromBeanDefine();
		loadRootConfigFromFile();
		
		initProviders();
		executePostSetting();
	}
	
	private void loadRootConfigFromBeanDefine() {
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
		
//		this.appName = getRootConfigValue(rootSetting, "clobaframe.setting.appName", appName);
//		this.dataFolder = getRootConfigValue(rootSetting, "clobaframe.setting.dataFolder", DEFAULT_DATA_FOLDER);
//		this.autoCreateDataFolder = Boolean.parseBoolean(getRootConfigValue(rootSetting, "clobaframe.setting.autoCreateDataFolder", Boolean.toString(DEFAULT_AUTO_CREATE_DATA_FOLDER)));
//		this.defaultSettingFileName = getRootConfigValue(rootSetting, "clobaframe.setting.defaultSettingFileName", DEFAULT_SETTING_FILE_NAME);
//		this.customSettingFileName = getRootConfigValue(rootSetting, "clobaframe.setting.customSettingFileName", DEFAULT_CUSTOM_SETTING_FILE_NAME);
//		this.extraSettingFileName = getRootConfigValue(rootSetting, "clobaframe.setting.extraSettingFileName", DEFAULT_EXTRA_SETTING_FILE_NAME);
		
		rootSetting = Utils.merge(rootSetting, map);
	}
	
//	private String getRootConfigValue(Map<String, Object> rootSetting, String key, String defaultValue) {
//		String value = (String)rootSetting.get(key);
//		return (value == null ? defaultValue : value);
//	}
	
//	private void buildRootSetting() {
//		// put the base settings.
//		//setting.clear();
//		setting.put("clobaframe.setting.appName", appName);
//		setting.put("clobaframe.setting.dataFolder", dataFolder);
//		setting.put("clobaframe.setting.autoCreatedataFolder", autoCreateDataFolder);
//		setting.put("clobaframe.setting.defaultSettingFileName", defaultSettingFileName);
//		setting.put("clobaframe.setting.customSettingFileName", customSettingFileName);
//		setting.put("clobaframe.setting.extraSettingFileName", extraSettingFileName);
//	}
	
	private void initProviders(){
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
		String configFolder = (String)Utils.resolvePlaceholder(setting, setting.get(ROOT_KEY_CONFIG_FOLDER));
		String autoCreateConfigFolder = (String)Utils.resolvePlaceholder(setting, setting.get(ROOT_KEY_AUTO_CREATE_CONFIG_FOLDER));
		String customFileName = (String)Utils.resolvePlaceholder(setting, setting.get(ROOT_KEY_CUSTOM_SETTING_FILE_NAME));
		String extraFileName = (String)Utils.resolvePlaceholder(setting, setting.get(ROOT_KEY_EXTRA_SETTING_FILE_NAME));

		// try to create config folder
		if (StringUtils.isNotEmpty(autoCreateConfigFolder) && Boolean.valueOf(autoCreateConfigFolder)) {
			File file = new File(configFolder);
			if (file.exists() && file.isDirectory()){
				// the specify folder already exists
			}else if (file.isFile()) {
				// duplicate name file exists
				logger.error("Application config folder duplicate name with a file [{}].", file.getAbsolutePath());
			}else{
				try{
					logger.info("Creating application config folder [{}].", file.getAbsolutePath());
					file.mkdirs();
				}catch(Exception e){
					logger.error("Fail to create application config folder [{}].", file.getAbsolutePath());
				}
			}
		}
		
		// add user custom application setting providers
		List<ApplicationSettingProvider> customApplicationSettingProviders = new ArrayList<ApplicationSettingProvider>();
		
		if (StringUtils.isNotEmpty(customFileName)){
			customApplicationSettingProviders.add(new JsonApplicationSettingProvider(configFolder, customFileName));
		}
		
		if (StringUtils.isNotEmpty(extraFileName)){
			customApplicationSettingProviders.add(new JsonApplicationSettingProvider(configFolder, extraFileName));
		}

		// merge all custom settings.
		for(ApplicationSettingProvider provider : customApplicationSettingProviders){
			Map<String, Object> map = provider.list();
			setting = Utils.merge(setting, map);
		}
		
		// merge custom providers
		applicationSettingProviders.addAll(customApplicationSettingProviders);
		
		// set setting repository
		if (StringUtils.isNotEmpty(customFileName)){
			applicationSettingRepository = new JsonApplicationSettingRepository(configFolder, customFileName);
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
