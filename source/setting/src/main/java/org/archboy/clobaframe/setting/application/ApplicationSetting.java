package org.archboy.clobaframe.setting.application;

import java.util.Collection;
import java.util.Properties;
import org.archboy.clobaframe.setting.CacheableSetting;

/**
 * Application setting handle all values that needing for boot a application.
 * 
 * Such as database connection, underlay services configuration, base components configuration,
 * framework configuration etc..
 * 
 * Application settings layer:
 * 1. The lowest priority, the application default settings, immutable/readonly. (bundle within application jar package)
 * 2. System environment variables.
 * 3. Java system properties.
 * 4. Other settings within application jar package.
 * 5. custom settings, mutable. (persists in application data folder, JSON format)
 * 6. extra custom settings, immutable/readonly. (persist in application data folder, JSON format)
 * 
 * About config folder and application data folder.
 * 
 * System wide config folder:
 * linux and osx: /etc/APP_NAME
 * windows: %SystemDrive%\ProgramData\APP_NAME
 * 
 * User local config folder:
 * linux: ~/.local/share/APP_NAME or ~/.APP_NAME
 * osx: ~/Library/Application Support/APP_NAME
 * osx sandbox: ~/Library/Containers/APP_BUNDLE_ID/Data/Library/Application Support/APP_NAME
 * windows: ~\Application Data\Local|Roaming\APP_NAME
 *
 * System wide application data folder:
 * linux: /var/lib/APP_NAME
 * windows: %SystemDrive%\ProgramData\APP_NAME
 * 
 * User local application data folder:
 * Same as user local application config folder.
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
	 * Root properties.
	 * 
	 * @param properties 
	 */
	void setProperties(Properties properties);
	
	/**
	 * Set the root properties by the in-package properties file.
	 * @param rootConfigFileName 
	 */
	void setRootConfigFileName(String rootConfigFileName);
	
	/**
	 * Other in-jar-package setting resources.
	 * 
	 * @param locations 
	 */
	void setLocations(String... locations);
	
	/**
	 * 
	 * @param postApplicationSettings 
	 */
	void setPostApplicationSettings(
			Collection<PostInitialedHandler> postApplicationSettings);
	
}
