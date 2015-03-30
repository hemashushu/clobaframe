package org.archboy.clobaframe.webresource;

/**
 * Hold all web resource for reuse and increase the access speed.
 * 
 * This is some kinds of the cache, the implementation maybe clean some long 
 * time no access resource automatically.
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
