package org.archboy.clobaframe.blobstore;

/**
 *
 * @author yang
 */
public interface BlobstoreManager {
	
	/**
	 * 
	 * @param name
	 * @return 
	 */
	Blobstore getBlobstore(String name);
	
	/**
	 * 
	 * @return 
	 */
	Blobstore getDefault();
}
