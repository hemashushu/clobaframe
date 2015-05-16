package org.archboy.clobaframe.setting.principal.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import org.archboy.clobaframe.setting.principal.Principal;
import org.archboy.clobaframe.setting.principal.PrincipalSetting;
import org.archboy.clobaframe.setting.principal.PrincipalSettingProvider;
import org.archboy.clobaframe.setting.principal.PrincipalSettingRepository;
import org.archboy.clobaframe.setting.support.Utils;

/**
 *
 * @author yang
 */
@Named
public class PrincipalSettingImpl implements PrincipalSetting {

	private List<PrincipalSettingProvider> principalSettingProviders = new ArrayList<PrincipalSettingProvider>();

	private List<PrincipalSettingRepository> principalSettingRepositorys = new ArrayList<PrincipalSettingRepository>();
	
	@Override
	public void addProfileSettingProvider(PrincipalSettingProvider profileSettingProvider) {
		principalSettingProviders.add(profileSettingProvider);
		sortProviders();
	}

	@Override
	public void addProfileSettingRepository(PrincipalSettingRepository profileSettingRepository) {
		principalSettingRepositorys.add(profileSettingRepository);
	}
	
	private void sortProviders(){
		// sort providers, from higher(smaller number) priority to lower.
		principalSettingProviders.sort(new Comparator<PrincipalSettingProvider>() {
			@Override
			public int compare(PrincipalSettingProvider o1, PrincipalSettingProvider o2) {
				return o1.getPriority() - o2.getPriority();
			}
		});
	}
	
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
		for(PrincipalSettingRepository repository : principalSettingRepositorys) {
			if (repository.support(profile)) {
				repository.set(profile, key, value);
				return;
			}
		}
	}

	@Override
	public void set(Principal profile, Map<String, Object> items) {
		for(PrincipalSettingRepository repository : principalSettingRepositorys) {
			if (repository.support(profile)) {
				repository.set(profile, items);
				return;
			}
		}
	}
	
}
