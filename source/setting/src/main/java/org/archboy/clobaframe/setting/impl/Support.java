package org.archboy.clobaframe.setting.impl;

import java.util.Map;

/**
 *
 * @author yang
 */
public class Support {
	
	/**
	 * Merge the new or none-null value into target map.
	 * @param target
	 * @param source
	 */
	public static void merge(Map<String, Object> target, Map<String, Object> source) {
		for(Map.Entry<String, Object> entry : source.entrySet()) {
			if (entry.getValue() == null) {
				continue;
			}
			
			target.put(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * Merge the new or none-null value into target map.
	 * @param target
	 * @param key
	 * @param value 
	 */
	public static void merge(Map<String, Object> target, String key, Object value) {
		if (value == null) {
			return;
		}

		target.put(key, value);
	}
}
