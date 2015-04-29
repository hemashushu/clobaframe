package org.archboy.clobaframe.blobstore;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 * Generates the {@link BlobResourceInfo} object by {@link InputStream} or byte array.
 *
 * @author yang
 */
public interface BlobResourceInfoFactory {

	/**
	 * Create the blob object by InputStream.
	 *
	 * @param repositoryName
	 * @param key
	 * @param inputStream
	 * @param contentLength
	 * @param mimeType
	 * @param lastModified Optional
	 * @param metadata Optional
	 * @return
	 */
	BlobResourceInfo make(
			String repositoryName, String key,
			InputStream inputStream, long contentLength,
			String mimeType, Date lastModified,
			Map<String, Object> metadata);

	/**
	 * Create the blob object by byte array.
	 *
	 * @param repositoryName
	 * @param key
	 * @param content
	 * @param mimeType
	 * @param lastModified Optional
	 * @param metadata Optional
	 * @return
	 */
	BlobResourceInfo make(
			String repositoryName, String key,
			byte[] content, String mimeType, Date lastModified,
			Map<String, Object> metadata);
	
	/**
	 * 
	 * @param repositoryName
	 * @param key
	 * @param resourceInfo
	 * @param metadata Optional
	 * @return 
	 */
	BlobResourceInfo make(
			String repositoryName, String key,
			ResourceInfo resourceInfo,
			Map<String, Object> metadata);

}
