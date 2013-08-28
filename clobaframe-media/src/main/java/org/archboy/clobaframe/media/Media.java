package org.archboy.clobaframe.media;

import java.io.InputStream;
import java.util.Date;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author yang
 */
public interface Media {
	
	String getContentType();
	
	MetaData getMetaData();
	
	//InputStream getInputStream();
	
	ResourceInfo getResourceInfo();
	
	/**
	 * 
	 * @param lastModified Specify the new last modified date. NULL for keep original.
	 * @return 
	 */
	ResourceInfo getResourceInfo(Date lastModified);
}
