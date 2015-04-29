package org.archboy.clobaframe.query.simplequery;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import org.apache.commons.beanutils.PropertyUtils;
import org.archboy.clobaframe.query.QueryException;

/**
 *
 * @author yang
 */
class Support {

	/**
	 * Get the value of the specify object property.
	 * 
	 * @param object Should not NULL.
	 * @param key Should not EMPTY.
	 * @return
	 */
	public static Object getPropertyValue(Object object, String key) {
		if (object instanceof Map) {
			return ((Map) object).get(key);
		} else {
			try {
				return PropertyUtils.getSimpleProperty(object, key);
			} catch (IllegalAccessException ex) {
				throw new QueryException(
						String.format(
								"Can not get the value of property [%s] " +
								"from object [%s].", key, object.getClass().getName()),
						ex);
			} catch (InvocationTargetException ex) {
				throw new QueryException(
						String.format(
								"Can not get the value of property [%s] " + 
								"from object [%s].", key, object.getClass().getName()),
						ex);
			} catch (NoSuchMethodException ex) {
				throw new QueryException(
						String.format(
								"Can not get the value of property [%s] " + 
								"from object [%s].", key, object.getClass().getName()),
						ex);
			}
		}
	}

	/**
	 * Compare two objects (should be comparable).
	 * 
	 * @param value1
	 * @param value2
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public static int compareValue(Object value1, Object value2) {
		try {
			return ((Comparable<Object>) value1).compareTo(value2);
		} catch (ClassCastException ex) {
			throw new QueryException(
					String.format("Can not compare object [%s].",
							value1.getClass().getName()),
					ex);
		}
	}
}
