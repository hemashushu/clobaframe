package org.archboy.clobaframe.webresource;

/**
 *
 * @author yang
 */
public interface CacheableWebResource {
	
	/**
	 * 
	 * @param resourceUpdateListener 
	 */
	void addUpdateListener(CacheableWebResourceUpdateListener resourceUpdateListener);
	
	/**
	 * Force refresh by outside invoker.
	 * 
	 * E.g. a user has modified a custom style-sheet and want to take effect immediately.
	 * 
	 */
	void refresh();
}
