package org.archboy.clobaframe.webresource;

/**
 *
 * @author yang
 */
public interface ResourceRepository {
	
	/**
	 * 
	 * @param name Resource name includes the relative path, e.g. "css/main.css".
	 * @return NULL if can not find the specify resource.
	 */
	WebResourceInfo getByName(String name);

}
