package org.archboy.clobaframe.webresource;

import java.util.Collection;

/**
 *
 * @author yang
 */
public interface WebResourceRepository {
	
	/**
	 * 
	 * @param name Resource name includes the relative path, e.g. "css/main.css".
	 * @return NULL if can not find the specify resource.
	 */
	WebResourceInfo getByName(String name);

	Collection<WebResourceInfo> getAll();
}
