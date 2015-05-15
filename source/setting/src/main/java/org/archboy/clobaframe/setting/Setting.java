package org.archboy.clobaframe.setting;

import java.util.Map;
import org.archboy.clobaframe.setting.instance.InstanceSettingRepository;

/**
 * The application settings manager.
 * 
 * @author yang
 */
public interface Setting {

	/**
	 * Get the item value and resolve the placeholder.
	 * 
	 * The key name can be used as part of other item's value, e.g:
	 * item1 = value1
	 * item2 = ${item1}-value2
	 * item3 = ${item2}-${item1}
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
	 * Get the original item value.
	 * I.e. leave the placeholder un-resolve.
	 * 
	 * @param key
	 * @return 
	 */
	Object get(String key);

	/**
	 * Get all items original value.
	 * @return 
	 */
	Map<String, Object> getAll();
	
	/**
	 * Update or add an item.
	 * 
	 * @param key
	 * @param value 
	 */
	void set(String key, Object value);
	
	/**
	 * Update or add many items.
	 * 
	 * @param items 
	 */
	void set(Map<String, Object> items);

}
