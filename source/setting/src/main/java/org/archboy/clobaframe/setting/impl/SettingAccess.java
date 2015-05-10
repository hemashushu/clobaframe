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
public interface SettingAccess {
	
	/**
	 * 
	 * @param in the caller should close the input stream manually.
	 * @return
	 * @throws IOException 
	 */
	Map<String, Object> read(InputStream in) throws IOException;
	
	/**
	 * Save the setting into OutputStream.
	 * The setting items should not be ambiguity.
	 * e.g. the wrong way:
	 * foo.bar = 123456
	 * foo.bar.name = hello
	 * 
	 * the right way:
	 * foo.bar.id = 123456
	 * foo.bar.name = hello
	 * 
	 * @param outputStream The caller should close the output stream manually.
	 * @param setting
	 * @throws IOException 
	 */
	void write(
			OutputStream outputStream, 
			Map<String, Object> setting)
			throws IOException;
}
