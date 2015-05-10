package org.archboy.clobaframe.setting.system.impl;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.setting.impl.Support;
import org.archboy.clobaframe.setting.system.SystemSetting;
import org.archboy.clobaframe.setting.system.SystemSettingProvider;
import org.archboy.clobaframe.setting.system.SystemSettingRepository;
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

	private static final String DEFAULT_DATA_DIR = "/var/lib/${setting.appName}";
	private static final String DEFAULT_SETTING_FILE_NAME = "classpath:default.properties";
	private static final String DEFAULT_CUSTOM_SETTING_FILE_NAME = "settings.json";
	private static final String DEFAULT_EXTRA_SETTING_FILE_NAME = "extra.json";
	
	@Value("${clobaframe.setting.appName}")
	private String appName;
	
	@Value("${clobaframe.setting.dataDir}")
	private String dataDir = DEFAULT_DATA_DIR;

	@Value("${clobaframe.setting.defaultSettingFileName}")
	private String defaultSettingFileName = DEFAULT_SETTING_FILE_NAME;
	
	@Value("${clobaframe.setting.customSettingFileName}")
	private String customSettingFileName = DEFAULT_CUSTOM_SETTING_FILE_NAME;
	
	@Value("${clobaframe.setting.extraSettingFileName}")
	private String extraSettingFileName = DEFAULT_EXTRA_SETTING_FILE_NAME;
	
	@Inject
	private ResourceLoader resourceLoader;
	
	private Map<String, Object> setting = new LinkedHashMap<String, Object>();
	
	private List<SystemSettingProvider> systemSettingProviders;
	
	private SystemSettingRepository systemSettingRepository;

	private final Logger logger = LoggerFactory.getLogger(SystemSettingImpl.class);
	
	@PostConstruct
	public void init(){
		// add default values
		setting.put("setting.appName", appName);
		setting.put("setting.dataDir", Support.resolvePlaceholder(setting, dataDir));
		setting.put("setting.defaultSettingFileName", Support.resolvePlaceholder(setting, defaultSettingFileName));
		setting.put("setting.customSettingFileName", Support.resolvePlaceholder(setting, customSettingFileName));
		setting.put("setting.extraSettingFileName", Support.resolvePlaceholder(setting, extraSettingFileName));
				
		// add setting provider
		systemSettingProviders = new ArrayList<SystemSettingProvider>();
		systemSettingProviders.add(new PropertiesFileSystemSettingProvider(resourceLoader, defaultSettingFileName));
		systemSettingProviders.add(new EnvironmentSettingProvider());
		systemSettingProviders.add(new PropertiesSettingProvider());
		systemSettingProviders.add(new JsonSystemSettingProvider(dataDir, customSettingFileName));
		systemSettingProviders.add(new JsonSystemSettingProvider(dataDir, extraSettingFileName));
		
		for(SystemSettingProvider provider : systemSettingProviders){
			Map<String, Object> map = provider.getAll();
			setting = Support.merge(setting, map);
		}
		

		this.systemSettingRepository = (SystemSettingRepository)
				(new JsonSystemSettingProvider(dataDir, customSettingFileName));

	}
	
	@Override
	public Object get(String key) {
		Object value = setting.get(key);
		return (value == null ? null : Support.resolvePlaceholder(setting, value));
	}

	@Override
	public Object get(String key, Object defaultValue) {
		Object value = get(key);
		return (value == null ? defaultValue : value);
	}

	@Override
	public void set(String key, Object value) {
		systemSettingRepository.update(key, value);
	}

	@Override
	public void set(Map<String, Object> items) {
		systemSettingRepository.update(items);
	}

	@Override
	public Map<String, Object> getAll() {
		return setting;
	}
	
}
