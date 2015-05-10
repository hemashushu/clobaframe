package org.archboy.clobaframe.setting.system.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.archboy.clobaframe.setting.system.SystemSettingProvider;

/**
 *
 * @author yang
 */
public class EnvironmentSettingProvider implements SystemSettingProvider {

	@Override
	public int getPriority() {
		return PRIORITY_LOW;
	}

	@Override
	public Map<String, Object> getAll() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Map<String, String> env = System.getenv();
		for(Map.Entry<String, String> entry : env.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}
}
