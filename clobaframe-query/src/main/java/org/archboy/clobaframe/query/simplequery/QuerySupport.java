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

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import org.apache.commons.beanutils.PropertyUtils;
import org.archboy.clobaframe.query.QueryException;

/**
 *
 * @author arch
 */
class QuerySupport {

	/**
	 * Get the object property value by key (i.e. property name).
	 * 
	 * @param object Should not NULL.
	 * @param key Should not empty.
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
						String.format("Can not get the value of property [%s] "
								+ "from object [%s].", key, object.getClass().getName()),
						ex);
			} catch (NoSuchMethodException ex) {
				throw new QueryException(
						String.format("Can not get the value of property [%s] "
								+ "from object [%s].", key, object.getClass().getName()),
						ex);
			}
		}
	}

	/**
	 * Compare the two value objects.
	 * @param value1
	 * @param value2
	 * @return 
	 */
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
