package org.archboy.clobaframe.media;

/**
 *
 * @author yang
 */
public interface MetaDataParser {

	boolean support(String contentType);
	
	MetaData parse(Media media);
}
