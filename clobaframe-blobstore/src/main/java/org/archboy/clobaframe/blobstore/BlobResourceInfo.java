package org.archboy.clobaframe.blobstore;

import java.util.Map;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author yang
 */
public interface BlobResourceInfo extends ResourceInfo {

	String getBucketName();
	
	/**
	 * The blob object key.
	 *
	 * @return
	 */
	String getKey();

	/**
	 *
	 * @return
	 */
	Map<String, Object> getMetadata();

	/**
	 * 
	 * @param metadata 
	 */
	//void setMetadata(Map<String, Object> metadata);
	
	/**
	 *
	 * @param key
	 * @param value
	 */
	//void addMetadata(String key, Object value);
}
