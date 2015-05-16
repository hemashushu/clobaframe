package org.archboy.clobaframe.setting.application.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.setting.support.AbstractPropertiesFileSettingAccess;
import org.archboy.clobaframe.setting.application.ApplicationSettingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 */
public class DefaultApplicationSettingProvider extends AbstractPropertiesFileSettingAccess implements ApplicationSettingProvider {

	private ResourceLoader resourceLoader;
	private String fileName;
	
	private final Logger logger = LoggerFactory.getLogger(DefaultApplicationSettingProvider.class);
	
	public DefaultApplicationSettingProvider(ResourceLoader resourceLoader, String fileName) {
		super();
		this.resourceLoader = resourceLoader;
		this.fileName = fileName;
	}
	
	@Override
	public int getPriority() {
		return PRIORITY_LOWER;
	}

	@Override
	public Map<String, Object> getAll() {
		Resource resource = resourceLoader.getResource(fileName);
		
		if (!resource.exists()) {
			logger.warn("Default application setting [{}] not found.", fileName);
		}else{
			logger.info("Load default application setting [{}]", fileName);
			InputStream in = null;
			try{
				in = resource.getInputStream();
				return read(in);
			}catch(IOException e) {
				// ignore
				logger.error("Load default application setting failed: {}", e.getMessage());
			}finally {
				IOUtils.closeQuietly(in);
			}
		}
		
		return new LinkedHashMap<String, Object>();
	}
}
