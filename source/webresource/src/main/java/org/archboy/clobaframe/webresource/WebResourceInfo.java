package org.archboy.clobaframe.webresource;

import org.archboy.clobaframe.io.NamedResourceInfo;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 * The web resource object.
 * 
 * The java script, style sheet, image and fonts files.
 *
 * @author yang
 *
 */
public interface WebResourceInfo extends NamedResourceInfo{

	

	/**
	 * Content hash.
	 * 
	 * Commonly uses the MD5/SHA256 algorithm.
	 * It doesn't have to be the hash value for the current content, 
	 * it can be the underlying content hash values
	 *
	 * @return
	 */
	String getContentHash();

}
