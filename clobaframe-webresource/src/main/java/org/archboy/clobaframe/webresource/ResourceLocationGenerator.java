package org.archboy.clobaframe.webresource;

/**
 *
 * @author yang
 */
public interface ResourceLocationGenerator {

	/**
	 * Get the full location (URL) for the specify web resource.
	 *
	 * @param webResourceInfo
	 * @return
	 */
	String getLocation(WebResourceInfo webResourceInfo);
}
