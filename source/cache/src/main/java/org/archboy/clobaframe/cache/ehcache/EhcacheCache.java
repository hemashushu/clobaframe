package org.archboy.clobaframe.cache.ehcache;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.cache.Cache.Policy;
import org.archboy.clobaframe.cache.Expiration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * About the ehcache configuration file.
 * 
 * the diskStore path can use the System.properties.
 * for details, see: http://ehcache.org/apidocs/2.6.9/net/sf/ehcache/config/DiskStoreConfiguration.html
 * 
 * Legacy e.g. 
 * java.io.tmpdir/ehcache/ = /var/tmp/ehcache/.
 * 
 * user.home - the user's home directory
 * user.dir - the current working directory
 * java.io.tmpdir - the default temp file path
 * ehcache.disk.store.dir - a system property you would normally specify on the command line, e.g. java -Dehcache.disk.store.dir=/u01/myapp/diskdir
 * 
 * or
 * 
 * use System properties, e.g.
 * ${java.io.tmpdir}/clobaframe
 * 
 * @author yang
 */
@Named
public class EhcacheCache implements org.archboy.clobaframe.cache.Cache { //, 
		//ResourceLoaderAware, InitializingBean, DisposableBean {

	public static final String DEFAULT_CACHE_REGION_NAME = "common";
	public static final String DEFAULT_CACHE_CONFIGURATION_FILE = ""; //"classpath:ehcache.xml";

	public static final String SETTING_KEY_CACHE_REGION_NAME = "clobaframe.cache.ehcache.region";
	public static final String SETTING_KEY_CACHE_CONFIGURATION_FILE = "clobaframe.cache.ehcache.configuration";
	
	@Value("${" + SETTING_KEY_CACHE_REGION_NAME + ":" + DEFAULT_CACHE_REGION_NAME + "}")
	private String cacheRegionName;

	@Value("${" + SETTING_KEY_CACHE_CONFIGURATION_FILE + ":" + DEFAULT_CACHE_CONFIGURATION_FILE + "}")
	private String cacheConfigurationFile;

	@Inject
	private ResourceLoader resourceLoader;

	private CacheManager cacheManager;
	private Cache cache;

	private final Logger logger = LoggerFactory.getLogger(EhcacheCache.class);

	public void setCacheRegionName(String cacheRegionName) {
		this.cacheRegionName = cacheRegionName;
	}

	public void setCacheConfigurationFile(String cacheConfigurationFile) {
		this.cacheConfigurationFile = cacheConfigurationFile;
	}

	//@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@PostConstruct
	//@Override
	public void init() throws Exception {
		if (StringUtils.isEmpty(cacheConfigurationFile)){
			return;
		}
		
		Resource resource = resourceLoader.getResource(cacheConfigurationFile);
		if (!resource.exists()) {
			throw new FileNotFoundException(String.format(
					"Can not find the ehcache config file [%s].",
					cacheConfigurationFile));
		}
		
		InputStream in = null;
		try{
			in = resource.getInputStream();
			cacheManager = CacheManager.create(in);
			cache = cacheManager.getCache(cacheRegionName);
		}finally{
			IOUtils.closeQuietly(in);
		}
	}

	@PreDestroy
	//@Override
	public void close() throws Exception {
		if (cacheManager != null){
			cacheManager.shutdown();
		}
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
		Policy policy) {

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
	public boolean put(String key, Object value) {
		return put(key, value, null, Policy.SET_ALWAYS);
	}

	@Override
	public boolean put(String key, Object value, Expiration expiration) {
		return put(key, value, expiration, Policy.SET_ALWAYS);
	}
	
	@Override
	public Set<String> putAll(Map<String, ? extends Object> values, 
			Expiration expires, Policy policy) {

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
