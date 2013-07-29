package org.archboy.clobaframe.webio;

import java.io.File;
import java.util.Date;

/**
 *
 * @author arch
 */
public interface ResourceInfoFactory {

	ResourceInfo make(File file);

	ResourceInfo make(byte[] content, String contentType, Date lastModified);
	
}
