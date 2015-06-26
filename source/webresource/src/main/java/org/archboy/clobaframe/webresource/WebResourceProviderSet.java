package org.archboy.clobaframe.webresource;

import java.util.Collection;

/**
 *
 * @author yang
 */
public interface WebResourceProviderSet {

	/**
	 * Add source.
	 * @param webResourceProvider
	 */
	void addProvider(WebResourceProvider webResourceProvider);
	
	/**
	 * Remove source.
	 * @param providerName 
	 */
	void removeProvider(String providerName);
	
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
	Collection<WebResourceInfo> list();
}
