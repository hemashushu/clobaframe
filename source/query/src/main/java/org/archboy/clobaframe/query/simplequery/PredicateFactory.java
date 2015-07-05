package org.archboy.clobaframe.query.simplequery;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.springframework.util.Assert;

/**
 *
 * @author yang
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
				Object actualValue = Utils.getPropertyValue(object, key);
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
				Object actualValue = Utils.getPropertyValue(object, key);
				if (value == null && actualValue == null){
					return false;
				}else if (value == null){
					return true;
				}else if (actualValue == null) {
					return false;
				}else{
					int result = Utils.compareValue(actualValue, value);
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
				Object actualValue = Utils.getPropertyValue(object, key);
				if (value == null&& actualValue == null){
					return false;
				}else if (value == null){
					return false;
				}else if (actualValue == null) {
					return true;
				}else{
					int result = Utils.compareValue(actualValue, value);
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
