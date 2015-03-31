package org.archboy.clobaframe.blobstore;

import java.io.IOException;
import java.io.InputStream;

/**
 * Storing or fetching the large binary objects (i.e. user file, image, long text etc.)
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
	 * @param name
	 * @return
	 */
	boolean existBucket(String name);

	/**
	 * Create a bucket by name.
	 * Duplicate name will be ignored.
	 *
	 * @param name
	 * @throws IOException
	 */
	void createBucket(String name) throws IOException;

	/**
	 * Delete a bucket.
	 * This function can only delete an empty bucket.
	 * If the specified store does not exist will not generate an exception.
	 *
	 * @param name
	 * @throws IOException Delete failed.
	 */
	void deleteBucket(String name) throws IOException;

	/**
	 * Store the blob object into repository.
	 * Object will be overwrite if the key duplicate.
	 * The {@link InputStream} of {@link BlobResourceInfo} will be close
	 * automatically.
	 *
	 * @param blobResourceInfo
	 * @throws java.io.IOException
	 */
	void put(BlobResourceInfo blobResourceInfo) throws IOException;

	/**
	 * Store the blob object into repository.
	 *
	 * @param blobResourceInfo
	 * @param publicReadable This blob object can be public read.
	 * @param priority
	 *     0~9, smaller indicates this object is less important, and storing into a
	 *     minor repository (if possible). "Minor repository" means
	 *     reduced redundancy storing.
	 * @throws java.io.IOException
	 */
	void put(BlobResourceInfo blobResourceInfo, 
			boolean publicReadable,
			int priority) throws IOException;

	/**
	 * Get object by key.
	 *
	 * @param blobKey
	 * @return
	 * @throws IOException If the specify key does not exists.
	 */
	BlobResourceInfo get(BlobKey blobKey) throws IOException;

	/**
	 * Remove object by key.
	 * If the specified object does not exist will not generate an exception.
	 *
	 * @param blobKey
	 * @throws java.io.IOException Delete failed.
	 */
	void delete(BlobKey blobKey) throws IOException;

	/**
	 * List objects by the prefix key.
	 *
	 * @param prefix
	 *     The key name of {@link BlobKey} can assigns a prefix, such as
	 *     'image-', then all objects with 'image-' prefix name will
	 *     be selected. or keep key name as null for return all items in the
	 *     specify bucket.
	 * @return
	 */
	PartialCollection<BlobResourceInfo> list(); //BlobKey prefix);

	/**
	 * List the remain objects by the previous result.
	 *
	 * @param collection
	 * @return
	 */
	PartialCollection<BlobResourceInfo> listNext(PartialCollection<BlobResourceInfo> collection);
}
