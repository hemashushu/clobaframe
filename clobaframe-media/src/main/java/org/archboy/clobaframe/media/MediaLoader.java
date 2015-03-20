package org.archboy.clobaframe.media;

import java.io.IOException;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;

/**
 *
 * @author yang
 */
public interface MediaLoader {
	
	boolean support(String mimeType);
	
	/**
	 * 
	 * @param fileBaseResourceInfo
	 * @return
	 * @throws IOException 
	 */
	Media load(FileBaseResourceInfo fileBaseResourceInfo) throws IOException;
	
}
