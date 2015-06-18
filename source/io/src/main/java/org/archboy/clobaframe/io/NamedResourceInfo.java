package org.archboy.clobaframe.io;

/**
 *
 * @author yang
 */
public interface NamedResourceInfo extends ResourceInfo {
	
	/**
	 * The resource name, include relative path.
	 * E.g. "common.css", "index.js", "css/main.css".
	 * 
	 * @return
	 */
	String getName();
}
