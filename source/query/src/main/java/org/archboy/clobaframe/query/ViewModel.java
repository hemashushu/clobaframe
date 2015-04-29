package org.archboy.clobaframe.query;

import java.util.Map;


/**
 * Model for web front-end.
 * <p>
 *     This model has the ability of dynamic properties,
 *     web controller can take dynamic data to viewer by this model.
 * </p>
 *
 * @author yang
 */
public interface ViewModel extends Map<String, Object> {

	/**
	 * Add/update a new property.
	 *
	 * @param key
	 * @param value
	 * @return Return model itself.
	 */
	ViewModel add(String key, Object value);

	/**
	 * Create and add a child model.
	 *
	 * @param name
	 *
	 * @return Return the child model.
	 */
	ViewModel addChild(String name);
	
	/**
	 * Return the parent model (if exists).
	 * 
	 * @return 
	 */
	ViewModel parent();
	
}
