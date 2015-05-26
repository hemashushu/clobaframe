package org.archboy.clobaframe.setting.global.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
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

	@Inject
	private ResourceLoader resourceLoader;

	private static final String DEFAULT_GLOBAL_SETTING_FILE_NAME = "classpath:global.properties";
	
	@Value("${clobaframe.setting.defaultGlobalSettingFileName:" + DEFAULT_GLOBAL_SETTING_FILE_NAME + "}")
	private String defaultGlobalSettingFileName;
	
	private final Logger logger = LoggerFactory.getLogger(DefaultGlobalSettingProvider.class);
	
	@Override
	public int getOrder() {
		return 10;
	}

	@Override
	public Map<String, Object> getAll() {
		Resource resource = resourceLoader.getResource(defaultGlobalSettingFileName);
		if (!resource.exists()) {
			logger.warn("Default global setting [{}] not found.", resource.getFilename());
		}else{
			logger.info("Loading default global setting [{}]", resource.getFilename());
			
			InputStream in = null;
			try{
				in = resource.getInputStream();
				return Utils.readProperties(in);
			}catch(IOException e) {
				// ignore
				logger.error("Load default global setting [{}] failed: {}", resource.getFilename(), e.getMessage());
			}finally {
				IOUtils.closeQuietly(in);
			}
		}
		
		return new LinkedHashMap<String, Object>();
	}
}
