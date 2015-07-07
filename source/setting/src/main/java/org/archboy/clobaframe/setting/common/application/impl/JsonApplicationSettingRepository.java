package org.archboy.clobaframe.setting.common.application.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.setting.support.Utils;
import org.archboy.clobaframe.setting.common.application.ApplicationSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 */
public class JsonApplicationSettingRepository extends JsonApplicationSettingProvider implements ApplicationSettingRepository {

	private final Logger logger = LoggerFactory.getLogger(JsonApplicationSettingRepository.class);
	
	public JsonApplicationSettingRepository(String dataFolder, String fileName) {
		super(dataFolder, fileName);
	}

	public JsonApplicationSettingRepository(ResourceLoader resourceLoader, String fileName) {
		super(resourceLoader, fileName);
	}

	public JsonApplicationSettingRepository(Resource resource) {
		super(resource);
	}
	
	@Override
	public void update(Map<String, Object> item) {
		Map<String, Object> map = Utils.merge(list(), item);
		
		// save
		OutputStream out = null;
		try {
			File file = resource.getFile();
			out = new FileOutputStream(file);
			Utils.writeJson(out, map);
		} catch (IOException e) {
			// ignore
			logger.error("Save setting to resource [{}] failed: {}", 
					resource.getFilename(),
					e.getMessage());
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	@Override
	public void update(String key, Object value) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put(key, value);
		update(map);
	}
	
}
