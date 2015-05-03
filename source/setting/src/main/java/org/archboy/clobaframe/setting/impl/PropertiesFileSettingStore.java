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
public abstract class PropertiesFileSettingStore extends AbstractSettingStore {
	
	@Override
	public Map<String, Object> read(InputStream in) throws IOException{
		Properties properties = new Properties();
		properties.load(in);
		
		Map<String, Object> setting = new HashMap<String, Object>();
		for(Map.Entry<Object, Object> entry : properties.entrySet()){
			setting.put((String)entry.getKey(), entry.getValue());
		}
		
		return setting;
	}
	
	@Override
	public void write(
			InputStream in, OutputStream outputStream, 
			Map<String, Object> setting)
			throws IOException {
		Map<String, Object> origin = read(in);
		Support.merge(origin, setting);
		
		Properties properties = new Properties();
		properties.putAll(origin);
		properties.store(outputStream, null);
	}

}
