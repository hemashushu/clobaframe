package org.archboy.clobaframe.setting.application;

import java.util.Collection;
import org.archboy.clobaframe.setting.CacheableSetting;
import org.springframework.core.io.Resource;

/**
 * Application setting handle all values that need to boot the application.
 * 
 * Such as database connection, underlay services configurate, base components configurate,
 * framework configurate etc..
 * 
 * Application layers:
 * 1. Default settings, immutable/readonly. (bundle within application jar package)
 * 2. System environment variables.
 * 3. Java system properties.
 * 4. Other within application jar package settings.
 * 5. custom settings, mutable. (persist in application data folder, JSON format)
 * 6. extra custom settings, immutable/readonly. (persist in application data folder, JSON format)
 * 
 * @author yang
 */
public interface ApplicationSetting extends CacheableSetting {
	
	/**
	 * 
	 * @param name 
	 */
	void setApplicationName(String name);
	
	/**
	 * 
	 * @return 
	 */
	String getApplicationName();
	
	/**
	 * Other in-jar-package setting resources.
	 * 
	 * @param locations 
	 */
	void setLocations(Resource... locations);
	
	/**
	 * 
	 * @param postApplicationSettings 
	 */
	void setPostApplicationSettings(
			Collection<PostApplicationSetting> postApplicationSettings);
	
}
