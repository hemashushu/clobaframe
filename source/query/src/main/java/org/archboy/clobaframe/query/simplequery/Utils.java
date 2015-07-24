package org.archboy.clobaframe.query.simplequery;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.WrapDynaBean;
import org.archboy.clobaframe.common.collection.DefaultObjectMap;
import org.archboy.clobaframe.common.collection.ObjectMap;
import org.archboy.clobaframe.query.QueryException;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
public class Utils {

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
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
				throw new QueryException(
						String.format(
								"Can not get the value of property [%s] from object [%s].", 
								key, object.getClass().getName()),
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
	
	/**
	 * Wrap an object info ObjectMap.
	 * 
	 * @param object
	 * @return 
	 */
	public static ObjectMap Wrap(Object object){
		Assert.notNull(object);
		
		ObjectMap viewModel = new DefaultObjectMap();
		
		// check object type
		if (object instanceof Map){
			Map m = (Map)object;
			for(Object name : m.keySet()){
				viewModel.add(name.toString(), m.get(name));
			}
			return viewModel;
		}

		// copy all properties (except 'class') to map
		DynaBean dynaBean = new WrapDynaBean(object);
		DynaClass dynaClass = dynaBean.getDynaClass();
		for(DynaProperty property : dynaClass.getDynaProperties()){
			String key = property.getName();
			if (key.equals("class")){
				continue;
			}

			viewModel.add(key, dynaBean.get(key));
		}
		
		return viewModel;
	}
	
	public static ObjectMap Wrap(Object object, boolean deep){
		throw new UnsupportedOperationException("Does not supported.");
	}
	
	/**
	 * Wrap an object into ObjectMap with the specify properties.
	 * 
	 * @param object
	 * @param names
	 * @return 
	 */
	public static ObjectMap Wrap(Object object, Collection<String> names){
		Assert.notNull(object);
		Assert.notNull(names);
		
		ObjectMap viewModel = new DefaultObjectMap();
		
		// check object type
		if (object instanceof Map){
			Map m = (Map)object;
			for(Object name : m.keySet()){
				String s = name.toString();
				if (names.contains(s)){
					viewModel.add(s, m.get(name));
				}
			}
			return viewModel;
		}
		
		// copy all properties (except 'class') to map
		DynaBean dynaBean = new WrapDynaBean(object);
		DynaClass dynaClass = dynaBean.getDynaClass();
		for(DynaProperty property : dynaClass.getDynaProperties()){
			String key = property.getName();
			if (names.contains(key)){
				viewModel.add(key, dynaBean.get(key));
			}
		}
		
		return viewModel;
	}
}
