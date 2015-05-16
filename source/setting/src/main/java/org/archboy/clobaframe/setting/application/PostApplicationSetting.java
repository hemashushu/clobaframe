package org.archboy.clobaframe.setting.application;

/**
 * Do something after application setting load complete and before
 * other service startup.
 * 
 * such as applicate data initialize working etc.
 * 
 * @author yang
 */
public interface PostApplicationSetting {
	
	
	void execute(ApplicationSetting applicationSetting);
	
}
