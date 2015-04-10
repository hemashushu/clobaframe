package org.archboy.clobaframe.webresource;

import java.util.Collection;

/**
 *
 * @author yang
 */
public interface VirtualWebResourceProvider {
	
	/**
	 * Lookup the virtual resource by name.
	 * 
	 * @param name
	 * @return NULL if the specify resource does not found.
	 */
	WebResourceInfo lookup(String name);
	
	/**
	 * 
	 * @return EMPTY when no resources.
	 */
	Collection<String> list();
}
