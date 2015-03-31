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
	 * @param mimeType
	 * @param content
	 * @return
	 */
	BlobResourceInfo make(
			BlobKey blobKey,
			String mimeType,
			InputStream content, long size);

	/**
	 * Create the blob object by byte array.
	 *
	 * @param blobKey
	 * @param mimeType
	 * @param content
	 * @return
	 */
	BlobResourceInfo make(
			BlobKey blobKey,
			String mimeType,
			byte[] content);

}
