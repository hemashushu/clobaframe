package org.archboy.clobaframe.blobstore;

import java.util.Map;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author yang
 */
public interface BlobResourceInfo extends ResourceInfo {

	/**
	 * The blob object key.
	 *
	 * @return
	 */
	BlobKey getBlobKey();

	/**
	 *
	 * @return
	 */
	Map<String, String> getMetadata();

	/**
	 *
	 * @param key
	 * @param value
	 */
	void addMetadata(String key, String value);
}
