package org.archboy.clobaframe.media;

import java.io.IOException;

/**
 *
 * @author yang
 */
public class UnsupportedMediaException extends IOException{

	private static final long serialVersionUID = 1L;

	public UnsupportedMediaException(String message){
		super(message);
	}
}
