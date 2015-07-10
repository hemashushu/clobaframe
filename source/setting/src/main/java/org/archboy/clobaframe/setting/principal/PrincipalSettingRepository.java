package org.archboy.clobaframe.setting.principal;

import java.util.Map;

/**
 *
 * @author yang
 */
public interface PrincipalSettingRepository extends PrincipalSpecial {
	
	/**
	 * The repository name.
	 * Optional.
	 * @return 
	 */
	String getName();
	
	/**
	 * 
	 * @param principal
	 * @param item 
	 */
	void set(Principal principal, Map<String, Object> item);
	
	/**
	 * 
	 * @param principal
	 * @param key
	 * @param value 
	 */
	void set(Principal principal, String key, Object value);
}
