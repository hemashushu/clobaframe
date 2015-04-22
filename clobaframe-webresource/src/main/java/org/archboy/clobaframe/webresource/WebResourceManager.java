package org.archboy.clobaframe.webresource;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * Web resources management service.
 *
 * A web resource manager will includes one web resource repository set.
 * 
 * manager --T-- repository set --T-- repository 1
 *           |                    |-- repository 2
 *           |-- cache            |-- virtual repository --T-- provider 1
 *                                |                        |-- provider 2
 *                                |                        |-- provider 3
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
	 * Get the specify web resource by name.
	 * The resource maybe url-transformed, compressed, minified and/or cached.
	 * 
	 * @param name
	 * @return
	 */
	WebResourceInfo getResource(String name);

	/**
	 *
	 * @param versionName The name that combines with resource name and version.
	 * @return
	 */
	WebResourceInfo getResourceByVersionName(String versionName);

	/**
	 * Get the un-transform, un-compressed, un-minified, un-cached web resource.
	 * @param name
	 * @return
	 */
	WebResourceInfo getOriginalResource(String name);
	
	/**
	 * Get all web resources name.
	 * Keep in mind that some virtual web resource repository can NOT be list.
	 * 
	 * @return 
	 */
	Collection<String> getAllNames();
	
	/**
	 * 
	 * @param webResourceInfo
	 * @return 
	 */
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
	 */
	String getLocation(String name);

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
	void setWebResourceCache(WebResourceCache webResourceCache);
	
	/**
	 * 
	 * @param versionStrategy 
	 */
	void setVersionStrategy(VersionStrategy versionStrategy);
}
