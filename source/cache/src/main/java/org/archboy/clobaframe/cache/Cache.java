package org.archboy.clobaframe.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * The cache abstracting.
 * 
 * @author yang
 */
public interface Cache {

	public static enum Policy {
		ADD_ONLY_IF_NOT_PRESENT,
		REPLACE_ONLY_IF_PRESENT,
		SET_ALWAYS
	}
	
	/**
	 * The cache implementation name.
	 * @return 
	 */
	public abstract String getName();
	
	/**
	 * Clean all cache items.
	 */
	void clearAll();

	/**
	 * Delete a cache item.
	 *
	 * @param key
	 * @return TRUE if the operation was performed.
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
	 * @return NULL if the specify item does not exists.
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
	 * Put a cache item with no expired and Policy.SET_ALWAYS.
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	boolean put(String key, Object value);

	/**
	 * Put a cache item with Policy.SET_ALWAYS.
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
	 * @return TRUE if the operation was performed
	 */
	boolean put(String key, Object value,
			Expiration expiration, Policy policy);

	/**
	 * Bulk put.
	 *
	 * @param values
	 * @param expiration
	 * @param policy
	 * @return
	 */
	Set<String> putAll(java.util.Map<String, ? extends Object> values,
			Expiration expiration, Policy policy);

}
