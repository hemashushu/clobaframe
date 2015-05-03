package org.archboy.clobaframe.setting;

import java.util.Map;

/**
 *
 * @author yang
 */
public interface Setting {

	Object get(String key);
	
	Object get(String key, Object defaultValue);
	
	void set(String key, Object value);
	
	void set(Map<String, Object> items);

}
