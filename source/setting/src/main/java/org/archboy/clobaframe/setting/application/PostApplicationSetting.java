package org.archboy.clobaframe.setting.application;

/**
 * Do something after application setting load complete and before
 * other service startup.
 * 
 * such as something initialization.
 * 
 * @author yang
 */
public interface PostApplicationSetting {
	
	
	void execute(ApplicationSetting applicationSetting);
	
}
