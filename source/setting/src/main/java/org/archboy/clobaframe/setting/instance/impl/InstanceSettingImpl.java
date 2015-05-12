package org.archboy.clobaframe.setting.instance.impl;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.setting.instance.InstanceSettingProvider;
import org.archboy.clobaframe.setting.instance.InstanceSettingRepository;
import org.archboy.clobaframe.setting.impl.Support;
import org.archboy.clobaframe.setting.instance.InstanceSetting;
import org.archboy.clobaframe.setting.application.ApplicationSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author yang
 */
@Named
public class InstanceSettingImpl implements InstanceSetting {
	
	@Inject
	private ApplicationSetting systemSetting;
	
	@Inject
	private List<InstanceSettingProvider> instanceSettingProviders;
	
	@Inject
	private InstanceSettingRepository instanceSettingRepository;

	private Map<String, Object> setting = new LinkedHashMap<String, Object>();
	
	private final Logger logger = LoggerFactory.getLogger(InstanceSettingImpl.class);
	
	@PostConstruct
	public void init(){
		refresh();
	}
	
	@Override
	public void refresh(){
		
		// clear setting
		setting.clear();
		
		// merge system setting
		setting = Support.merge(setting, systemSetting.getAll());
		
		// merge all application settings.
		instanceSettingProviders.sort(new Comparator<InstanceSettingProvider>() {
			@Override
			public int compare(InstanceSettingProvider o1, InstanceSettingProvider o2) {
				return o1.getPriority() - o2.getPriority();
			}
		});
		
		for(InstanceSettingProvider provider : instanceSettingProviders){
			Map<String, Object> map = provider.getAll();
			setting = Support.merge(setting, map);
		}
	}
	
	@Override
	public Object getValue(String key) {
		Object value = setting.get(key);
		return (value == null ? null : Support.resolvePlaceholder(setting, value));
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
		instanceSettingRepository.update(key, value);
	}

	@Override
	public void set(Map<String, Object> items) {
		instanceSettingRepository.update(items);
	}

}
