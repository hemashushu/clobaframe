package org.archboy.clobaframe.query;

import java.util.Comparator;
import java.util.List;
import org.apache.commons.collections.Predicate;

/**
 * An utility for selecting and sorting objects.
 *
 * @author yang
 * @param <T>
 */
public interface Query<T> {

	/**
	 * Add a predicate to query.
	 * 
	 * Multiple predicates process the "AND" logic.
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
	 * Sort the items by a specify key.
	 * 
	 * Multiple comparators process the "order by... then by... then by" logic.
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
	 * Limit the result items.
	 * 
	 * @param size
	 * @return 
	 */
	Query<T> limit(int size);
	
	/**
	 * Return the result.
	 *
	 * @return
	 */
	List<T> list();

	/**
	 * Return the first item of the result.
	 *
	 * @return Return null if there is no result.
	 */
	T first();

	/**
	 * Return the ObjectMap object with specify keys.
	 * 
	 * Example: 
	 * [
	 * {id:'123', name:'foo', checked:true},
	 * {id:..., name:..., checked:...},
	 * {id:..., name:..., checked:...},
	 * ...]
	 * 
	 * with select:
	 * ['id','checked']
	 * 
	 * will return:
	 * 
	 * [{id:'123', checked:true},...]
	 *
	 * @param keys
	 * @return
	 */
	List<ObjectMap> select(String... keys);
}
