package org.archboy.clobaframe.setting;

import java.util.Map;

/**
 *
 * @author yang
 */
public interface SettingRepository {
	
	/**
	 * Merge and update several items.
	 * The implementation should NOT modify the map object.
	 * 
	 * @param item 
	 */
	void update(Map<String, Object> item);
	
	/**
	 * Merge and update an item.
	 * 
	 * @param key
	 * @param value 
	 */
	void update(String key, Object value);
	
}
