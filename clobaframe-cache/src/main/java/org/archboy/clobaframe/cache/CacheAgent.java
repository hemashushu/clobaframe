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

	void clearAll();

	boolean delete(String key);

	void deleteAll(Collection<String> keys);

	Object get(String key);

	Map<String, Object> getAll(Collection<String> keys);

	boolean put(String key, Object value, Expiration expires,
			Cache.SetPolicy policy);

	Set<String> putAll(java.util.Map<String, ? extends Object> values,
			Expiration expires, Cache.SetPolicy policy);
}
