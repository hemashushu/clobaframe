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
package org.archboy.clobaframe.query.simplequery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.archboy.clobaframe.query.Query;
import org.archboy.clobaframe.query.QueryException;

/**
 *
 * @author young
 */
public class SimpleQuery<T> implements Query<T>{

	private Collection<T> collection;

	private List<Predicate> predicates = null;
	private List<Comparator<T>> comparators = null;

	public SimpleQuery(Collection<T> collection) {
		this.collection = collection;
	}

	public static <T> Query<T> from(Collection<T> collection){
		return new SimpleQuery<T>(collection);
	}

	@Override
	public Query<T> where(Predicate predicate) {
		if (predicate != null){
			if (predicates == null){
				predicates = new ArrayList<Predicate>();
			}
			predicates.add(predicate);
		}
		return this;
	}

	@Override
	public Query<T> whereEquals(String key, Object value) {
		Predicate predicate = PredicateFactory.equals(key, value);
		return where(predicate);
	}

	@Override
	public Query<T> whereNotEquals(String key, Object value) {
		Predicate predicateEquals = PredicateFactory.equals(key, value);
		Predicate predicate = PredicateFactory.not(predicateEquals);
		return where(predicate);
	}

	@Override
	public Query<T> whereGreaterThan(String key, Object value) {
		Predicate predicate = PredicateFactory.greaterThan(key, value);
		return where(predicate);
	}

	@Override
	public Query<T> whereGreaterThanOrEqual(String key, Object value) {
		Predicate predicateLessThan = PredicateFactory.lessThan(key, value);
		Predicate predicate = PredicateFactory.not(predicateLessThan);
		return where(predicate);
	}

	@Override
	public Query<T> whereLessThan(String key, Object value) {
		Predicate predicate = PredicateFactory.lessThan(key, value);
		return where(predicate);
	}

	@Override
	public Query<T> whereLessThanOrEqual(String key, Object value) {
		Predicate predicateGreaterThan = PredicateFactory.greaterThan(key, value);
		Predicate predicate = PredicateFactory.not(predicateGreaterThan);
		return where(predicate);
	}

	@Override
	public Query<T> orderBy(String key) {
		Comparator<T> comparator = ComparatorFactory.build(key, true);
		return orderBy(comparator);
	}

	@Override
	public Query<T> orderByDesc(String key) {
		Comparator<T> comparator = ComparatorFactory.build(key, false);
		return orderBy(comparator);
	}

	@Override
	public Query<T> orderBy(Comparator<T> comparator) {
		if (comparator != null){
			if (comparators == null){
				comparators = new ArrayList<Comparator<T>>();
			}
			comparators.add(comparator);
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> list() {
		Collection<T> items = collection;

		if (predicates != null){
			Predicate[] predicateArray = predicates.toArray(new Predicate[0]);
			Predicate predicate = PredicateFactory.and(predicateArray);
			items = (Collection<T>)CollectionUtils.select(items, predicate);
		}

		if (items.isEmpty() || comparators == null){
			return new ArrayList(items);
		}

		Comparator<T>[] comparatorArray = (Comparator<T>[])comparators.toArray(new Comparator[0]);
		Comparator<T> comparator = ComparatorFactory.combine(comparatorArray);

		List<T> result = new ArrayList(items);
		Collections.sort(result, comparator);

		return result;
	}

	@Override
	public T first() {
		Collection<T> items = list();
		if (items.isEmpty()){
			return null;
		}else{
			return items.iterator().next();
		}
	}

	@Override
	public List<Map<String, Object>> select(String[] keys) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Collection<T> items = list();

		try {
			for (T item : items) {
				Map<String, Object> obj = new HashMap<String, Object>();
				for (String key : keys) {
					obj.put(key, BeanUtils.getPropertyValue(item, key));
				}

				result.add(obj);
			}
		} catch (Exception ex) {
			throw new QueryException(
							String.format("Exception occur while copy properties [%s]",
								ex.getMessage())
							);
		}

		return result;
	}
}
