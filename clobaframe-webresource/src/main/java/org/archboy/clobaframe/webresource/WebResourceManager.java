package org.archboy.clobaframe.webresource;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;


/**
 * Web resources management service.
 *
 *
 * @author yang
 *
 */
public interface WebResourceManager {

	public static final String MIME_TYPE_STYLE_SHEET = "text/css";
	
	public static final List<String> MIME_TYPE_JAVA_SCRIPT = Arrays.asList(
		"text/javascript", // javascript, IE
		"application/x-javascript", // javascript, obsolete, but widely used
		"application/javascript" // javascript, standard 
	);
	
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
	 * Force refresh a specify resource.
	 * 
	 * @param name 
	 */
	void refresh(String name);
	
	void setLocationGenerator(LocationGenerator locationGenerator);
	
	void setResourceCache(WebResourceCache webResourceCache);
}
