package org.archboy.clobaframe.setting.global;

import org.archboy.clobaframe.setting.CacheableSetting;

/**
 * Global setting manager.
 * 
 * Global setting is the values that used by all around the application/instance,
 * e.g. within a web application, the web site name/title, the administrator's email address,
 * the web site theme name etc. are global settings.
 * 
 * Commonly global setting can be modified by user and persists in database or file.
 * 
 * Global settings layer:
 * 1. Default global setting, immutable/readonly. (bundle within application package)
 * 2. Other global settings.
 * 
 * 
 * @author yang
 */
public interface GlobalSetting extends CacheableSetting {
	
}
