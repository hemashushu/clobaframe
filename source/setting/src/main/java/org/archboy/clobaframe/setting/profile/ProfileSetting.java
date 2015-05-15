package org.archboy.clobaframe.setting.profile;

import java.util.Map;

/**
 * The profile/principal special setting manager.
 * 
 * @author yang
 */
public interface ProfileSetting {

	Object get(Profile profile, String key);
	
//	Object getValue(Profile profile, String key);
//	
//	Object getValue(Profile profile, String key, Object defaultValue);
	
	Map<String, Object> getAll(Profile profile);
	
	void set(Profile profile, String key, Object value);
	
	void set(Profile profile, Map<String, Object> items);
	
	void addProfileSettingProvider(ProfileSettingProvider profileSettingProvider);
	
	void addProfileSettingRepository(ProfileSettingRepository profileSettingRepository);
}
