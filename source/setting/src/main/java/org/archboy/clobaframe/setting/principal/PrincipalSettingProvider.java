package org.archboy.clobaframe.setting.principal;

import java.util.Map;

/**
 *
 * @author yang
 */
public interface PrincipalSettingProvider extends PrincipalSpecial {
	
	/**
	 * The provider priority.
	 * 
	 * The higher priority item value will be selected when
	 * many items have the same item key (/name).
	 * @return 
	 */
	int getPriority();
	
	Object get(Principal profile, String key);
	
	/**
	 * Never return null.
	 * 
	 * @param profile
	 * @return 
	 */
	Map<String, Object> getAll(Principal profile);

	
}
