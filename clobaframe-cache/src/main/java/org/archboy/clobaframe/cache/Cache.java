package org.archboy.clobaframe.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A simple interface for accessing memory cache.
 *
 * @author yang
 *
 */
public interface Cache {

	/**
	 * Clean all cache items.
	 */
	void clearAll();

	/**
	 * Delete a cache item.
	 *
	 * @param key
	 * @return whether or not the operation was performed.
	 */
	boolean delete(String key);

	/**
	 * Bulk delete.
	 *
	 * @param keys
	 */
	void deleteAll(Collection<String> keys);

	/**
	 * Get a cache item.
	 *
	 * @param key
	 * @return Return null if the specify item does not exists.
	 */
	Object get(String key);

	/**
	 * Bulk get.
	 *
	 * @param keys
	 * @return
	 */
	Map<String, Object> getAll(Collection<String> keys);

	/**
	 * Put a cache item with no expired and SetPolicy.SET_ALWAYS.
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	boolean put(String key, Object value);

	/**
	 * Put a cache item with SetPolicy.SET_ALWAYS.
	 *
	 * @param key
	 * @param value
	 * @param expiration
	 * @return 
	 */
	boolean put(String key, Object value, Expiration expiration);

	/**
	 * Put a cache item.
	 *
	 * @param key
	 * @param value
	 * @param expiration Pass null equals no expired.
	 * @param policy
	 * @return whether or not the operation was performed
	 */
	boolean put(String key, Object value, Expiration expiration,
			Cache.SetPolicy policy);

	/**
	 * Bulk put.
	 *
	 * @param values
	 * @param expiration
	 * @param policy
	 * @return
	 */
	Set<String> putAll(java.util.Map<String, ? extends Object> values,
			Expiration expiration, Cache.SetPolicy policy);

	public static enum SetPolicy {
		ADD_ONLY_IF_NOT_PRESENT,
		REPLACE_ONLY_IF_PRESENT,
		SET_ALWAYS
	}
}
