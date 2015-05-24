package org.archboy.clobaframe.setting.principal;

import java.util.Map;

/**
 * The principal special setting manager.
 * 
 * Principal refers to who has difference settings to the others, e.g. in a 
 * web application, the user and the group are typical principals.
 * 
 * @author yang
 */
public interface PrincipalSetting {

	/**
	 * 
	 * @param profile
	 * @param key
	 * @return 
	 */
	Object get(Principal profile, String key);

	/**
	 * 
	 * @param profile
	 * @return 
	 */
	Map<String, Object> getAll(Principal profile);
	
	/**
	 * 
	 * @param profile
	 * @param key
	 * @param value 
	 */
	void set(Principal profile, String key, Object value);
	
	/**
	 * 
	 * @param profile
	 * @param items 
	 */
	void set(Principal profile, Map<String, Object> items);
	
//	/**
//	 * Late binding.
//	 * 
//	 * @param profileSettingProvider 
//	 */
//	void addProfileSettingProvider(PrincipalSettingProvider profileSettingProvider);
//	
//	/**
//	 * Late binding.
//	 * 
//	 * @param profileSettingRepository 
//	 */
//	void addProfileSettingRepository(PrincipalSettingRepository profileSettingRepository);
}
