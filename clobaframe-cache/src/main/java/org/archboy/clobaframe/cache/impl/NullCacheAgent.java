package org.archboy.clobaframe.cache.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.archboy.clobaframe.cache.Cache.SetPolicy;
import org.archboy.clobaframe.cache.CacheAgent;
import org.archboy.clobaframe.cache.Expiration;

/**
 *
 * @author arch
 */
@Component
public class NullCacheAgent implements CacheAgent {

	private static final String AGENT_NAME = "null";

	@Override
	public String getName() {
		return AGENT_NAME;
	}

	@Override
	public void clearAll() {
		//
	}

	@Override
	public boolean delete(String key) {
		return true;
	}

	@Override
	public void deleteAll(Collection<String> keys) {
		//
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
	public boolean put(String key, Object value, Expiration expires, SetPolicy policy) {
		return true;
	}

	@Override
	public Set<String> putAll(Map<String, ? extends Object> values, Expiration expires, SetPolicy policy) {
		return new HashSet<String>();
	}
}
