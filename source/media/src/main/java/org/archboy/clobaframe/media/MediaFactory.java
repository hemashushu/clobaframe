package org.archboy.clobaframe.media;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.TemporaryResources;

/**
 * Make {@link Media} from input stream or resource.
 * 
 * @author yang
 */
public interface MediaFactory {
	
	/**
	 * Make by byte array.
	 *
	 * @param data
	 * @param mimeType 
	 * @param lastModified Specify the last modified date, NULL for current date.
	 * @param temporaryResources
	 * @return
	 * @throws IOException
	 */
	Media make(byte[] data, String mimeType, Date lastModified, TemporaryResources temporaryResources) throws IOException;

	/**
	 * Make by InputStream.
	 *
	 * @param inputStream A stream contains image data.
	 * <p>
	 *     The stream will be closed after this call.
	 * </p>
	 * @param mimeType 
	 * @param lastModified Specify the last modified date, NULL for current date.
	 * @param temporaryResources
	 * @return
	 * @throws IOException
	 */
	Media make(InputStream inputStream, String mimeType, Date lastModified, TemporaryResources temporaryResources) throws IOException;
	
	/**
	 * Make by file.
	 *
	 * @param file
	 * @param temporaryResources
	 * @return
	 * @throws IOException
	 */
	Media make(File file, TemporaryResources temporaryResources) throws IOException;

	/**
	 * Make by URL.
	 *
	 * @param url
	 * @param temporaryResources
	 * @return
	 * @throws IOException
	 */
	Media make(URL url, TemporaryResources temporaryResources) throws IOException;

	/**
	 * Make by {@link ResourceInfo}.
	 * 
	 * @param resourceInfo
	 * @param temporaryResources
	 * @return
	 * @throws IOException 
	 */
	Media make(ResourceInfo resourceInfo, TemporaryResources temporaryResources) throws IOException;
}
