package org.archboy.clobaframe.setting.application.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.archboy.clobaframe.setting.application.ApplicationSettingProvider;

/**
 *
 * @author yang
 */
public class SystemPropertiesSettingProvider implements ApplicationSettingProvider {

	public static final String NAME = "systemProperties";

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public int getOrder() {
		return PRIORITY_NORMAL;
	}

	@Override
	public Map<String, Object> list() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Properties properties = System.getProperties();
		for(Map.Entry<Object, Object> entry : properties.entrySet()) {
			map.put((String)entry.getKey(), entry.getValue());
		}
		return map;
	}
	
}
