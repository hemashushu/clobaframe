package org.archboy.clobaframe.webio;

import java.io.File;
import java.io.InputStream;

/**
 *
 * @author arch
 */
public interface ContentTypeAnalyzer {

	public static final String CONTENT_TYPE_UNKNOWN = "application/octet-stream";

	/**
	 * This function will check both the extension name and file stream.
	 * 
	 * @param file
	 * @return CONTENT_TYPE_UNKNOWN if can not detect.
	 */
	String getByFile(File file);
	
	/**
	 *
	 * @param filename
	 * @return CONTENT_TYPE_UNKNOWN if can not detect.
	 */
	String getByExtensionName(String filename);

	/**
	 * This function works not very well.
	 * 
	 * @param in
	 * @return CONTENT_TYPE_UNKNOWN if can not detect.
	 */
	String getByContent(InputStream in);
}
