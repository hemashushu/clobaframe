package org.archboy.clobaframe.blobstore;

import java.io.IOException;
import java.io.InputStream;

/**
 * Blob bucket manager.
 *
 * @author yang
 *
 */
public interface Blobstore {

	public static final int MIN_STORE_PRIORITY = 0;
	public static final int DEFAULT_STORE_PRIOTITY = 5;
	
	/**
	 * Check whether a bucket exists.
	 *
	 * A bucket is the collection of blob objects, just like the folder to the file.
	 *
	 * @param bucketName
	 * @return
	 */
	boolean exist(String bucketName);

	/**
	 * Create a bucket by name.
	 * Duplicate name will be ignored.
	 *
	 * @param bucketName
	 * @throws IOException
	 */
	void create(String bucketName) throws IOException;

	/**
	 * 
	 * @param bucketName
	 * @return
	 * @throws IOException 
	 */
	BlobResourceRepository getRepository(String bucketName) throws IOException;
	
	/**
	 * Delete a bucket.
	 * This function can only delete an empty bucket.
	 * If the specified store does not exist will not generate an exception.
	 *
	 * @param bucketName
	 * @throws IOException Delete failed.
	 */
	void delete(String bucketName) throws IOException;


}
