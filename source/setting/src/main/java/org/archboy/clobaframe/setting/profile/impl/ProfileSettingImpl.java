package org.archboy.clobaframe.setting.profile.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import org.archboy.clobaframe.setting.profile.Profile;
import org.archboy.clobaframe.setting.profile.ProfileSetting;
import org.archboy.clobaframe.setting.profile.ProfileSettingProvider;
import org.archboy.clobaframe.setting.profile.ProfileSettingRepository;
import org.archboy.clobaframe.setting.support.Utils;

/**
 *
 * @author yang
 */
@Named
public class ProfileSettingImpl implements ProfileSetting {

	private List<ProfileSettingProvider> profileSettingProviders = new ArrayList<ProfileSettingProvider>();

	private List<ProfileSettingRepository> profileSettingRepositorys = new ArrayList<ProfileSettingRepository>();
	
	@Override
	public void addProfileSettingProvider(ProfileSettingProvider profileSettingProvider) {
		profileSettingProviders.add(profileSettingProvider);
		sortProviders();
	}

	@Override
	public void addProfileSettingRepository(ProfileSettingRepository profileSettingRepository) {
		profileSettingRepositorys.add(profileSettingRepository);
	}
	
	private void sortProviders(){
		// sort providers, from higher(smaller number) priority to lower.
		profileSettingProviders.sort(new Comparator<ProfileSettingProvider>() {
			@Override
			public int compare(ProfileSettingProvider o1, ProfileSettingProvider o2) {
				return o1.getPriority() - o2.getPriority();
			}
		});
	}
	
	@Override
	public Object get(Profile profile, String key) {
		for(ProfileSettingProvider provider : profileSettingProviders) {
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
	public Map<String, Object> getAll(Profile profile) {
		Map<String, Object> setting = new LinkedHashMap<String, Object>();
		for(ProfileSettingProvider provider : profileSettingProviders) {
			if (provider.support(profile)) {
				setting = Utils.merge(setting, provider.getAll(profile));
			}
		}
		return setting;
	}

	@Override
	public void set(Profile profile, String key, Object value) {
		for(ProfileSettingRepository repository : profileSettingRepositorys) {
			if (repository.support(profile)) {
				repository.set(profile, key, value);
				return;
			}
		}
	}

	@Override
	public void set(Profile profile, Map<String, Object> items) {
		for(ProfileSettingRepository repository : profileSettingRepositorys) {
			if (repository.support(profile)) {
				repository.set(profile, items);
				return;
			}
		}
	}
	
}
