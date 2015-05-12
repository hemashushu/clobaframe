package org.archboy.clobaframe.setting.profile;

import java.util.Map;
import org.archboy.clobaframe.setting.SettingProvider;

/**
 *
 * @author yang
 */
public interface ProfileSettingProvider {
	
	boolean support(Profile profile);
	
	Object get(Profile profile, String key);
	
	Map<String, Object> getAll(Profile profile);

}
