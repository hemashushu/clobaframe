package org.archboy.clobaframe.setting.support;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Ordered properties.
 * For Keeping the items order when save the new values.
 * 
 * @author yang
 */
public class OrderedProperties extends Properties {

	private static final long serialVersionUID = 2L;

	private final Set<Object> keys = new LinkedHashSet<Object>();

	@Override
	public Enumeration<Object> propertyNames() {
		return keys();
	}

	@Override
	public Set<String> stringPropertyNames() {
		Set<String> names = new LinkedHashSet<String>(); 
		for(Object key : keys){
			names.add((String)key);
		}
		return names;
	}

	@Override
	public Enumeration<Object> keys() {
		return Collections.enumeration(keys);
	}

	@Override
	public synchronized Object put(Object key, Object value) {
		keys.add(key);
		return super.put(key, value);
	}

	@Override
	public synchronized Object remove(Object key) {
		keys.remove(key);
		return super.remove(key);
	}

	@Override
	public synchronized void putAll(Map<?, ?> values) {
		for (Object key : values.keySet()) {
			keys.add(key);
		}
		super.putAll(values);
	}

	@Override
	public Set<Map.Entry<Object, Object>> entrySet() {
		Set<Map.Entry<Object, Object>> entries = new LinkedHashSet<Map.Entry<Object, Object>>();
		for(Object key : keys){
			entries.add(new AbstractMap.SimpleEntry<Object, Object>(key, get(key)));
		}
		return entries;
	}
	
	@Override
	public synchronized void clear() {
		super.clear();
		keys.clear();
	}
}
