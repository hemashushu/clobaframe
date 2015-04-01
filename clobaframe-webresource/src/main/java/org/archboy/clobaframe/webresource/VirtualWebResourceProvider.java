package org.archboy.clobaframe.webresource;

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
	
}
