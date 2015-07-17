package org.archboy.clobaframe.resource;

import java.util.Collection;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author yang
 */
public interface WrapperResourceInfo extends ResourceInfo {
	
	public static final int TYPE_CACHE = 1;
	public static final int TYPE_COMPRESS = 2;
	public static final int TYPE_LOCATION_TRANSFORM = 4;
	public static final int TYPE_MINIFY = 8;
	public static final int TYPE_CONCATENATE = 16;
	
	/**
	 * The current type
	 * @return 
	 */
	int getType();
	
	/**
	 * 
	 * @return 
	 */
	Collection<Integer> listTypes();
	
}
