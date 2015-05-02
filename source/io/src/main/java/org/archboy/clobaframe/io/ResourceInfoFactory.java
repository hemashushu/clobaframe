package org.archboy.clobaframe.io;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;

/**
 *
 * @author yang
 */
public interface ResourceInfoFactory {

	ResourceInfo make(byte[] content, String mimeType, Date lastModified);
	
	ResourceInfo make(InputStream inputStream, long contentLength, String mimeType, Date lastModified);
	
	TextResourceInfo make(String text, Charset charset, String mimeType, Date lastModified);

}
