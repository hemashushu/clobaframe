package org.archboy.clobaframe.setting.principal.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import org.archboy.clobaframe.setting.principal.Principal;
import org.archboy.clobaframe.setting.principal.PrincipalSetting;
import org.archboy.clobaframe.setting.principal.PrincipalSettingProvider;
import org.archboy.clobaframe.setting.principal.PrincipalSettingRepository;
import org.archboy.clobaframe.setting.support.Utils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author yang
 */
@Named
public class PrincipalSettingImpl implements PrincipalSetting {

	@Autowired(required = false)
	private List<PrincipalSettingProvider> principalSettingProviders;

	@Autowired(required = false)
	private List<PrincipalSettingRepository> principalSettingRepositorys;

	@Override
	public Object get(Principal profile, String key) {
		for(PrincipalSettingProvider provider : principalSettingProviders) {
			if (provider.support(profile)) {
				Object value = provider.get(profile, key);
				if (value != null) {
					return value;
				}
			}
		}
		
		return null;
	}

	@Override
	public Map<String, Object> getAll(Principal profile) {
		Map<String, Object> setting = new LinkedHashMap<String, Object>();
		for(PrincipalSettingProvider provider : principalSettingProviders) {
			if (provider.support(profile)) {
				setting = Utils.merge(setting, provider.getAll(profile));
			}
		}
		return setting;
	}

	@Override
	public void set(Principal profile, String key, Object value) {
		if (principalSettingRepositorys == null || principalSettingRepositorys.isEmpty()){
			throw new NullPointerException("No principal setting repository.");
		}
		
		for(PrincipalSettingRepository repository : principalSettingRepositorys) {
			if (repository.support(profile)) {
				repository.set(profile, key, value);
				return;
			}
		}
	}

	@Override
	public void set(Principal profile, Map<String, Object> items) {
		if (principalSettingRepositorys == null || principalSettingRepositorys.isEmpty()){
			throw new NullPointerException("No principal setting repository.");
		}
		
		for(PrincipalSettingRepository repository : principalSettingRepositorys) {
			if (repository.support(profile)) {
				repository.set(profile, items);
				return;
			}
		}
	}
	
}
