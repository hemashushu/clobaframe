package org.archboy.clobaframe.webresource;

/**
 *
 * @author yang
 */
public interface UniqueNameGenerator {
	
	/**
	 * Get the generator name.
	 * @return 
	 */
	String getName();
	
	
	String getUniqueName(WebResourceInfo webResourceInfo);
}
