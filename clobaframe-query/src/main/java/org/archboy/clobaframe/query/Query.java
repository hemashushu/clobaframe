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
package org.archboy.clobaframe.query;

import java.util.Comparator;
import java.util.List;

import java.util.Map;
import org.apache.commons.collections.Predicate;

/**
 * A simple interface for selecting or sorting objects from collection.
 * <p>
 *     Currently the SimpleQuery implements this interface.
 * </p>
 *
 * @author young
 */
public interface Query<T> {

	/**
	 * Add a predicate to query.
	 * <p>
	 *     Multiple predicates runs with 'AND' logic.
	 * </p>
	 *
	 * @param predicate
	 * @return
	 */
	Query<T> where(Predicate predicate);

	Query<T> whereEquals(String key, Object value);

	Query<T> whereNotEquals(String key, Object value);

	Query<T> whereGreaterThan(String key, Object value);

	Query<T> whereGreaterThanOrEqual(String key, Object value);

	Query<T> whereLessThan(String key, Object value);

	Query<T> whereLessThanOrEqual(String key, Object value);

	Query<T> orderBy(String key);

	Query<T> orderByDesc(String key);

	Query<T> orderBy(Comparator<T> comparator);

	/**
	 * Return a result list.
	 *
	 * @return
	 */
	List<T> list();

	/**
	 * Return the first item of the result list.
	 *
	 * @return Return null if there is no result.
	 */
	T first();

	/**
	 * Return the map object with specify keys.
	 * For example: {id:'123', name:'foo', checked:'true'} with
	 * select ['id','checked'] result the {id:'123', checked:'true'}.
	 *
	 * @param keys
	 * @return
	 */
	List<Map<String, Object>> select(String... keys);
}
