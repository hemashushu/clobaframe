package org.archboy.clobaframe.media;

import java.util.Date;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author yang
 */
public interface Media {
	
	/**
	 * 
	 * @return 
	 */
	MetaData getMetaData();
	
	/**
	 * 
	 * @return 
	 */
	ResourceInfo getResourceInfo();
	
	/**
	 * 
	 * @param lastModified Specify the new last modified date. NULL for keep original.
	 * @return 
	 */
	ResourceInfo getResourceInfo(Date lastModified);
}
