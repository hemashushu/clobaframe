package org.archboy.clobaframe.io;

/**
 *
 * @author yang
 */
public interface CacheableResourceInfo extends ResourceInfo {
	
	public static final int CACHE_ALWAYS = -1;
	public static final int NO_CACHE = 0;
	
	/**
	 * Refresh manually.
	 */
	void refresh();
	
}
