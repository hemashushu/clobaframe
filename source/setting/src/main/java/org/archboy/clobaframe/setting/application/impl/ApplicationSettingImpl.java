package org.archboy.clobaframe.setting.application.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.setting.support.Utils;
import org.archboy.clobaframe.setting.application.ApplicationSetting;
import org.archboy.clobaframe.setting.application.ApplicationSettingProvider;
import org.archboy.clobaframe.setting.application.ApplicationSettingRepository;
import org.archboy.clobaframe.setting.application.PostApplicationSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
public class ApplicationSettingImpl implements ApplicationSetting, ResourceLoaderAware, InitializingBean {

	private static final String DEFAULT_ROOT_CONFIG_FILE_NAME = "classpath:root.properties";
	private static final String DEFAULT_DATA_FOLDER = "${java.io.tmpdir}/${clobaframe.setting.appName}";
	private static final boolean DEFAULT_AUTO_CREATE_DATA_FOLDER = true;
	private static final String DEFAULT_SETTING_FILE_NAME = "classpath:application.properties";
	private static final String DEFAULT_CUSTOM_SETTING_FILE_NAME = "settings.json";
	private static final String DEFAULT_EXTRA_SETTING_FILE_NAME = "extra.json";

	private String rootConfigFileName = DEFAULT_ROOT_CONFIG_FILE_NAME;
	private String appName;
	
	/**
	 * folder that store application running necessary files.
	 * in linux it's ~/.local/share
	 * in osx ~/Library/Application Support
	 * in windows ~\Application Data\Local|Roaming
	 */
	private String dataFolder = DEFAULT_DATA_FOLDER;
	private boolean autoCreateDataFolder = DEFAULT_AUTO_CREATE_DATA_FOLDER;
	private String defaultSettingFileName = DEFAULT_SETTING_FILE_NAME;
	private String customSettingFileName = DEFAULT_CUSTOM_SETTING_FILE_NAME;
	private String extraSettingFileName = DEFAULT_EXTRA_SETTING_FILE_NAME;
	
	private Map<String, Object> setting = new LinkedHashMap<String, Object>();
	
	private Resource[] locations;
	
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
	
	public void setRootConfigFileName(String rootConfigFileName) {
		this.rootConfigFileName = rootConfigFileName;
	}

	@Override
	public void setPostApplicationSettings(Collection<PostApplicationSetting> postApplicationSettings) {
		this.postApplicationSettings = postApplicationSettings;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void setLocations(Resource... locations) {
		this.locations = locations;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		loadRootConfig();
		rebuildBaseSetting();
		initComponents();
		executePostSetting();
	}
	
	private void loadRootConfig(){
		Map<String, Object> rootSetting = null;
		Resource resource = resourceLoader.getResource(rootConfigFileName);
		
		if (!resource.exists()) {
			logger.warn("Application root setting resource [{}] not found.", resource.getFilename());
			return;
		}
		
		InputStream in = null;
		try{
			in = resource.getInputStream();
			rootSetting = Utils.readProperties(in);
		}catch(IOException e) {
			// ignore
			logger.error("Load root application configuration failed: {}", e.getMessage());
		}finally {
			IOUtils.closeQuietly(in);
		}
		
		if (rootSetting == null || rootSetting.isEmpty()) {
			return;
		}
		
		this.appName = getRootConfigValue(rootSetting, "clobaframe.setting.appName", appName);
		this.dataFolder = getRootConfigValue(rootSetting, "clobaframe.setting.dataFolder", DEFAULT_DATA_FOLDER);
		this.autoCreateDataFolder = Boolean.parseBoolean(getRootConfigValue(rootSetting, "clobaframe.setting.autoCreateDataFolder", Boolean.toString(DEFAULT_AUTO_CREATE_DATA_FOLDER)));
		this.defaultSettingFileName = getRootConfigValue(rootSetting, "clobaframe.setting.defaultSettingFileName", DEFAULT_SETTING_FILE_NAME);
		this.customSettingFileName = getRootConfigValue(rootSetting, "clobaframe.setting.customSettingFileName", DEFAULT_CUSTOM_SETTING_FILE_NAME);
		this.extraSettingFileName = getRootConfigValue(rootSetting, "clobaframe.setting.extraSettingFileName", DEFAULT_EXTRA_SETTING_FILE_NAME);
	}
	
	private String getRootConfigValue(Map<String, Object> rootSetting, String key, String defaultValue) {
		String value = (String)rootSetting.get(key);
		return (value == null ? defaultValue : value);
	}
	
	private void rebuildBaseSetting() {
		// put the base settings.
		setting.clear();
		setting.put("clobaframe.setting.appName", appName);
		setting.put("clobaframe.setting.dataFolder", dataFolder);
		setting.put("clobaframe.setting.autoCreatedataFolder", autoCreateDataFolder);
		setting.put("clobaframe.setting.defaultSettingFileName", defaultSettingFileName);
		setting.put("clobaframe.setting.customSettingFileName", customSettingFileName);
		setting.put("clobaframe.setting.extraSettingFileName", extraSettingFileName);
	}
	
	private void initComponents(){
		// add setting provider, from lower priority to higher priority.
		applicationSettingProviders = new ArrayList<ApplicationSettingProvider>();
		applicationSettingProviders.add(new PropertiesApplicationSettingProvider(resourceLoader, defaultSettingFileName));
		applicationSettingProviders.add(new EnvironmentVariablesSettingProvider());
		applicationSettingProviders.add(new SystemPropertiesSettingProvider());
		
		// add other in-app application setting providers
		if (locations != null) {
			for(Resource resource : locations){
				applicationSettingProviders.add(new PropertiesApplicationSettingProvider(resource));
			}
		}
		
		// merge all settings.
		for(ApplicationSettingProvider provider : applicationSettingProviders){
			Map<String, Object> map = provider.getAll();
			setting = Utils.merge(setting, map);
		}
		
		// get the data dir and file name
		String dataFolderValue = (String)Utils.resolvePlaceholder(setting, dataFolder);
		String customFileNameValue = (String)Utils.resolvePlaceholder(setting, customSettingFileName);
		String extraFileNameValue = (String)Utils.resolvePlaceholder(setting, extraSettingFileName);

		// try to create data folder
		if (autoCreateDataFolder) {
			File file = new File(dataFolderValue);
			if (file.exists() && file.isDirectory()){
				// continue
			}else if (file.isFile()) {
				// file exist
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
		customApplicationSettingProviders.add(new JsonApplicationSettingProvider(dataFolderValue, customFileNameValue));
		customApplicationSettingProviders.add(new JsonApplicationSettingProvider(dataFolderValue, extraFileNameValue));

		// merge all custom settings.
		for(ApplicationSettingProvider provider : customApplicationSettingProviders){
			Map<String, Object> map = provider.getAll();
			setting = Utils.merge(setting, map);
		}
		
		// merge custom providers
		applicationSettingProviders.addAll(customApplicationSettingProviders);
		
		// set setting repository
		applicationSettingRepository = new JsonApplicationSettingRepository(dataFolderValue, customFileNameValue);

		
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
		applicationSettingRepository.update(key, value);
	}

	@Override
	public void set(Map<String, Object> items) {
		applicationSettingRepository.update(items);
	}

	@Override
	public Map<String, Object> getAll() {
		return setting;
	}
	
	@Override
	public void refresh(){
		
		rebuildBaseSetting();

		// merge all settings.
		for(ApplicationSettingProvider provider : applicationSettingProviders){
			Map<String, Object> map = provider.getAll();
			setting = Utils.merge(setting, map);
		}
	}
}
