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

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.springframework.util.Assert;

/**
 *
 * @author young
 */
public class PredicateFactory {

	/**
	 * 
	 * @param key
	 * @param value
	 * @return 
	 */
	public static Predicate equals(final String key, final Object value){
		Assert.hasText(key);
		
		Predicate predicate = new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				Object actualValue = QuerySupport.getPropertyValue(object, key);
				if (value == null){
					return (actualValue == null);
				}else{
					return value.equals(actualValue);
				}
			}
		};
		return predicate;
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return 
	 */
	public static Predicate greaterThan(final String key, final Object value){
		Assert.hasText(key);
		
		Predicate predicate = new Predicate() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean evaluate(Object object) {
				Object actualValue = QuerySupport.getPropertyValue(object, key);
				if (value == null && actualValue == null){
					return false;
				}else if (value == null){
					return true;
				}else if (actualValue == null) {
					return false;
				}else{
					int result = QuerySupport.compareValue(actualValue, value); //((Comparable<Object>)actualValue).compareTo(value);
					return result > 0;
				}
			}
		};
		return predicate;
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return 
	 */
	public static Predicate lessThan(final String key, final Object value){
		Assert.hasText(key);
		
		Predicate predicate = new Predicate() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean evaluate(Object object) {
				Object actualValue = QuerySupport.getPropertyValue(object, key);
				if (value == null&& actualValue == null){
					return false;
				}else if (value == null){
					return false;
				}else if (actualValue == null) {
					return true;
				}else{
					int result = QuerySupport.compareValue(actualValue, value); //((Comparable<Object>)actualValue).compareTo(value);
					return result < 0;
				}
			}
		};
		return predicate;
	}

	/**
	 * 
	 * @param predicate
	 * @return 
	 */
	public static Predicate not(Predicate predicate){
		Assert.notNull(predicate);
		
		return PredicateUtils.notPredicate(predicate);
	}

	/**
	 * 
	 * @param predicates
	 * @return 
	 */
	public static Predicate and(Predicate... predicates){
		Assert.notNull(predicates);
		
		return PredicateUtils.allPredicate(predicates);
	}

	/**
	 * 
	 * @param predicates
	 * @return 
	 */
	public static Predicate or(Predicate... predicates){
		Assert.notNull(predicates);
		
		return PredicateUtils.anyPredicate(predicates);
	}
}
