package org.archboy.clobaframe.cache.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.cache.Cache;
import org.archboy.clobaframe.cache.Expiration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
@Named
public class CacheImpl implements Cache {

	private static final String DEFAULT_ADAPTER_NAME = "null";

	@Value("${cache.agent}")
	private String defaultAdapterName = DEFAULT_ADAPTER_NAME;

	// the default cache client adapter
	private CacheClientAdapter defaultAdapter;

	private final Logger logger = LoggerFactory.getLogger(CacheImpl.class);

	@Inject
	private List<CacheClientAdapter> cacheClientAdapters;

	@PostConstruct
	public void init(){
		defaultAdapter = getCacheClientAdapter(defaultAdapterName);
		logger.info("Using [{}] cache client adapter as the default.", defaultAdapterName);
	}

	public List<CacheClientAdapter> getCacheClientAdapters() {
		return cacheClientAdapters;
	}

	public CacheClientAdapter getCacheClientAdapter(String name) {
		Assert.hasText(name);
		
		for (CacheClientAdapter adapter : cacheClientAdapters){
			if (adapter.getName().equals(name)) {
				return adapter;
			}
		}

		throw new IllegalArgumentException(
				String.format("The specify cache client adapter [%s] not found.", name));
	}

	@Override
	public void clearAll() {
		defaultAdapter.clearAll();
	}

	@Override
	public boolean delete(String key) {
		Assert.hasText(key);
		
		return defaultAdapter.delete(key);
	}

	@Override
	public void deleteAll(Collection<String> keys) {
		Assert.notNull(keys);
		
		defaultAdapter.deleteAll(keys);
	}

	@Override
	public Object get(String key) {
		Assert.hasText(key);
		
		return defaultAdapter.get(key);
	}

	@Override
	public Map<String, Object> getAll(Collection<String> keys) {
		Assert.notNull(keys);
		
		return defaultAdapter.getAll(keys);
	}

	@Override
	public boolean put(String key, Object value) {
		Assert.hasText(key);
		
		return defaultAdapter.put(key, value, null, SetPolicy.SET_ALWAYS);
	}

	@Override
	public boolean put(String key, Object value, Expiration expiration) {
		Assert.hasText(key);
		
		return defaultAdapter.put(key, value, expiration, SetPolicy.SET_ALWAYS);
	}

	@Override
	public boolean put(String key, Object value, Expiration expiration, SetPolicy policy) {
		Assert.hasText(key);
		
		return defaultAdapter.put(key, value, expiration, policy);
	}

	@Override
	public Set<String> putAll(Map<String, ? extends Object> values, Expiration expiration, SetPolicy policy) {
		Assert.notNull(values);
		
		return defaultAdapter.putAll(values, expiration, policy);
	}
}
