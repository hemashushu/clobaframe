package org.archboy.clobaframe.setting.common.application.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import org.archboy.clobaframe.setting.common.application.ApplicationSettingProvider;

/**
 *
 * @author yang
 */
public class EnvironmentVariablesSettingProvider implements ApplicationSettingProvider {

	@Override
	public int getOrder() {
		return PRIORITY_LOW;
	}

	@Override
	public Map<String, Object> list() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Map<String, String> env = System.getenv();
		for(Map.Entry<String, String> entry : env.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}
}
