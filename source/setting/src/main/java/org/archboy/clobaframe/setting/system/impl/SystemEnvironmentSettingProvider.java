package org.archboy.clobaframe.setting.system.impl;

import java.util.HashMap;
import java.util.Map;
import org.archboy.clobaframe.setting.system.SystemSettingProvider;

/**
 *
 * @author yang
 */
public class SystemEnvironmentSettingProvider implements SystemSettingProvider {

	@Override
	public int getPriority() {
		return PRIORITY_LOW;
	}

	@Override
	public Map<String, Object> get() {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, String> env = System.getenv();
		for(Map.Entry<String, String> entry : env.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
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
