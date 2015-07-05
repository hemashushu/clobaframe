package org.archboy.clobaframe.cache.impl;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.cache.Cache;
import org.archboy.clobaframe.cache.CacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
@Named
public class CacheManagerImpl implements CacheManager {

	private static final String DEFAULT_CACHE_NAME = "null";

	@Value("${clobaframe.cache.default:" + DEFAULT_CACHE_NAME + "}")
	private String defaultCacheName;

	@Inject
	private List<Cache> caches;

	public void setDefaultCacheName(String defaultCacheName) {
		this.defaultCacheName = defaultCacheName;
	}

	public void setCaches(List<Cache> caches) {
		this.caches = caches;
	}
	
	@Override
	public Cache getDefault(){
		return getCache(defaultCacheName);
	}
	
	@Override
	public Cache getCache(String name) {
		Assert.hasText(name);
		
		for (Cache cache : caches){
			if (cache.getName().equals(name)) {
				return cache;
			}
		}

		throw new IllegalArgumentException(
				String.format("Can not find the specify cache implementation [%s].", name));
	}
}
