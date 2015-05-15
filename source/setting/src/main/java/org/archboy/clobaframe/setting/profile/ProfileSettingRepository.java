package org.archboy.clobaframe.setting.profile;

import java.util.Map;

/**
 *
 * @author yang
 */
public interface ProfileSettingRepository extends ProfileSpecial {
	
	void set(Profile profile, Map<String, Object> item);
	
	void set(Profile profile, String key, Object value);
}
