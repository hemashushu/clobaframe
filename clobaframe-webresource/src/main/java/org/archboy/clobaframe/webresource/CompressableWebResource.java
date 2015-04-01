package org.archboy.clobaframe.webresource;

/**
 *
 * @author yang
 */
public interface CompressableWebResource {
	
	/**
	 * Commonly is "gzip".
	 * 
	 * @return 
	 */
	String getCompressAlgorithm();
}
