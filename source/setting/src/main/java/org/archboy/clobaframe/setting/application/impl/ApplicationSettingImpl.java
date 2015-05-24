package org.archboy.clobaframe.setting.application.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.setting.support.Utils;
import org.archboy.clobaframe.setting.application.ApplicationSetting;
import org.archboy.clobaframe.setting.application.ApplicationSettingProvider;
import org.archboy.clobaframe.setting.application.ApplicationSettingRepository;
import org.archboy.clobaframe.setting.application.PostApplicationSetting;
import org.archboy.clobaframe.setting.support.AbstractPropertiesFileSettingAccess;
import org.archboy.clobaframe.setting.support.SettingAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
@Named
public class ApplicationSettingImpl implements ApplicationSetting, ResourceLoaderAware, InitializingBean {

	private static final String DEFAULT_ROOT_CONFIG_FILE_NAME = "classpath:application.properties";
	
	private static final String DEFAULT_DATA_FOLDER = "/var/lib/${clobaframe.setting.appName}";
	private static final boolean DEFAULT_AUTO_CREATE_DATA_FOLDER = true;
	private static final String DEFAULT_SETTING_FILE_NAME = "classpath:application-setting-default.properties";
	private static final String DEFAULT_CUSTOM_SETTING_FILE_NAME = "settings.json";
	private static final String DEFAULT_EXTRA_SETTING_FILE_NAME = "extra.json";

	private String rootConfigFileName = DEFAULT_ROOT_CONFIG_FILE_NAME;
	
	//@Value("${clobaframe.setting.appName}")
	private String appName;
	
	//@Value("${clobaframe.setting.dataFolder}")
	private String dataFolder; // = DEFAULT_DATA_FOLDER;
	
	//@Value("${colbaframe.setting.autoCreateDataFolder}")
	private boolean autoCreateDataFolder; // = DEFAULT_AUTO_CREATE_DATA_FOLDER;

	//@Value("${clobaframe.setting.defaultSettingFileName}")
	private String defaultSettingFileName; // = DEFAULT_SETTING_FILE_NAME;
	
	//@Value("${clobaframe.setting.customSettingFileName}")
	private String customSettingFileName; // = DEFAULT_CUSTOM_SETTING_FILE_NAME;
	
	//@Value("${clobaframe.setting.extraSettingFileName}")
	private String extraSettingFileName; // = DEFAULT_EXTRA_SETTING_FILE_NAME;
	
	//@Inject
	private ResourceLoader resourceLoader;

	private Collection<PostApplicationSetting> postApplicationSettings;
	
	private Map<String, Object> setting = new LinkedHashMap<String, Object>();
	
	private List<ApplicationSettingProvider> applicationSettingProviders;
	
	private ApplicationSettingRepository applicationSettingRepository;

	private final Logger logger = LoggerFactory.getLogger(ApplicationSettingImpl.class);

	public void setRootConfigFileName(String rootConfigFileName) {
		this.rootConfigFileName = rootConfigFileName;
	}

	@Override
	public void setPostApplicationSettings(Collection<PostApplicationSetting> postApplicationSettings) {
		this.postApplicationSettings = postApplicationSettings;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		loadRootConfig();
		initComponents();
	}
	
	private void loadRootConfig(){
		
		Map<String, Object> rootSetting = null;
		SettingAccess settingAccess = new AbstractPropertiesFileSettingAccess() {};
		Resource resource = resourceLoader.getResource(rootConfigFileName);
		InputStream in = null;
		try{
			in = resource.getInputStream();
			rootSetting = settingAccess.read(in);
		}catch(IOException e) {
			// ignore
			logger.error("Load root application configuration failed: {}", e.getMessage());
		}finally {
			IOUtils.closeQuietly(in);
		}
		
		if (rootSetting == null || rootSetting.isEmpty()) {
			return;
		}
		
		this.appName = getRootConfigValue(rootSetting, "appName", null);
		this.dataFolder = getRootConfigValue(rootSetting, "dataFolder", DEFAULT_DATA_FOLDER);
		this.autoCreateDataFolder = Boolean.parseBoolean(getRootConfigValue(rootSetting, "autoCreateDataFolder", "true"));
		this.defaultSettingFileName = getRootConfigValue(rootSetting, "defaultSettingFileName", DEFAULT_SETTING_FILE_NAME);
		this.customSettingFileName = getRootConfigValue(rootSetting, "customSettingFileName", DEFAULT_CUSTOM_SETTING_FILE_NAME);
		this.extraSettingFileName = getRootConfigValue(rootSetting, "extraSettingFileName", DEFAULT_EXTRA_SETTING_FILE_NAME);
		
		Assert.hasText(appName, "App name should not empty.");
		
		putBaseSetting();
	}
	
	private String getRootConfigValue(Map<String, Object> rootSetting, String key, String defaultValue) {
		String value = (String)rootSetting.get(key);
		return (value != null ? value : defaultValue);
	}
	
	private void putBaseSetting() {
		// put the base settings.
		setting.clear();
		setting.put("setting.appName", appName);
		setting.put("setting.dataFolder", dataFolder);
		setting.put("setting.autoCreatedataFolder", autoCreateDataFolder);
		setting.put("setting.defaultSettingFileName", defaultSettingFileName);
		setting.put("setting.customSettingFileName", customSettingFileName);
		setting.put("setting.extraSettingFileName", extraSettingFileName);
	}
	
	private void initComponents(){
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
					file.mkdirs();
				}catch(Exception e){
					logger.error("Fail to create application data folder [{}].", file.getAbsolutePath());
				}
			}
		}
		
		// add setting provider, from lower priority to higher priority.
		applicationSettingProviders = new ArrayList<ApplicationSettingProvider>();
		applicationSettingProviders.add(new PropertiesApplicationSettingProvider(resourceLoader, defaultSettingFileName));
		applicationSettingProviders.add(new SystemEnvironmentSettingProvider());
		applicationSettingProviders.add(new SystemPropertiesSettingProvider());
		applicationSettingProviders.add(new JsonApplicationSettingProvider(dataFolderValue, customFileNameValue));
		applicationSettingProviders.add(new JsonApplicationSettingProvider(dataFolderValue, extraFileNameValue));

		// merge all settings.
		for(ApplicationSettingProvider provider : applicationSettingProviders){
			Map<String, Object> map = provider.getAll();
			setting = Utils.merge(setting, map);
		}
		
		// set setting repository
		applicationSettingRepository = new CustomApplicationSettingRepository(dataFolder, customSettingFileName);
		
		// execute post setting works.
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
		
		putBaseSetting();

		// merge all settings.
		for(ApplicationSettingProvider provider : applicationSettingProviders){;
			Map<String, Object> map = provider.getAll();
			setting = Utils.merge(setting, map);
		}
	}
}
