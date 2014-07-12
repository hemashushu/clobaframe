package org.archboy.clobaframe.blobstore.impl;

import java.io.IOException;
import org.archboy.clobaframe.blobstore.BlobKey;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;
import org.archboy.clobaframe.blobstore.BlobResourceInfoPartialCollection;

/**
 *
 * @author yang
 */
public interface BlobstoreClientAdapter {

	/**
	 * Adapter name.
	 * 
	 * @return 
	 */
	String getName();

	boolean existBucket(String name);

	void createBucket(String name) throws IOException;

	void deleteBucket(String name) throws IOException;

	void put(BlobResourceInfo blobResourceInfo, 
			boolean publicReadable,
			int priority) throws IOException;

	BlobResourceInfo get(BlobKey blobKey) throws IOException;

	void delete(BlobKey blobKey) throws IOException;

	BlobResourceInfoPartialCollection list(BlobKey prefix);

	BlobResourceInfoPartialCollection listNext(BlobResourceInfoPartialCollection collection);

}
