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

import java.util.Comparator;
import org.springframework.util.Assert;

/**
 *
 * @author young
 */
public class ComparatorFactory {

	/**
	 * 
	 * @param <T>
	 * @param key
	 * @param ascOrder
	 * @return 
	 */
	public static <T> Comparator<T> build(final String key, final boolean ascOrder){
		Assert.hasText(key);
		
		Comparator<T> comparator = new Comparator<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public int compare(T o1, T o2) {
				Object value1 = QuerySupport.getPropertyValue(o1, key);
				Object value2 = QuerySupport.getPropertyValue(o2, key);
				int result = 0;
				if (value1 == null){
					result = (value2 == null ? 0 : -1);
				}else{
					result = (value2 == null ? 1 : QuerySupport.compareValue(value1, value2));
				}
				return (ascOrder ? result : -result);
			}
		};
		return comparator;
	}

	/**
	 * 
	 * @param <T>
	 * @param comparators
	 * @return 
	 */
	public static <T> Comparator<T> combine(
			final Comparator<T>[] comparators){
		return combine(comparators, 0);
	}

	/**
	 * 
	 * @param <T>
	 * @param comparators
	 * @param level to mark the steps of the self-call.
	 * @return 
	 */
	private static <T> Comparator<T> combine(
			final Comparator<T>[] comparators, final int level){

		Comparator<T> comparator = new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				int result = comparators[level].compare(o1, o2);
				if (result == 0){
					if (level < comparators.length -1){
						Comparator<T> subComparator = combine(comparators, level + 1);
						return subComparator.compare(o1, o2);
					}else{
						return 0;
					}
				}else{
					return result;
				}
			}
		};
		return comparator;
	}
}
