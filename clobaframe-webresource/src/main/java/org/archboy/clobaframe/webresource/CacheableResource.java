package org.archboy.clobaframe.webresource;

/**
 *
 * @author yang
 */
public interface CacheableResource {
	
	/**
	 * 
	 * @param resourceUpdateListener 
	 */
	void addUpdateListener(CacheableResourceUpdateListener resourceUpdateListener);
	
	/**
	 * Force refresh by outside invoker.
	 * 
	 * E.g. a user has modified a custom style-sheet and want to take effect immediately.
	 * 
	 */
	void refresh();
}
