package org.archboy.clobaframe.setting;

import java.util.Map;

/**
 *
 * @author yang
 */
public interface SettingRepository {
	
	/**
	 * merge and update.
	 * The implementation should NOT modify the "item" object.
	 * @param item 
	 */
	void update(Map<String, Object> item);
	
	void update(String key, Object value);
	
}
