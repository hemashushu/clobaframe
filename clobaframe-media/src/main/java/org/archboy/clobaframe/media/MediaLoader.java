package org.archboy.clobaframe.media;

import java.io.IOException;
import org.archboy.clobaframe.webio.ResourceInfo;

/**
 *
 * @author yang
 */
public interface MediaLoader {
	
	boolean support(String contentType);
	
	/**
	 * 
	 * @param resourceInfo
	 * @return
	 * @throws IOException 
	 */
	Media load(ResourceInfo resourceInfo) throws IOException;
	
}
