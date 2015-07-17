package org.archboy.clobaframe.resource;

import org.archboy.clobaframe.io.CacheableResourceInfo;

/**
 *
 * @author yang
 */
public interface NotificationCacheableResourceInfo extends CacheableResourceInfo {
	
	/**
	 * 
	 * @param resourceUpdateListener 
	 */
	void addUpdateListener(ResourceUpdateListener resourceUpdateListener);
	
}
