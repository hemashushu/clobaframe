package org.archboy.clobaframe.webresource;

import java.util.Collection;

/**
 *
 * @author yang
 */
public interface VirtualWebResourceSource {
	
	/**
	 * The source name.
	 * The source name MUST be unique. It's recommended that
	 * use the form "virtual/" + "source name", e.g. "virtual/theme1",
	 * "virtual/custom1".
	 * @return 
	 */
	String getName();
	
	/**
	 * 
	 * @param name Resource name includes the relative path, e.g. "css/main.css".
	 * @return NULL when can not find the specify resource.
	 */
	WebResourceInfo getByName(String name);

	/**
	 * 
	 * @return EMPTY when no resources.
	 */
	Collection<WebResourceInfo> getAll();
	
}
