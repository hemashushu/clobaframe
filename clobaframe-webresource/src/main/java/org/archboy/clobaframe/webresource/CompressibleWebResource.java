package org.archboy.clobaframe.webresource;

/**
 *
 * @author yang
 */
public interface CompressibleWebResource {
	
	/**
	 * Commonly is "gzip".
	 * 
	 * @return 
	 */
	String getCompressAlgorithm();
}
