package org.archboy.clobaframe.setting;

import java.util.Map;

/**
 *
 * @author yang
 */
public interface Setting {

	/**
	 * Get the item value and resolve the placeholder.
	 * 
	 * @param key
	 * @return 
	 */
	Object getValue(String key);
	
	/**
	 * Get the item value and resolve the placeholder.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return 
	 */
	Object getValue(String key, Object defaultValue);
	
	/**
	 * 
	 * @param key
	 * @return 
	 */
	Object get(String key);
	
	Map<String, Object> getAll();
	
	void set(String key, Object value);
	
	void set(Map<String, Object> items);

	void refresh();
}
