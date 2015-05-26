package org.archboy.clobaframe.setting;

/**
 * Cacheable setting manager.
 * 
 * All item's value would be load once and store in the memory (as cache).
 * 
 * @author yang
 */
public interface CacheableSetting extends Setting {

	/**
	 * Clear the cache and reload all item's value.
	 */
	void refresh();
}
