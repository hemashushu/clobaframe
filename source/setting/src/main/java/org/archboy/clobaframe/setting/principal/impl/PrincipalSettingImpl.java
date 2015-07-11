package org.archboy.clobaframe.setting.principal.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import org.archboy.clobaframe.setting.SettingProvider;
import org.archboy.clobaframe.setting.application.ApplicationSettingProvider;
import org.archboy.clobaframe.setting.principal.Principal;
import org.archboy.clobaframe.setting.principal.PrincipalSetting;
import org.archboy.clobaframe.setting.principal.PrincipalSettingProvider;
import org.archboy.clobaframe.setting.principal.PrincipalSettingRepository;
import org.archboy.clobaframe.setting.support.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

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

	public void setPrincipalSettingProviders(List<PrincipalSettingProvider> principalSettingProviders) {
		this.principalSettingProviders = principalSettingProviders;
	}

	public void setPrincipalSettingRepositorys(List<PrincipalSettingRepository> principalSettingRepositorys) {
		this.principalSettingRepositorys = principalSettingRepositorys;
	}

	@Override
	public void addProvider(PrincipalSettingProvider settingProvider) {
		
		if (principalSettingProviders == null) {
			principalSettingProviders = new ArrayList<PrincipalSettingProvider>();
		}
		
		principalSettingProviders.add(settingProvider);
		principalSettingProviders.sort(new Comparator<PrincipalSettingProvider>() {

			@Override
			public int compare(PrincipalSettingProvider o1, PrincipalSettingProvider o2) {
				return o1.getOrder() - o2.getOrder();
			}
		});
	}

	@Override
	public void removeProvider(String providerName) {
		Assert.notNull(providerName);
		for (int idx=principalSettingProviders.size() -1; idx>=0; idx--){
			PrincipalSettingProvider provider = principalSettingProviders.get(idx);
			if (providerName.equals(provider.getName())){
				principalSettingProviders.remove(idx);
				break;
			}
		}
	}
	
	@Override
	public void addRepository(PrincipalSettingRepository settingRepository) {		
		
		if (principalSettingRepositorys == null) {
			principalSettingRepositorys = new ArrayList<PrincipalSettingRepository>();
		}
		
		principalSettingRepositorys.add(settingRepository);
	}

	@Override
	public void removeRepository(String repositoryName) {
		Assert.notNull(repositoryName);
		for (int idx=principalSettingRepositorys.size() -1; idx>=0; idx--){
			PrincipalSettingRepository provider = principalSettingRepositorys.get(idx);
			if (repositoryName.equals(provider.getName())){
				principalSettingRepositorys.remove(idx);
				break;
			}
		}
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
	public Map<String, Object> list(Principal profile) {
		Map<String, Object> setting = new LinkedHashMap<String, Object>();
		for(PrincipalSettingProvider provider : principalSettingProviders) {
			if (provider.support(profile)) {
				setting = Utils.merge(setting, provider.list(profile));
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
