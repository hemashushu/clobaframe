package org.archboy.clobaframe.webio;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

/**
 *
 * @author arch
 */
public interface ResourceInfoFactory {

	ResourceInfo make(File file);

	ResourceInfo make(byte[] content, String contentType, Date lastModified);
	
	ResourceInfo make(InputStream inputStream, long contentLength, String contentType, Date lastModified);
}
