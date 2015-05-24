package org.archboy.clobaframe.setting.application;

import java.util.Collection;
import java.util.List;
import org.archboy.clobaframe.setting.CacheableSetting;

/**
 * Application setting handle all values that need to boot the application.
 * 
 * Such as database connection, underlay services configurate, base components configurate,
 * framework configurate etc..
 * 
 * Application layers:
 * 1. Default values, immutable/readonly. (bundle within application package)
 * 2. System environment variables.
 * 3. Java system properties.
 * 4. custom values, mutable. (persist in application data folder)
 * 5. extra custom values, immutable/readonly. (persist in application data folder)
 * 
 * @author yang
 */
public interface ApplicationSetting extends CacheableSetting {
	
	void setPostApplicationSettings(
			Collection<PostApplicationSetting> postApplicationSettings);
	
}
