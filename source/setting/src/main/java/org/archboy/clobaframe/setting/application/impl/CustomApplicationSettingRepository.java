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

/**
 *
 * @author yang
 */
public class CustomApplicationSettingRepository extends CustomApplicationSettingProvider implements ApplicationSettingRepository {

	public CustomApplicationSettingRepository(String dataFolder, String fileName) {
		super(dataFolder, fileName);
	}

	@Override
	public void update(Map<String, Object> item) {
		Map<String, Object> map = Utils.merge(getAll(), item);
		
		// save
		File file = new File(dataFolder, fileName);
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			write(out, map);
		} catch (IOException e) {
			// ignore
			logger.error("Save custom application setting to [{}] failed: {}", 
					file.getAbsolutePath(),
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
