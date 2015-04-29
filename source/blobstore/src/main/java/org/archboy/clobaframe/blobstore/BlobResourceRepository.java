package org.archboy.clobaframe.blobstore;

import java.io.IOException;

/**
 * Blob resource repository.
 * 
 * For storing or fetching the large binary objects (i.e. user file, image, long text etc.)
 * @author yang
 */
public interface BlobResourceRepository {
	
	public static final int PRIORITY_MIN = 0;
	public static final int PRIORITY_DEFAULT = 5;
	
	/**
	 * The repository name;
	 * @return 
	 */
	String getName();
	
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
	 * @param publicReadable Indicates this blob object can be public read, default is TRUE.
	 * @param priority
	 *     0~9, smaller indicates this object is less important, and storing into a
	 *     minor repository (if possible), "Minor repository" means
	 *     reduced redundancy storing, default is {@link BlobResourceRepository#PRIORITY_DEFAULT}
	 * @throws java.io.IOException
	 */
	void put(BlobResourceInfo blobResourceInfo, 
			boolean publicReadable,
			int priority) throws IOException;

	/**
	 * Get object by key.
	 *
	 * @param key
	 * @return NULL If the specify key does not exists
	 */
	BlobResourceInfo get(String key);

	/**
	 * Remove object by key.
	 * It will NOT occurs an exception if the specify object does not exist.
	 *
	 * @param key
	 * @throws java.io.IOException Delete failed.
	 */
	void delete(String key) throws IOException;

	/**
	 * List objects by the prefix key.
	 *
	 * @return 
	 */
	PartialCollection<BlobResourceInfo> list();

	/**
	 * List the remain objects by the previous result.
	 *
	 * @param prevCollection
	 * @return
	 */
	PartialCollection<BlobResourceInfo> listNext(PartialCollection<BlobResourceInfo> prevCollection);
	
}
