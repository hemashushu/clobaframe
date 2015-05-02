package org.archboy.clobaframe.webresource;

import java.util.Collection;

/**
 *
 * @author yang
 */
public interface WebResourceRepository {

	public static final int PRIORITY_HIGHEST = 0;
	public static final int PRIORITY_HIGHER = 2;
	public static final int PRIORITY_HIGH = 4;
	public static final int PRIORITY_NORMAL = 5;
	
	/**
	 * Get the repository implementation name.
	 * 
	 * @return 
	 */
	String getName();
	
	/**
	 * Get the priority of repository.
	 * The high priority repository will be check first when get a resource by
	 * name.
	 * I.e. when there are resources that with duplicate name, the resource
	 * resist in the high priority repository will be selected.
	 * 
	 * @return 
	 */
	int getPriority();
	
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
	Collection<WebResourceInfo> getAll();
}
