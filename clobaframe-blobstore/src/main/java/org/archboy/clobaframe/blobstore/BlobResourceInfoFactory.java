package org.archboy.clobaframe.blobstore;

import java.io.InputStream;

/**
 * Generates the {@link BlobResourceInfo} object by {@link InputStream} or byte array.
 *
 * @author yang
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
