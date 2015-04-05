package org.archboy.clobaframe.webresource;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
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
		"application/javascript", // javascript, standard 
		"text/javascript", // javascript, IE
		"application/x-javascript" // javascript, obsolete
	);
	
	public static final List<String> MIME_TYPE_FONT = Arrays.asList(
		"application/x-font-ttf", // ttf
		"image/svg+xml", // svg
		"application/vnd.ms-fontobject", // eot
		"application/x-font-woff" // woff
	);
	
	public static final List<String> MIME_TYPE_TEXT = Arrays.asList(
		"text/plain",
		"text/html",
		"application/xml"
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

	String getVersionName(WebResourceInfo webResourceInfo);
	
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
	
	/**
	 * 
	 * @param locationGenerator 
	 */
	void setLocationGenerator(LocationGenerator locationGenerator);
	
	/**
	 * 
	 * @param webResourceCache 
	 */
	void setResourceCache(WebResourceCache webResourceCache);
}
