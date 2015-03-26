package org.archboy.clobaframe.webresource;

import org.archboy.clobaframe.io.ResourceInfo;

/**
 * The web resource object.
 * 
 * The java script, style sheet, image and fonts files.
 *
 * @author yang
 *
 */
public interface WebResourceInfo extends ResourceInfo{

	/**
	 * The resource name, include relative path.
	 * E.g. "common.css", "index.js", "css/main.css".
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Content hash.
	 * Commonly uses the MD5/SHA256 algorithm.
	 * It doesn't have to be the hash value for the current content, 
	 * or it may be the underlying content hash values
	 *
	 * @return
	 */
	String getContentHash();

}
