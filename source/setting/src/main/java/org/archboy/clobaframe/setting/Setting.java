package org.archboy.clobaframe.setting;

import java.util.Map;

/**
 *
 * @author yang
 */
public interface Setting {

	Object get(String key);
	
	Object get(String key, Object defaultValue);
	
	Map<String, Object> getAll();
	
	void set(String key, Object value);
	
	void set(Map<String, Object> items);

}