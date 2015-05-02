package org.archboy.clobaframe.io;

import java.util.Date;

/**
 *
 * @author yang
 */
public interface TextResourceInfo extends ResourceInfo {
	
	void updateContent(String text, Date lastModified);
	
}
