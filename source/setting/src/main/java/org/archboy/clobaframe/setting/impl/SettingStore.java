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
public interface SettingStore {
	
	Map<String, Object> read(InputStream in) throws IOException;
	
	void write(
			InputStream in, OutputStream outputStream, 
			Map<String, Object> setting)
			throws IOException;
	
	void write(
			InputStream in, OutputStream outputStream, 
			String key, Object value)
			throws IOException;
}
