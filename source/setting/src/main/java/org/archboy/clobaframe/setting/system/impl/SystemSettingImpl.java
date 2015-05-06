package org.archboy.clobaframe.setting.system.impl;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jws.soap.InitParam;
import org.archboy.clobaframe.setting.impl.Support;
import org.archboy.clobaframe.setting.system.SystemSetting;
import org.archboy.clobaframe.setting.system.SystemSettingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 */
@Named
public class SystemSettingImpl implements SystemSetting {

	private static final String DEFAULT_DATA_DIR = "/var/lib";
	private static final String DEFAULT_FILE_NAME = "classpath:default.properties";
	private static final String DEFAULT_CUSTOM_FILE_NAME = "settings.json";
	
	@Value("${clobaframe.setting.appName}")
	private String appName;
	
	@Value("${clobaframe.setting.dataDir}")
	private String dataDir = DEFAULT_DATA_DIR;

	@Value("${clobaframe.setting.defaultFileName}")
	private String defaultFileName = DEFAULT_FILE_NAME;
	
	@Value("${clobaframe.setting.customFileName}")
	private String customFileName = DEFAULT_CUSTOM_FILE_NAME;
	
	@Inject
	private ResourceLoader resourceLoader;
	
	private Map<String, Object> setting = new HashMap<String, Object>();
	
	private final Logger logger = LoggerFactory.getLogger(SystemSettingImpl.class);
	
	private SystemSettingProvider customSystemSettingProvider;
	
	@PostConstruct
	public void SystemSettingImpl(){
		// load default
		SystemSettingProvider defaultSettingProvider = new DefaultSystemSettingProvider(resourceLoader, defaultFileName);
		setting = defaultSettingProvider.get();
		
		// load system environment
		SystemSettingProvider environmentSettingProvider = new SystemEnvironmentSettingProvider();
		Map<String, Object> environmentSetting = environmentSettingProvider.get();
		Support.merge(setting, environmentSetting);
		
		// load system properties
		SystemSettingProvider propertiesSettingProvider = new SystemPropertiesSettingProvider();
		Map<String, Object> propertiesSetting = propertiesSettingProvider.get();
		Support.merge(setting, propertiesSetting);
		
		// load custom setting
		CustomSystemSettingProvider customSettingProvider = new CustomSystemSettingProvider(dataDir, appName);
		Map<String, Object> customSetting = customSettingProvider.get();
		Support.merge(setting, customSetting);
		
		this.customSystemSettingProvider =  customSettingProvider;
	}
	
	@Override
	public Object get(String key) {
		return setting.get(key);
	}

	@Override
	public Object get(String key, Object defaultValue) {
		Object value = setting.get(key);
		return (value == null ? defaultValue : value);
	}

	@Override
	public void set(String key, Object value) {
		customSystemSettingProvider.set(key, value);
	}

	@Override
	public void set(Map<String, Object> items) {
		customSystemSettingProvider.set(items);
	}

	@Override
	public Map<String, Object> getAll() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
