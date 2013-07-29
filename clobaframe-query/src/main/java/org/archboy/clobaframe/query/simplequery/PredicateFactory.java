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
import org.archboy.clobaframe.query.QueryException;

/**
 *
 * @author young
 */
public class PredicateFactory {

	public static Predicate equals(final String key, final Object value){
		Predicate predicate = new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				try{
					Object actualValue = BeanUtils.getPropertyValue(object, key);
					if (value == null){
						return (actualValue == null);
					}else{
						return value.equals(actualValue);
					}
				} catch (Exception ex) {
					throw new QueryException(
							String.format("Exception occur while getting the value of property [%s] " +
								"from object [%s], message: %s",
								key, object.getClass().getName(), ex.getMessage())
							);
				}
			}
		};
		return predicate;
	}

	public static Predicate greaterThan(final String key, final Object value){
		Predicate predicate = new Predicate() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean evaluate(Object object) {
				try{
					Object actualValue = BeanUtils.getPropertyValue(object, key);
					if (value == null && actualValue == null){
						return false;
					}else if (value == null){
						return true;
					}else if (actualValue == null) {
						return false;
					}else{
						int result = ((Comparable<Object>)actualValue).compareTo(value);
						return result > 0;
					}
				} catch (Exception ex) {
					throw new QueryException(
							String.format("Exception occur while getting the value of property [%s] " +
								"from object [%s], or the value is not comparable, message: %s",
								key, object.getClass().getName(), ex.getMessage())
							);
				}
			}
		};
		return predicate;
	}

	public static Predicate lessThan(final String key, final Object value){
		Predicate predicate = new Predicate() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean evaluate(Object object) {
				try{
					Object actualValue = BeanUtils.getPropertyValue(object, key);
					if (value == null&& actualValue == null){
						return false;
					}else if (value == null){
						return false;
					}else if (actualValue == null) {
						return true;
					}else{
						int result = ((Comparable<Object>)actualValue).compareTo(value);
						return result < 0;
					}
				} catch (Exception ex) {
					throw new QueryException(
							String.format("Exception occur while getting the value of property [%s] " +
								"from object [%s], or the value is not comparable, message: %s",
								key, object.getClass().getName(), ex.getMessage())
							);
				}
			}
		};
		return predicate;
	}

	public static Predicate not(Predicate predicate){
		return PredicateUtils.notPredicate(predicate);
	}

	public static Predicate and(Predicate... predicates){
		return PredicateUtils.allPredicate(predicates);
	}

	public static Predicate or(Predicate... predicates){
		return PredicateUtils.anyPredicate(predicates);
	}
}
