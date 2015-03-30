package org.archboy.clobaframe.webresource;

/**
 *
 * @author yang
 */
public interface CompressableResource {
	
	/**
	 * Commonly is "gzip".
	 * 
	 * @return 
	 */
	String getCompressAlgorithm();
}
