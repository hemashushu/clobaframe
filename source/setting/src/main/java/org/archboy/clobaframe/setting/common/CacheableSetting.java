package org.archboy.clobaframe.setting.common;

/**
 * Cache-able setting manager.
 * 
 * All item's value would be load from providers once and store in the memory (as cache).
 * 
 * @author yang
 */
public interface CacheableSetting extends Setting {

	/**
	 * Clear the cache and reload all item's value from providers.
	 * 
	 * Invoke this method only when the setting source was changed by the
	 * external tools, for example, the read-only (to application) properties file
	 * was modified by user manually.
	 * 
	 * The method {@link Setting#set()} will auto refresh the setting values, so
	 * it's NOT necessary invoke this method when update settings value.
	 */
	void refresh();
}
