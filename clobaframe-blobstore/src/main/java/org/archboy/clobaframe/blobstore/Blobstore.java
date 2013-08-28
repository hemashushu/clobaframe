/*
 * Copyright 2011 Spark Young (sparkyoungs@gmail.com). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.archboy.clobaframe.blobstore;

import java.io.IOException;
import java.io.InputStream;

/**
 * Storing or fetching the large binary objects (i.e. file data, imaging data, long text etc.)
 *
 * @author young
 *
 */
public interface Blobstore {

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
	 *<p>
	 *     Object will be overwrite if the key duplicate,
	 *     and the {@link InputStream} of {@link BlobResourceInfo} will be close
	 *     automatically.
	 *</p>
	 *
	 * @param blobResourceInfo
	 */
	void put(BlobResourceInfo blobResourceInfo) throws IOException;

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
