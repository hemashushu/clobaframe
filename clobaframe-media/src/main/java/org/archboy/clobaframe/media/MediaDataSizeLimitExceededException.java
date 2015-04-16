package org.archboy.clobaframe.media;

import java.io.IOException;

/**
 *
 * @author yang
 */
public class MediaDataSizeLimitExceededException extends IOException{

	private static final long serialVersionUID = 1L;

	public MediaDataSizeLimitExceededException(String message){
		super(message);
	}
}
