package org.archboy.clobaframe.setting.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author yang
 */
public abstract class AbstractPropertiesFileSettingAccess implements SettingAccess {
	
	@Override
	public Map<String, Object> read(InputStream in) throws IOException{
		Properties properties = new OrderedProperties();
		properties.load(in);
		
		Map<String, Object> setting = new LinkedHashMap<String, Object>();
		for(Map.Entry<Object, Object> entry : properties.entrySet()){
			setting.put((String)entry.getKey(), entry.getValue());
		}
		
		return setting;
	}
	
	@Override
	public void write(
			OutputStream outputStream, 
			Map<String, Object> setting)
			throws IOException {
		Properties properties = new OrderedProperties();
		properties.putAll(setting);
		properties.store(outputStream, null);
	}

}
