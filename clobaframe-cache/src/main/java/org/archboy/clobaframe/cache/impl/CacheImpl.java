package org.archboy.clobaframe.cache.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.archboy.clobaframe.cache.Cache;
import org.archboy.clobaframe.cache.CacheAgent;
import org.archboy.clobaframe.cache.CacheAgentFactory;
import org.archboy.clobaframe.cache.Expiration;

/**
 *
 * @author arch
 */
@Component
public class CacheImpl implements Cache {

	@Autowired
	private CacheAgentFactory cacheAgentFactory;

	private CacheAgent cacheAgent;

	@PostConstruct
	public void init() {
		cacheAgent = cacheAgentFactory.getCacheAgent();
	}

	@Override
	public void clearAll() {
		cacheAgent.clearAll();
	}

	@Override
	public boolean delete(String key) {
		return cacheAgent.delete(key);
	}

	@Override
	public void deleteAll(Collection<String> keys) {
		cacheAgent.deleteAll(keys);
	}

	@Override
	public Object get(String key) {
		return cacheAgent.get(key);
	}

	@Override
	public Map<String, Object> getAll(Collection<String> keys) {
		return cacheAgent.getAll(keys);
	}

	@Override
	public boolean put(String key, Object value) {
		return cacheAgent.put(key, value, null, SetPolicy.SET_ALWAYS);
	}

	@Override
	public boolean put(String key, Object value, Expiration expiration) {
		return cacheAgent.put(key, value, expiration, SetPolicy.SET_ALWAYS);
	}

	@Override
	public boolean put(String key, Object value, Expiration expires, SetPolicy policy) {
		return cacheAgent.put(key, value, expires, policy);
	}

	@Override
	public Set<String> putAll(Map<String, ? extends Object> values, Expiration expires, SetPolicy policy) {
		return cacheAgent.putAll(values, expires, policy);
	}
}
