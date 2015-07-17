package org.archboy.clobaframe.webresource;

import java.util.Collection;
import org.archboy.clobaframe.io.NamedResourceInfo;

/**
 *
 * @author yang
 */
public interface ResourceProviderSet {

	/**
	 * Add source.
	 * @param resourceProvider
	 */
	void addProvider(ResourceProvider resourceProvider);
	
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
	NamedResourceInfo getByName(String name);

	/**
	 * 
	 * @return EMPTY when no resources.
	 */
	Collection<NamedResourceInfo> list();
}
