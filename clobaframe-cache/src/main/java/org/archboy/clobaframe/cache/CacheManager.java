package org.archboy.clobaframe.cache;

/**
 *
 * @author yang
 */
public interface CacheManager {
	
	/**
	 * 
	 * @return 
	 */
	Cache getDefault();
	
	/**
	 * 
	 * @param name
	 * @return 
	 */
	Cache getCache(String name);
}
