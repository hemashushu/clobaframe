package org.archboy.clobaframe.webresource;

/**
 * Hold all web resource for reuse to increase the access speed.
 * 
 * This is some kinds of the memory cache, the implementation maybe clean some long 
 * time no access resource automatically.
 * 
 * @author yang
 */
public interface WebResourceCache {

	/**
	 * 
	 * @param name
	 * @return NULL if the specify resource not found.
	 */
	WebResourceInfo get(String name);
	
	/**
	 * 
	 * @param webResourceInfo 
	 */
	void set(WebResourceInfo webResourceInfo);
}
