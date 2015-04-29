package org.archboy.clobaframe.io;

import java.io.File;
import java.io.InputStream;

/**
 *
 * @author yang
 */
public interface MimeTypeDetector {

	public static final String MIME_TYPE_UNKNOWN = "application/octet-stream";

	/**
	 * This function will check both the extension name and file stream.
	 * 
	 * @param file
	 * @return MIME_TYPE_UNKNOWN if can not detect.
	 */
	String getByFile(File file);
	
	/**
	 *
	 * @param filename
	 * @return MIME_TYPE_UNKNOWN if can not detect.
	 */
	String getByExtensionName(String filename);

	/**
	 * This function works not very well.
	 * 
	 * @param in
	 * @return MIME_TYPE_UNKNOWN if can not detect.
	 */
	String getByContent(InputStream in);
}
