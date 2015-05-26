package org.archboy.clobaframe.setting.application;

import java.util.Map;

/**
 * Do something after application setting load complete and before
 * other service startup.
 * 
 * such as application data/database initialization etc.
 * 
 * @author yang
 */
public interface PostApplicationSetting {
	
	
	void execute(Map<String, Object> settings) throws Exception;
	
}
