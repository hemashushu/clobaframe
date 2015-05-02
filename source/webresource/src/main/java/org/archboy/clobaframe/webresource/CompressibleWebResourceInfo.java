package org.archboy.clobaframe.webresource;

/**
 *
 * @author yang
 */
public interface CompressibleWebResourceInfo extends WebResourceInfo {
	
	/**
	 * Commonly is "gzip".
	 * 
	 * @return 
	 */
	String getCompressAlgorithm();
}
