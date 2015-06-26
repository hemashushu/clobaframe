package org.archboy.clobaframe.setting.application.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.setting.support.Utils;
import org.archboy.clobaframe.setting.application.ApplicationSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author yang
 */
public class JsonApplicationSettingRepository extends JsonApplicationSettingProvider implements ApplicationSettingRepository {

	private final Logger logger = LoggerFactory.getLogger(JsonApplicationSettingRepository.class);
	
	public JsonApplicationSettingRepository(String dataFolder, String fileName) {
		super(dataFolder, fileName);
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
