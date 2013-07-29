/*
 * Copyright 2011 Spark Young (sparkyoungs@gmail.com). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.archboy.clobaframe.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A simple interface for accessing memory cache.
 *
 * @author young
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

//	/**
//	 * Atom increment,
//	 *
//	 * @param key
//	 * @param by
//	 * @return Return -1 if the key does not exists.
//	 */
//	long increment(String key, int by);
//
//	/**
//	 * Atom increment.
//	 *
//	 * @param key
//	 * @param by
//	 * @param defaultValue If the specify key does not exists, this value
//	 * will be return.
//	 * @return
//	 */
//	long increment(String key, int by, long defaultValue);

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
	 * @param expires Pass null equals no expired.
	 * @param policy
	 * @return whether or not the operation was performed
	 */
	boolean put(String key, Object value, Expiration expires,
			Cache.SetPolicy policy);

	/**
	 * Bulk put.
	 *
	 * @param values
	 * @param expires
	 * @param policy
	 * @return
	 */
	Set<String> putAll(java.util.Map<String, ? extends Object> values,
			Expiration expires, Cache.SetPolicy policy);

	public static enum SetPolicy {
		ADD_ONLY_IF_NOT_PRESENT,
		REPLACE_ONLY_IF_PRESENT,
		SET_ALWAYS
	}
}
