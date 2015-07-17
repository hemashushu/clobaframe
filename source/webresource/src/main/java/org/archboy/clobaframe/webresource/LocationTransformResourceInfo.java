package org.archboy.clobaframe.webresource;

import java.util.Collection;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author yang
 */
public interface LocationTransformResourceInfo extends ResourceInfo {
	
	/**
	 * 
	 * @return 
	 */
	Collection<String> listChildResourceNames();
}
