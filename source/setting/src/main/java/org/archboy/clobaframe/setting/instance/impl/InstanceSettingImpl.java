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
import org.archboy.clobaframe.setting.support.Utils;
import org.archboy.clobaframe.setting.instance.InstanceSetting;
import org.archboy.clobaframe.setting.application.ApplicationSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
	
//	@Inject
//	private List<InstanceSettingRepository> instanceSettingRepositorys;

	@Autowired(required = false)
	private InstanceSettingRepository instanceSettingRepository;
	
	private Map<String, Object> setting = new LinkedHashMap<String, Object>();
	
	private final Logger logger = LoggerFactory.getLogger(InstanceSettingImpl.class);
	
	@PostConstruct
	public void init(){
		
		// sort providers, from higher(smaller number) priority to lower.
		instanceSettingProviders.sort(new Comparator<InstanceSettingProvider>() {
			@Override
			public int compare(InstanceSettingProvider o1, InstanceSettingProvider o2) {
				return o1.getPriority() - o2.getPriority();
			}
		});
		
		// set repository
//		if (instanceSettingRepositorys.size() > 1){
//			for(InstanceSettingRepository repository : instanceSettingRepositorys){
//				if (repository instanceof NullInstanceSettingRepository){
//					continue;
//				}
//				this.instanceSettingRepository = repository;
//				break;
//			}
//		}else{
//			this.instanceSettingRepository = instanceSettingRepositorys.iterator().next();
//		}
		
		refresh();
	}
	
	@Override
	public void refresh(){
		
		// clear setting
		setting.clear();
		
		// merge system setting
		setting = Utils.merge(setting, systemSetting.getAll());

		// merge all providers setting
		for(int idx = instanceSettingProviders.size() -1; idx >=0; idx--){
			InstanceSettingProvider provider = instanceSettingProviders.get(idx);
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
		instanceSettingRepository.update(key, value);
	}

	@Override
	public void set(Map<String, Object> items) {
		instanceSettingRepository.update(items);
	}
}
