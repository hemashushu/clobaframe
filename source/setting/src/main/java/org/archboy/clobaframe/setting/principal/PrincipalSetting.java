package org.archboy.clobaframe.setting.principal;

import java.util.Map;

/**
 * The principal special setting manager.
 * 
 * 
 * @author yang
 */
public interface PrincipalSetting {

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
