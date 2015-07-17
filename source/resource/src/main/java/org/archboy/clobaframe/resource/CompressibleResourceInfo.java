package org.archboy.clobaframe.resource;

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
