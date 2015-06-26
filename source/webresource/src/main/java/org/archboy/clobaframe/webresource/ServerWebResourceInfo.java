package org.archboy.clobaframe.webresource;

import java.util.Collection;

/**
 *
 * @author yang
 */
public interface ServerWebResourceInfo extends WebResourceInfo {
	
	public static final int TYPE_CACHE = 1;
	public static final int TYPE_COMPRESS = 2;
	public static final int TYPE_LOCATION_TRANSFORM = 4;
	public static final int TYPE_MINIFY = 8;
	public static final int TYPE_CONCATENATE = 16;
	
	int getType();
	
	/**
	 * 
	 * @return 
	 */
	Collection<Integer> listInheritTypes();
	
}
