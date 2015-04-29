package org.archboy.clobaframe.blobstore;

import java.util.Map;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author yang
 */
public interface BlobResourceInfo extends ResourceInfo {

	/**
	 * The repository likes as the blob object collection.
	 * 
	 * There are many repositories in one blob store.
	 * @return
	 */
	String getRepositoryName();
	
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

}
