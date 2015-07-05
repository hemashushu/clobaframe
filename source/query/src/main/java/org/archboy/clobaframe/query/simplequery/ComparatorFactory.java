package org.archboy.clobaframe.query.simplequery;

import java.util.Comparator;
import org.springframework.util.Assert;

/**
 *
 * @author yang
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
				Object value1 = Utils.getPropertyValue(o1, key);
				Object value2 = Utils.getPropertyValue(o2, key);
				int result = 0;
				if (value1 == null){
					result = (value2 == null ? 0 : -1);
				}else{
					result = (value2 == null ? 1 : Utils.compareValue(value1, value2));
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
	 * @param level level of recursive.
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
