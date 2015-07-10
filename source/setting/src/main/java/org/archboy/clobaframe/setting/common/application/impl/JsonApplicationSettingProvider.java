package org.archboy.clobaframe.setting.common.application.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.setting.common.application.ApplicationSettingProvider;
import org.archboy.clobaframe.setting.support.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 */
public class JsonApplicationSettingProvider implements ApplicationSettingProvider {
	
	protected Resource resource;
	
	private final Logger logger = LoggerFactory.getLogger(JsonApplicationSettingProvider.class);
	
	public JsonApplicationSettingProvider(String dataFolder, String fileName) {
		File file = new File(dataFolder, fileName);
		resource = new FileSystemResource(file);
	}
	
	public JsonApplicationSettingProvider(ResourceLoader resourceLoader, String fileName) {
		this.resource = resourceLoader.getResource(fileName);
	}

	public JsonApplicationSettingProvider(Resource resource) {
		this.resource = resource;
	}

	@Override
	public String getName() {
		// return resource file name as provider name.
		return resource.getFilename();
	}

	@Override
	public int getOrder() {
		return PRIORITY_HIGH;
	}

	@Override
	public Map<String, Object> list() {
		if (!resource.exists()){
			logger.warn("Setting resource [{}] not found.", resource.getFilename());
		}else {
			logger.info("Loading setting resource [{}]", resource.getFilename());

			InputStream in = null;
			try{
				in = resource.getInputStream();
				return Utils.readJson(in);
			}catch(IOException e){
				// ignore
				logger.error("Load setting resource [{}] failed: {}",resource.getFilename(), e.getMessage());
			}finally{
				IOUtils.closeQuietly(in);
			}
		}
		
		return new LinkedHashMap<String, Object>();
	}
	
}
