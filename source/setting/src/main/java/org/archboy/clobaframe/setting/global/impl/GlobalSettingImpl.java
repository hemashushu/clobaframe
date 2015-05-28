package org.archboy.clobaframe.setting.global.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import org.archboy.clobaframe.setting.global.GlobalSettingProvider;
import org.archboy.clobaframe.setting.global.GlobalSettingRepository;
import org.archboy.clobaframe.setting.support.Utils;
import org.archboy.clobaframe.setting.global.GlobalSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author yang
 */
@Named
public class GlobalSettingImpl implements GlobalSetting {
	
	private Map<String, Object> setting = new LinkedHashMap<String, Object>();
	
	@Autowired(required = false)
	private List<GlobalSettingProvider> globalSettingProviders;

	@Autowired(required = false)
	private GlobalSettingRepository globalSettingRepository;
	
	private final Logger logger = LoggerFactory.getLogger(GlobalSettingImpl.class);
	
	@PostConstruct
	@Override
	public void refresh(){
		
		// clear setting
		setting.clear();
		
		if (globalSettingProviders == null || globalSettingProviders.isEmpty()) {
			return;
		}
		
		// merge all providers setting
		for(int idx = globalSettingProviders.size() -1; idx >=0; idx--){
			GlobalSettingProvider provider = globalSettingProviders.get(idx);
			Map<String, Object> map = provider.getAll();
			setting = Utils.merge(setting, map);
		}
	}
	
	@Override
	public Object getValue(String key) {
		Object value = setting.get(key);
		return (value == null ? null : Utils.resolvePlaceholder(setting, value));
	}

	@Override
	public Object getValue(String key, Object defaultValue) {
		Object value = getValue(key);
		return (value == null ? defaultValue : value);
	}

	@Override
	public Object get(String key) {
		return setting.get(key);
	}

	@Override
	public Map<String, Object> getAll() {
		return setting;
	}

	@Override
	public void set(String key, Object value) {
		if (globalSettingRepository == null){
			throw new NullPointerException("No global setting repository.");
		}
		
		globalSettingRepository.update(key, value);
	}

	@Override
	public void set(Map<String, Object> items) {
		if (globalSettingRepository == null){
			throw new NullPointerException("No global setting repository.");
		}
		
		globalSettingRepository.update(items);
	}
}
