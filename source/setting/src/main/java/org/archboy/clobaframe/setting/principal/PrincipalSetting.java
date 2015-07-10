package org.archboy.clobaframe.setting.principal;

import java.util.Map;
import org.archboy.clobaframe.setting.common.SettingProvider;

/**
 * The principal special setting manager.
 * 
 * Principal setting has no cache, i.e. all setting value will fetch from all providers
 * each call.
 * 
 * Principal Setting value does NOT support placeholder.
 * 
 * @author yang
 */
public interface PrincipalSetting {

	void addProvider(PrincipalSettingProvider settingProvider);
	
	void removeProvider(String providerName);
	
	void addRepository(PrincipalSettingRepository settingRepository);
	
	void removeRepository(String repositoryName);
	
	/**
	 * 
	 * @param principal
	 * @param key
	 * @return 
	 */
	Object get(Principal principal, String key);

	/**
	 * 
	 * @param principal
	 * @return 
	 */
	Map<String, Object> list(Principal principal);
	
	/**
	 * 
	 * @param principal
	 * @param key
	 * @param value 
	 */
	void set(Principal principal, String key, Object value);
	
	/**
	 * 
	 * @param principal
	 * @param items 
	 */
	void set(Principal principal, Map<String, Object> items);
	
}
