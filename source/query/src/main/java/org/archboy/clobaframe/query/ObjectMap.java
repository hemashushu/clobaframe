package org.archboy.clobaframe.query;

import java.util.Map;

/**
 *
 * @author yang
 */
public interface ObjectMap extends Map<String, Object> {

	/**
	 * Add/update a new property.
	 *
	 * @param key
	 * @param value
	 * @return Return model itself.
	 */
	ObjectMap add(String key, Object value);

	/**
	 * Create and add a child model.
	 *
	 * @param name
	 *
	 * @return Return the child model.
	 */
	ObjectMap addChild(String name);
	
	/**
	 * Return the parent model (if exists).
	 * 
	 * @return NULL when does not exist.
	 */
	ObjectMap parent();
	
	/**
	 * Return the top model.
	 * 
	 * @return itself when it's topmost.
	 */
	ObjectMap top();
	
}
