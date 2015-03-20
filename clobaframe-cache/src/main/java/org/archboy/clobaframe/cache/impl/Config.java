package org.archboy.clobaframe.cache.impl;

import org.archboy.clobaframe.cache.AbstractCache;
import java.util.List;
import javax.inject.Inject;
import org.archboy.clobaframe.cache.Cache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
@Configuration
public class Config {

	private static final String DEFAULT_IMPLEMENTATION_NAME = "null";

	@Value("${clobaframe.cache.impl}")
	private String implementationName = DEFAULT_IMPLEMENTATION_NAME;

	@Inject
	private List<AbstractCache> caches;

	@Bean(name = "default")
	public Cache getCache(){
		return getCache(implementationName);
	}
	
	private Cache getCache(String name) {
		Assert.hasText(name);
		
		for (AbstractCache cache : caches){
			if (cache.getName().equals(name)) {
				return cache;
			}
		}

		throw new IllegalArgumentException(
				String.format("Can not find the specify cache implement [%s].", name));
	}
}
