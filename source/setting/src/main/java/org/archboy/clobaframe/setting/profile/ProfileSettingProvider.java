package org.archboy.clobaframe.setting.profile;

import java.util.Map;
import org.archboy.clobaframe.setting.SettingProvider;

/**
 *
 * @author yang
 */
public interface ProfileSettingProvider extends ProfileSpecial {
	
	/**
	 * The provider priority.
	 * 
	 * The higher priority item value will be selected when
	 * many items have the same item key (/name).
	 * @return 
	 */
	int getPriority();
	
	Object get(Profile profile, String key);
	
	/**
	 * Never return null.
	 * 
	 * @param profile
	 * @return 
	 */
	Map<String, Object> getAll(Profile profile);

	
}
