package org.archboy.clobaframe.webresource;

import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author yang
 */
public interface CompressibleResourceInfo extends ResourceInfo {
	
	/**
	 * Commonly it is "gzip".
	 * 
	 * @return 
	 */
	String getAlgorithm();
}
