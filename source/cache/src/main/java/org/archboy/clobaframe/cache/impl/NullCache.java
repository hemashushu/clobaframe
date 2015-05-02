package org.archboy.clobaframe.cache.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Named;
import org.archboy.clobaframe.cache.Cache;
import org.archboy.clobaframe.cache.Cache.Policy;
import org.archboy.clobaframe.cache.Expiration;

/**
 *
 * @author yang
 */
@Named
public class NullCache implements Cache {

	@Override
	public String getName() {
		return "null";
	}

	@Override
	public void clearAll() {
	}

	@Override
	public boolean delete(String key) {
		return true;
	}

	@Override
	public void deleteAll(Collection<String> keys) {
	}

	@Override
	public Object get(String key) {
		return null;
	}

	@Override
	public Map<String, Object> getAll(Collection<String> keys) {
		return new HashMap<String, Object>();
	}

	@Override
	public boolean put(String key, Object value, Expiration expires, Policy policy) {
		return true;
	}

	@Override
	public Set<String> putAll(Map<String, ? extends Object> values, Expiration expires, Policy policy) {
		return new HashSet<String>();
	}

	@Override
	public boolean put(String key, Object value) {
		return true;
	}

	@Override
	public boolean put(String key, Object value, Expiration expiration) {
		return true;
	}
}
