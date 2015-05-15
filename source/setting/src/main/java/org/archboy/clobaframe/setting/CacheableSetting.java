package org.archboy.clobaframe.setting;

import java.util.Map;

/**
 * Cache-able setting manager.
 * 
 * All item's value would be load first and store in the memory cache,
 * rather than load from provider each access.
 * 
 * @author yang
 */
public interface CacheableSetting extends Setting {

	/**
	 * Clear the cache and reload all item's value.
	 */
	void refresh();
}
