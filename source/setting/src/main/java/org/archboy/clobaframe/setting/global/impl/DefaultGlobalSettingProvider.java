package org.archboy.clobaframe.setting.global.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.setting.SettingProvider;
import org.archboy.clobaframe.setting.application.ApplicationSetting;
import org.archboy.clobaframe.setting.global.GlobalSettingProvider;
import org.archboy.clobaframe.setting.support.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 */
@Named
public class DefaultGlobalSettingProvider implements GlobalSettingProvider {
	//, ResourceLoaderAware {

	public static final String NAME = "defaultGlobalSetting";
	
	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private ApplicationSetting applicationSetting;
	
	//private static final String DEFAULT_GLOBAL_SETTING_FILE_NAME = "classpath:global.properties";
	public static final String DEFAULT_GLOBAL_SETTING_FILE_NAME = "";
	
	public static final String SETTING_KEY_GLOBAL_SETTING_FILE_NAME = "clobaframe.setting.defaultGlobalSettingFileName";
	
	//@Value("${" + SETTING_KEY_GLOBAL_SETTING_FILE_NAME + ":" + DEFAULT_GLOBAL_SETTING_FILE_NAME + "}")
	//public String defaultGlobalSettingFileName;
	
	private final Logger logger = LoggerFactory.getLogger(DefaultGlobalSettingProvider.class);

//	public void setDefaultGlobalSettingFileName(String defaultGlobalSettingFileName) {
//		this.defaultGlobalSettingFileName = defaultGlobalSettingFileName;
//	}

	//@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public int getOrder() {
		return SettingProvider.PRIORITY_LOWER;
	}

	@Override
	public Map<String, Object> list() {
		String defaultGlobalSettingFileName = (String)applicationSetting.getValue(
				SETTING_KEY_GLOBAL_SETTING_FILE_NAME, 
				DEFAULT_GLOBAL_SETTING_FILE_NAME);
		
		if (StringUtils.isEmpty(defaultGlobalSettingFileName)){
			return new LinkedHashMap<String, Object>();
		}
		
		Resource resource = resourceLoader.getResource(defaultGlobalSettingFileName);
		if (!resource.exists()) {
			logger.error("Default global setting [{}] not found.", resource.getFilename());
			return new LinkedHashMap<String, Object>();
		}

		logger.info("Loading default global setting [{}]", resource.getFilename());

		InputStream in = null;
		try{
			in = resource.getInputStream();
			return Utils.readProperties(in);
		}catch(IOException e) {
			throw new RuntimeException(
					String.format("Load default global setting [%s] failed.", 
					resource.getFilename()), e);
		}finally {
			IOUtils.closeQuietly(in);
		}
		
	}
}
