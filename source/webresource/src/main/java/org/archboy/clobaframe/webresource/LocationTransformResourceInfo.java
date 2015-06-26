package org.archboy.clobaframe.webresource;

import java.util.Collection;

/**
 *
 * @author yang
 */
public interface LocationTransformResourceInfo extends WebResourceInfo {
	
	/**
	 * 
	 * @return 
	 */
	Collection<String> listChildResourceNames();
}
