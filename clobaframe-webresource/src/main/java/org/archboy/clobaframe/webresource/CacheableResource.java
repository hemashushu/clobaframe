package org.archboy.clobaframe.webresource;

import java.util.Collection;

/**
 *
 * @author yang
 */
public interface CacheableResource {
	
	/**
	 * Add the reference web resource.
	 * E.g. "common.css" import the "custom.css" by the "@import" command, then
	 * the "common.css" resource is the update listener, and the "custom.css" is the
	 * cacheable resource
	 * 
	 * @param resourceUpdateListener 
	 */
	void addUpdateListener(CacheableResourceUpdateListener resourceUpdateListener);
	
	void setReferenceResourceNames(Collection<String> names);
	
	void refresh();
}
