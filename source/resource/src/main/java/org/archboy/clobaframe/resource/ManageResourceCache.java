package org.archboy.clobaframe.resource;

import org.archboy.clobaframe.io.NamedResourceInfo;

/**
 * Hold all resource for reuse to increase the access speed.
 * 
 * This is some kinds of the memory cache, the implementation maybe clean some long 
 * time no access resource automatically.
 * 
 * @author yang
 */
public interface ManageResourceCache {

	/**
	 * 
	 * @param name
	 * @return NULL if the specify resource not found.
	 */
	NamedResourceInfo get(String name);
	
	/**
	 * 
	 * @param resourceInfo 
	 */
	void set(NamedResourceInfo resourceInfo);
}
