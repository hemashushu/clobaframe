package org.archboy.clobaframe.webresource;

import java.util.Collection;

/**
 *
 * @author yang
 */
public interface CacheableResourceUpdateListener {
	
	/**
	 * Occur while the specify resource has modified.
	 * 
	 * @param resourceName 
	 * @param referenceResourceNames 
	 */
	void onUpdate(String resourceName, Collection<String> referenceResourceNames);
}
