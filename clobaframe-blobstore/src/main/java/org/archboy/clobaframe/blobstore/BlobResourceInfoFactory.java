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

import java.io.InputStream;

/**
 * Generates the {@link BlobResourceInfo} object by {@link InputStream} or byte array.
 *
 * @author young
 */
public interface BlobResourceInfoFactory {

	/**
	 * Create the blob object by InputStream.
	 *
	 * @param blobKey
	 * @param size
	 * @param contentType
	 * @param content
	 * @return
	 */
	BlobResourceInfo make(
			BlobKey blobKey,
			String contentType,
			InputStream content, long size);

	/**
	 * Create the blob object by byte array.
	 *
	 * @param blobKey
	 * @param contentType
	 * @param content
	 * @return
	 */
	BlobResourceInfo make(
			BlobKey blobKey,
			String contentType,
			byte[] content);

}
