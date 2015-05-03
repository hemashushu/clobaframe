package org.archboy.clobaframe.setting.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author yang
 */
public abstract class AbstractSettingStore implements SettingStore {
	
	@Override
	public void write(
			InputStream in, OutputStream outputStream, 
			String key, Object value)
			throws IOException {
		Map<String, Object> setting = new HashMap<String, Object>();
		setting.put(key, value);
		write(in, outputStream, setting);
	}
}
