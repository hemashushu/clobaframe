package org.archboy.clobaframe.webresource;

import java.util.Collection;
import org.archboy.clobaframe.io.NamedResourceInfo;
import org.springframework.core.Ordered;

/**
 *
 * About the "order" property,.
 * The high priority repository will be check first when get a resource by
 * name.
 * I.e. when there are resources that with duplicate name, the resource
 * resist in the high priority repository will be selected.
 * 
 * @author yang
 */
public interface ResourceProvider extends Ordered {

	public static final int PRIORITY_HIGHEST = 0;
	public static final int PRIORITY_HIGHER = 20;
	public static final int PRIORITY_HIGH = 40;
	public static final int PRIORITY_NORMAL = 60;
	public static final int PRIORITY_LOW = 80;
	public static final int PRIORITY_LOWER = 100;
	
	/**
	 * Get the repository implementation name.
	 * 
	 * @return 
	 */
	String getName();
	
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
