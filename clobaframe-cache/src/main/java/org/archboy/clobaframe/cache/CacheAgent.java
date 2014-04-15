package org.archboy.clobaframe.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author arch
 */
public interface CacheAgent {

	/**
	 * Agent name.
	 *
	 * @return
	 */
	String getName();

	/**
	 * 
	 */
	void clearAll();

	/**
	 * 
	 * @param key
	 * @return 
	 */
	boolean delete(String key);

	/**
	 * 
	 * @param keys 
	 */
	void deleteAll(Collection<String> keys);

	/**
	 * 
	 * @param key
	 * @return 
	 */
	Object get(String key);

	/**
	 * 
	 * @param keys
	 * @return 
	 */
	Map<String, Object> getAll(Collection<String> keys);

	/**
	 * 
	 * @param key
	 * @param value
	 * @param expires
	 * @param policy
	 * @return 
	 */
	boolean put(String key, Object value, Expiration expires,
			Cache.SetPolicy policy);

	/**
	 * 
	 * @param values
	 * @param expires
	 * @param policy
	 * @return 
	 */
	Set<String> putAll(java.util.Map<String, ? extends Object> values,
			Expiration expires, Cache.SetPolicy policy);
}
