package org.archboy.clobaframe.io;

/**
 *
 * @author yang
 */
public interface CacheableResourceInfoWrapper {
	
	/**
	 * 
	 * @param resourceInfo
	 * @param cacheSeconds
	 * @return 
	 */
	CacheableResourceInfo wrap(ResourceInfo resourceInfo, int cacheSeconds);
}
