package org.archboy.clobaframe.io;

/**
 *
 * @author yang
 */
public interface SettingManager {

	Object getSystem(String key);
	
	Object getSystem(String key, Object defaultValue);
	
	Object getGlobal(String key);
	
	Object getGlobal(String key, Object defaultValue);
	
	Object getProfile(String key, Settings profileSetting);
	
	Object getProfile(String key, Settings profileSetting, Object defaultValue);
}
