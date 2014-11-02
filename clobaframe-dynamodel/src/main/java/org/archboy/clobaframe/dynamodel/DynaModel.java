package org.archboy.clobaframe.dynamodel;

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
public interface DynaModel extends Map<String, Object> {

	/**
	 * Add/update a new property.
	 *
	 * @param key
	 * @param value
	 * @return Return model itself.
	 */
	DynaModel add(String key, Object value);

	/**
	 * Add a child model.
	 *
	 * @param name
	 * @param model
	 *
	 * @return Return model itself.
	 */
	//DynaModel addModel(String name, DynaModel model);
}
