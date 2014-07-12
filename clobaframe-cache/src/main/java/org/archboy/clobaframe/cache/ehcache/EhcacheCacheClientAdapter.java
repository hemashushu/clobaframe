package org.archboy.clobaframe.cache.ehcache;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import javax.inject.Named;
import org.archboy.clobaframe.cache.impl.CacheClientAdapter;
import org.archboy.clobaframe.cache.Expiration;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 *
 * @author yang
 */
@Named
public class EhcacheCacheClientAdapter implements CacheClientAdapter, Closeable {

	private static final String DEFAULT_CACHE_REGION_NAME = "common";
	private static final String DEFAULT_CACHE_CONFIGURATION_FILE = "ehcache.xml";

	@Value("${cache.ehcache.region}")
	private String cacheRegionName = DEFAULT_CACHE_REGION_NAME;

	@Value("${cache.ehcache.configuration}")
	private String cacheConfigurationFile = DEFAULT_CACHE_CONFIGURATION_FILE;

	@Inject
	private ResourceLoader resourceLoader;

	private CacheManager cacheManager;
	private Cache cache;

	@PostConstruct
	public void init() throws IOException{
		Resource resource = resourceLoader.getResource(cacheConfigurationFile);
		cacheManager = CacheManager.create(resource.getURL());
		cache = cacheManager.getCache(cacheRegionName);
	}

	@PreDestroy
	@Override
	public void close(){
		cacheManager.shutdown();
	}

	@Override
	public String getName() {
		return "ehcache";
	}

	@Override
	public void clearAll() {
		cache.removeAll();
	}

	@Override
	public boolean delete(String key) {
		return cache.remove(key);
	}

	@Override
	public void deleteAll(Collection<String> keys) {
		for(String key : keys){
			delete(key);
		}
	}

	@Override
	public Object get(String key) {
		Element element = cache.get(key);
		if (element == null){
			return null;
		}else{
			return element.getObjectValue();
		}
	}

	@Override
	public Map<String, Object> getAll(Collection<String> keys) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (String key : keys){
			Element element = cache.get(key);
			if (element != null){
				result.put(key, element.getObjectValue());
			}
		}
		return result;
	}

	@Override
	public boolean put(String key, Object value, Expiration expires,
		org.archboy.clobaframe.cache.Cache.SetPolicy policy) {

		int expireSecond = 0;
		if (expires != null) {
			expireSecond = expires.getSeconds();
		}

		Element element = new Element(key, value, false, expireSecond, expireSecond);
		boolean result = true;

		switch (policy) {
			case SET_ALWAYS:
				cache.put(element);
				break;

			case ADD_ONLY_IF_NOT_PRESENT:
				Element absent = cache.putIfAbsent(element);
				result = (absent == null);
				break;

			case REPLACE_ONLY_IF_PRESENT:
				Element previous = cache.replace(element);
				result = (previous != null);
				break;
		}

		return result;
	}

	@Override
	public Set<String> putAll(Map<String, ? extends Object> values, Expiration expires,
		org.archboy.clobaframe.cache.Cache.SetPolicy policy) {

		Set<String> items = new HashSet<String>();
		for (String key : values.keySet()) {
			boolean created = put(key, values.get(key), expires, policy);
			if (created) {
				items.add(key);
			}
		}
		return items;
	}
}
