package org.archboy.clobaframe.io;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

/**
 *
 * @author yang
 */
public interface ResourceInfoFactory {

	ResourceInfo make(byte[] content, String contentType, Date lastModified);
	
	ResourceInfo make(InputStream inputStream, long contentLength, String contentType, Date lastModified);
}
