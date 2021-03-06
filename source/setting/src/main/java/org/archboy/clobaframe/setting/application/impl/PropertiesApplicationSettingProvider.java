package org.archboy.clobaframe.setting.application.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.setting.application.ApplicationSettingProvider;
import org.archboy.clobaframe.setting.support.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 */
public class PropertiesApplicationSettingProvider implements ApplicationSettingProvider {
	
	protected Resource resource;
	
	private final Logger logger = LoggerFactory.getLogger(PropertiesApplicationSettingProvider.class);
	
	public PropertiesApplicationSettingProvider(ResourceLoader resourceLoader, String fileName) {
		this.resource = resourceLoader.getResource(fileName);
	}

	public PropertiesApplicationSettingProvider(Resource resource) {
		this.resource = resource;
	}

	@Override
	public String getName() {
		// return resource file name as provider name.
		return resource.getFilename();
	}
	
	@Override
	public int getOrder() {
		return PRIORITY_LOWER;
	}

	@Override
	public Map<String, Object> list() {
		if (!resource.exists()) {
			logger.warn("Setting resource [{}] not found.", resource.getFilename());
		}else{
			logger.info("Loading setting resource [{}]", resource.getFilename());
			InputStream in = null;
			try{
				in = resource.getInputStream();
				return Utils.readProperties(in);
			}catch(IOException e) {
				// ignore
				logger.error("Load setting resource [{}] failed: {}", resource.getFilename(), e.getMessage());
			}finally {
				IOUtils.closeQuietly(in);
			}
		}
		
		return new LinkedHashMap<String, Object>();
	}
}
