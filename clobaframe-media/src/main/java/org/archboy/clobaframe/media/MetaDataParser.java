package org.archboy.clobaframe.media;

import org.archboy.clobaframe.io.file.FileBaseResourceInfo;

/**
 *
 * @author yang
 */
public interface MetaDataParser {

	boolean support(String contentType);
	
	MetaData parse(FileBaseResourceInfo fileBaseResourceInfo);
}
