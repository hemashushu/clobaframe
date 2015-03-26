package org.archboy.clobaframe.webresource;

import java.io.FileNotFoundException;


/**
 * Web resources management service.
 *
 *
 * @author yang
 *
 */
public interface WebResourceManager {

	/**
	 *
	 * @param name
	 * @return
	 * @throws java.io.FileNotFoundException
	 */
	WebResourceInfo getResource(String name) throws FileNotFoundException;

	/**
	 *
	 * @param versionName The name that combines with resource name and version.
	 * @return
	 * @throws java.io.FileNotFoundException
	 */
	WebResourceInfo getResourceByVersionName(String versionName) throws FileNotFoundException;

	/**
	 * Get the location (URL) of the specify resource.
	 *
	 * The location can print on the web page directly.
	 * 
	 * @param webResourceInfo
	 * @return
	 */
	String getLocation(WebResourceInfo webResourceInfo);
	
	/**
	 * Get the location (URL) of the specify resource.
	 * 
	 * @param name
	 * @return
	 * @throws java.io.FileNotFoundException
	 */
	String getLocation(String name) throws FileNotFoundException;

	/**
	 * Force refresh a resource.
	 * 
	 * @param name 
	 */
	void refresh(String name);
}
