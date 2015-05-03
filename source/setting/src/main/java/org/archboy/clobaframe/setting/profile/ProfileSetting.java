package org.archboy.clobaframe.setting.profile;

import java.util.Map;
import org.archboy.clobaframe.setting.*;

/**
 *
 * @author yang
 */
public interface ProfileSetting {

	Object get(Profile profile, String key);
	
	Object get(Profile profile, String key, Object defaultValue);
	
	void set(Profile profile, String key, Object value);
	
	void set(Profile profile, Map<String, Object> items);

}
