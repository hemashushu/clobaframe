package org.archboy.clobaframe.blobstore;

import java.io.IOException;

/**
 *
 * @author arch
 */
public interface StoreAgent {

	/**
	 * Agent name.
	 *
	 * @return
	 */
	String getName();

	/**
	 * Check whether a bucket is exists or not.
	 *
	 * A bucket is the collection of blob objects, just like the folder to the file.
	 *
	 * @param name
	 * @return
	 */
	boolean existBucket(String name);

	/**
	 * Create a bucket by name.
	 * <p>
	 *     Duplicate name will just do nothing.
	 * </p>
	 *
	 * @param name
	 * @throws IOException
	 */
	void createBucket(String name) throws IOException;

	/**
	 * Delete a bucket.
	 * <p>
	 *     This function can only delete an empty bucket.
	 *     Delete non-exists bucket will not raise exception.
	 * </p>
	 *
	 * @param name
	 * @throws IOException
	 */
	void deleteBucket(String name) throws IOException;

	/**
	 * Store the blob object into repository.
	 *
	 * @param blobResourceInfo
	 * @param publicReadable This blob object can be public read.
	 * @param minor
	 * <p>
	 *     Indicate this object is less important, and storing into a
	 *     minor repository (if possible). "Minor repository" means
	 *     reduced redundancy storing.
	 * </p>
	 */
	void put(BlobResourceInfo blobResourceInfo, 
			boolean publicReadable,
			boolean minor) throws IOException;

	/**
	 * Get object by key.
	 *
	 * @param key
	 * @return
	 * @throws IOException If the specify key does not exists.
	 */
	BlobResourceInfo get(BlobKey blobKey) throws IOException;

	/**
	 * Remove object by key.
	 * <p>
	 *     No exception will be occur if the specify key does not exists.
	 * </p>
	 *
	 * @param blobKey
	 */
	void delete(BlobKey blobKey) throws IOException;

	/**
	 * List objects by the prefix key.
	 *
	 * @param prefix
	 * <p>
	 *     The key name of {@link BlobKey} can assigns a prefix, such as
	 *     'image-', then all objects with 'image-' prefix name will
	 *     be selected. or keep key name as null for return all items in the
	 *     specify bucket.
	 * </p>
	 * @return
	 */
	BlobResourceInfoPartialCollection list(BlobKey prefix);

	/**
	 * List the remain objects by the previous result.
	 *
	 * @param collection
	 * @return
	 */
	BlobResourceInfoPartialCollection listNext(BlobResourceInfoPartialCollection collection);

}
