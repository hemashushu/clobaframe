package org.archboy.clobaframe.webresource;

/**
 * Hold all web resource.
 * 
 * @author yang
 */
public interface ResourceCollection {

	/**
	 * 
	 * @param name
	 * @return NULL if the specify resource not found.
	 */
	WebResourceInfo getByName(String name);
	
	void add (WebResourceInfo webResourceInfo);
}
