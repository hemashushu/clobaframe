package org.archboy.clobaframe.setting.application.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.setting.SettingRepository;
import org.archboy.clobaframe.setting.support.Utils;
import org.archboy.clobaframe.setting.application.ApplicationSetting;
import org.archboy.clobaframe.setting.application.ApplicationSettingProvider;
import org.archboy.clobaframe.setting.application.ApplicationSettingRepository;
import org.archboy.clobaframe.setting.application.PostApplicationSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 */
@Named
public class ApplicationSettingImpl implements ApplicationSetting {

	private static final String DEFAULT_DATA_FOLDER = "/var/lib/${clobaframe.setting.appName}";
	private static final boolean DEFAULT_AUTO_CREATE_DATA_FOLDER = true;
	private static final String DEFAULT_SETTING_FILE_NAME = "classpath:application-default.properties";
	private static final String DEFAULT_CUSTOM_SETTING_FILE_NAME = "settings.json";
	private static final String DEFAULT_EXTRA_SETTING_FILE_NAME = "extra.json";
	
	@Value("${clobaframe.setting.appName}")
	private String appName;
	
	@Value("${clobaframe.setting.dataFolder}")
	private String dataFolder = DEFAULT_DATA_FOLDER;
	
	@Value("${colbaframe.setting.autoCreateDataFolder}")
	private boolean autoCreateDataFolder = DEFAULT_AUTO_CREATE_DATA_FOLDER;

	@Value("${clobaframe.setting.defaultSettingFileName}")
	private String defaultSettingFileName = DEFAULT_SETTING_FILE_NAME;
	
	@Value("${clobaframe.setting.customSettingFileName}")
	private String customSettingFileName = DEFAULT_CUSTOM_SETTING_FILE_NAME;
	
	@Value("${clobaframe.setting.extraSettingFileName}")
	private String extraSettingFileName = DEFAULT_EXTRA_SETTING_FILE_NAME;
	
	@Inject
	private ResourceLoader resourceLoader;

	@Autowired(required = false)
	private List<PostApplicationSetting> postApplicationSettings = new ArrayList<PostApplicationSetting>();
	
	private Map<String, Object> setting = new LinkedHashMap<String, Object>();
	
	private List<ApplicationSettingProvider> applicationSettingProviders;
	
	private ApplicationSettingRepository applicationSettingRepository;

	private final Logger logger = LoggerFactory.getLogger(ApplicationSettingImpl.class);
	
	@PostConstruct
	public void init(){
		// add setting provider, from lower priority to higher priority.
		applicationSettingProviders = new ArrayList<ApplicationSettingProvider>();
		applicationSettingProviders.add(new DefaultApplicationSettingProvider(resourceLoader, defaultSettingFileName));
		applicationSettingProviders.add(new EnvironmentSettingProvider());
		applicationSettingProviders.add(new PropertiesSettingProvider());

		// put the base settings.
		setting.put("setting.appName", appName);
		setting.put("setting.dataFolder", dataFolder);
		setting.put("setting.autoCreatedataFolder", autoCreateDataFolder);
		setting.put("setting.defaultSettingFileName", defaultSettingFileName);
		setting.put("setting.customSettingFileName", customSettingFileName);
		setting.put("setting.extraSettingFileName", extraSettingFileName);
		
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
					file.mkdirs();
				}catch(Exception e){
					logger.error("Fail to create application data folder [{}].", file.getAbsolutePath());
				}
			}
		}
		
		int baseProviderCount = applicationSettingProviders.size();
		
		applicationSettingProviders.add(new CustomApplicationSettingProvider(dataFolderValue, customFileNameValue));
		applicationSettingProviders.add(new CustomApplicationSettingProvider(dataFolderValue, extraFileNameValue));

		// merge the remain settings
		for (int idx=baseProviderCount; idx< applicationSettingProviders.size(); idx++){
			Map<String, Object> map = applicationSettingProviders.get(idx).getAll();
			setting = Utils.merge(setting, map);
		}
		
		// set setting repository
		applicationSettingRepository = new CustomApplicationSettingRepository(dataFolder, customSettingFileName);
		
		// execute post setting works.
		for(PostApplicationSetting postApplicationSetting : postApplicationSettings) {
			postApplicationSetting.execute(this);
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
		
		setting.clear();
		
		// add base setting values
		setting.put("setting.appName", appName);
		setting.put("setting.dataDir", dataFolder);
		setting.put("setting.defaultSettingFileName", defaultSettingFileName);
		setting.put("setting.customSettingFileName", customSettingFileName);
		setting.put("setting.extraSettingFileName", extraSettingFileName);

		// merge all settings.
		for(ApplicationSettingProvider provider : applicationSettingProviders){;
			Map<String, Object> map = provider.getAll();
			setting = Utils.merge(setting, map);
		}
	}
}
