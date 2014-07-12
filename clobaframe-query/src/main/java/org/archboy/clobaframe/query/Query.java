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
 * @author yang
 * @param <T>
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

	/**
	 * 
	 * @param key
	 * @param value
	 * @return 
	 */
	Query<T> whereEquals(String key, Object value);

	/**
	 * 
	 * @param key
	 * @param value
	 * @return 
	 */
	Query<T> whereNotEquals(String key, Object value);

	/**
	 * 
	 * @param key
	 * @param value
	 * @return 
	 */
	Query<T> whereGreaterThan(String key, Object value);

	/**
	 * 
	 * @param key
	 * @param value
	 * @return 
	 */
	Query<T> whereGreaterThanOrEqual(String key, Object value);

	/**
	 * 
	 * @param key
	 * @param value
	 * @return 
	 */
	Query<T> whereLessThan(String key, Object value);

	/**
	 * 
	 * @param key
	 * @param value
	 * @return 
	 */
	Query<T> whereLessThanOrEqual(String key, Object value);

	/**
	 * 
	 * @param key
	 * @return 
	 */
	Query<T> orderBy(String key);

	/**
	 * 
	 * @param key
	 * @return 
	 */
	Query<T> orderByDesc(String key);

	/**
	 * 
	 * @param comparator
	 * @return 
	 */
	Query<T> orderBy(Comparator<T> comparator);

	/**
	 * Limit the result list items.
	 * @param size
	 * @return 
	 */
	Query<T> limit(int size);
	
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
