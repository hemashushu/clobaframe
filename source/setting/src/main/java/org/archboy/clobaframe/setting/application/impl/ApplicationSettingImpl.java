package org.archboy.clobaframe.setting.application.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.setting.impl.Support;
import org.archboy.clobaframe.setting.application.ApplicationSetting;
import org.archboy.clobaframe.setting.application.ApplicationSettingProvider;
import org.archboy.clobaframe.setting.application.ApplicationSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 */
@Named
public class ApplicationSettingImpl implements ApplicationSetting {

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
	
	private List<ApplicationSettingProvider> systemSettingProviders = new ArrayList<ApplicationSettingProvider>();;
	
	private ApplicationSettingRepository systemSettingRepository;

	private final Logger logger = LoggerFactory.getLogger(ApplicationSettingImpl.class);
	
	@PostConstruct
	public void init(){
		refresh();
	}
	
	@Override
	public void refresh(){
		// add default values
		setting.clear();
		setting.put("setting.appName", appName);
		setting.put("setting.dataDir", Support.resolvePlaceholder(setting, dataDir));
		setting.put("setting.defaultSettingFileName", Support.resolvePlaceholder(setting, defaultSettingFileName));
		setting.put("setting.customSettingFileName", Support.resolvePlaceholder(setting, customSettingFileName));
		setting.put("setting.extraSettingFileName", Support.resolvePlaceholder(setting, extraSettingFileName));
				
		// add setting provider
		systemSettingProviders.clear();
		systemSettingProviders.add(new DefaultApplicationSettingProvider(resourceLoader, defaultSettingFileName));
		systemSettingProviders.add(new EnvironmentSettingProvider());
		systemSettingProviders.add(new PropertiesSettingProvider());
		systemSettingProviders.add(new CustomApplicationSettingProvider(dataDir, customSettingFileName));
		systemSettingProviders.add(new CustomApplicationSettingProvider(dataDir, extraSettingFileName));
		
		// add setting repository
		systemSettingRepository = (ApplicationSettingRepository)
				(new CustomApplicationSettingProvider(dataDir, customSettingFileName));

		// merge all settings.
		systemSettingProviders.sort(new Comparator<ApplicationSettingProvider>() {
			@Override
			public int compare(ApplicationSettingProvider o1, ApplicationSettingProvider o2) {
				return o1.getPriority() - o2.getPriority();
			}
		});
		
		for(ApplicationSettingProvider provider : systemSettingProviders){
			Map<String, Object> map = provider.getAll();
			setting = Support.merge(setting, map);
		}
	}
	
	@Override
	public Object getValue(String key) {
		Object value = setting.get(key);
		return (value == null ? null : Support.resolvePlaceholder(setting, value));
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
