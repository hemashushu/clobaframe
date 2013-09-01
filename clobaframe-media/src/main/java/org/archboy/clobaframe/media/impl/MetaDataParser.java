package org.archboy.clobaframe.media.impl;

import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.media.MetaData;

/**
 *
 * @author yang
 */
public interface MetaDataParser {

	/**
	 * 
	 * @param object Can be the FileBaseResourceInfo.
	 * @return 
	 */
	MetaData parse(Object object);
}
