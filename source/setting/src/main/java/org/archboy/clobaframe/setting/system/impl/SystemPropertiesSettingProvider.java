package org.archboy.clobaframe.setting.system.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.archboy.clobaframe.setting.system.SystemSettingProvider;

/**
 *
 * @author yang
 */
public class SystemPropertiesSettingProvider implements SystemSettingProvider {

	@Override
	public int getPriority() {
		return PRIORITY_NORMAL;
	}

	@Override
	public Map<String, Object> get() {
		Map<String, Object> map = new HashMap<String, Object>();
		Properties properties = System.getProperties();
		for(Map.Entry<Object, Object> entry : properties.entrySet()) {
			map.put((String)entry.getKey(), entry.getValue());
		}
		return map;
	}

	@Override
	public boolean canWrite() {
		return false;
	}

	@Override
	public void set(Map<String, Object> item) {
		//
	}

	@Override
	public void set(String key, Object value) {
		//
	}
	
}
