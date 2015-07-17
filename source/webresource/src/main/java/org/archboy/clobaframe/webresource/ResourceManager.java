package org.archboy.clobaframe.webresource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.archboy.clobaframe.io.NamedResourceInfo;


/**
 * Resources management service.
 *
 * A resource manager includes one resource provider set.
 * 
 * manager --T-- provider set --T-- provider 1
 *           |                  |-- provider 2
 *           |-- cache          |-- concatenate provider  
 * 
 * 
 * 
 * @author yang
 *
 */
public interface ResourceManager {

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
	 * Get the specify resource by name.
	 * The resource maybe url-transformed, compressed, minified and/or cached.
	 * 
	 * @param name
	 * @return NULL when the specify name can not be found.
	 */
	NamedResourceInfo getServedResource(String name);

	/**
	 *
	 * @param versionName The name that combines with resource name and version.
	 * @return NULL when the specify name can not be found.
	 */
	NamedResourceInfo getServedResourceByVersionName(String versionName);

	/**
	 * Get the un-transform, un-compressed, un-minified, un-cached original resource.
	 * @param name
	 * @return NULL when the specify name can not be found.
	 */
	NamedResourceInfo get(String name);
	
	/**
	 * Get all original resources.
	 * NOTE: virtual web resource repository may CAN NOT be listed.
	 * 
	 * @return 
	 */
	Collection<NamedResourceInfo> list();
	
	/**
	 * Get the location (URL) of the specify resource.
	 *
	 * The location can print on the web page directly.
	 * 
	 * @param resourceInfo
	 * @return
	 */
	String getLocation(NamedResourceInfo resourceInfo);
	
	/**
	 * Get the location (URL) of the specify resource.
	 * 
	 * @param name
	 * @return NULL when the specify name can not be found.
	 */
	String getLocation(String name);

	/**
	 * Force refresh a specify resource.
	 * 
	 * @param name 
	 */
	void refresh(String name);
	
	
}
