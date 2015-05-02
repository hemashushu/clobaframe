package org.archboy.clobaframe.webresource;

import org.archboy.clobaframe.io.CacheableResourceInfo;

/**
 *
 * @author yang
 */
public interface CacheableWebResourceInfo extends WebResourceInfo, CacheableResourceInfo {
	
	/**
	 * 
	 * @param resourceUpdateListener 
	 */
	void addUpdateListener(CacheableWebResourceInfoUpdateListener resourceUpdateListener);
	
}
