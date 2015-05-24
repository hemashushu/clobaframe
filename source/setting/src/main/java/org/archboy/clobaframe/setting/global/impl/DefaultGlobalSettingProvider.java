package org.archboy.clobaframe.setting.global.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.setting.global.GlobalSettingProvider;
import org.archboy.clobaframe.setting.application.ApplicationSetting;
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

	@Inject
	private ResourceLoader resourceLoader;
	
	@Inject
	private ApplicationSetting applicationSetting;

	private static final String DEFAULT_GLOBAL_SETTING_FILE_NAME = "global.properties";
	
	//@Value("${clobaframe.setting.defaultGlobalSettingFileName}")
	private String defaultGlobalSettingFileName = DEFAULT_GLOBAL_SETTING_FILE_NAME;
	
	private final Logger logger = LoggerFactory.getLogger(DefaultGlobalSettingProvider.class);
	
	@Override
	public int getOrder() {
		return 10;
	}

	@Override
	public Map<String, Object> getAll() {
		
//		String fileName = (String)applicationSetting.getValue("setting.defaultGlobalSettingFileName");
//		if (fileName == null) {
//			return new LinkedHashMap<String, Object>();
//		}
		
		Resource resource = resourceLoader.getResource(defaultGlobalSettingFileName);
		if (!resource.exists()) {
			logger.warn("Default instance setting [{}] not found.", defaultGlobalSettingFileName);
		}else{
			logger.info("Load default instance setting [{}]", defaultGlobalSettingFileName);
			
			InputStream in = null;
			try{
				in = resource.getInputStream();
				return Utils.readProperties(in);
			}catch(IOException e) {
				// ignore
				logger.error("Load default global setting failed: " + e.getMessage());
			}finally {
				IOUtils.closeQuietly(in);
			}
		}
		
		return new LinkedHashMap<String, Object>();
	}
}
