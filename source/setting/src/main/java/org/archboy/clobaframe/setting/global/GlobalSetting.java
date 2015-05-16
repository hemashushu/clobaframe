package org.archboy.clobaframe.setting.global;

import org.archboy.clobaframe.setting.CacheableSetting;

/**
 * Global setting manager.
 * 
 * Global setting refers to the values that used by all around the application/instance,
 * e.g. for a web application, the web site name/title, the administrator's email address,
 * the web site theme name etc..
 * 
 * Global setting layer:
 * 1. Default global setting, immutable/readonly. (bundle within application package)
 * 2. Providers.
 * 
 * 
 * @author yang
 */
public interface GlobalSetting extends CacheableSetting {
	
}
