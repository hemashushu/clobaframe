package org.archboy.clobaframe.resource;

import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author yang
 *
 */
public interface ContentHashResourceInfo extends ResourceInfo {

	/**
	 * Content hash.
	 * 
	 * Commonly uses the MD5/SHA256 algorithm.
	 * It doesn't have to is the hash value for the current content, 
	 * it can be the upstream content hash values when the actually content
	 * does not changed.
	 *
	 * @return
	 */
	String getContentHash();

}
