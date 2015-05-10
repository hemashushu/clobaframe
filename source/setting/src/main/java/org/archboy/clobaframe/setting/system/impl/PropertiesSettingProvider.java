package org.archboy.clobaframe.setting.system.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.archboy.clobaframe.setting.system.SystemSettingProvider;

/**
 *
 * @author yang
 */
public class PropertiesSettingProvider implements SystemSettingProvider {

	@Override
	public int getPriority() {
		return PRIORITY_NORMAL;
	}

	@Override
	public Map<String, Object> getAll() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Properties properties = System.getProperties();
		for(Map.Entry<Object, Object> entry : properties.entrySet()) {
			map.put((String)entry.getKey(), entry.getValue());
		}
		return map;
	}
	
}
