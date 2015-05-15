package org.archboy.clobaframe.setting.instance.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.setting.support.AbstractPropertiesFileSettingAccess;
import org.archboy.clobaframe.setting.instance.InstanceSettingProvider;
import org.archboy.clobaframe.setting.application.ApplicationSetting;
import org.archboy.clobaframe.setting.application.impl.ApplicationSettingImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 */
@Named
public class DefaultInstanceSettingProvider extends AbstractPropertiesFileSettingAccess implements InstanceSettingProvider {

	@Inject
	private ResourceLoader resourceLoader;
	
	@Inject
	private ApplicationSetting applicationSetting;

	private final Logger logger = LoggerFactory.getLogger(DefaultInstanceSettingProvider.class);
	
	@Override
	public int getPriority() {
		return PRIORITY_LOWER;
	}

	@Override
	public Map<String, Object> getAll() {
		
		String fileName = (String)applicationSetting.getValue("instance.defaultSettingFileName");
		if (fileName == null) {
			return new HashMap<String, Object>();
		}
		
		Resource resource = resourceLoader.getResource(fileName);
		if (!resource.exists()) {
			logger.warn("Default instance setting [{}] not found.", fileName);
		}else{
			logger.info("Load default instance setting [{}]", fileName);
			
			InputStream in = null;
			try{
				in = resource.getInputStream();
				return read(in);
			}catch(IOException e) {
				// ignore
				logger.error("Load default instance setting failed: " + e.getMessage());
			}finally {
				IOUtils.closeQuietly(in);
			}
		}
		
		return new HashMap<String, Object>();
	}
}
