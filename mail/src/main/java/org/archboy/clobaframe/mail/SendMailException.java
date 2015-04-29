package org.archboy.clobaframe.mail;

import java.io.IOException;

/**
 *
 * @author yang
 */
public class SendMailException extends IOException{

	private static final long serialVersionUID = 1L;

	public SendMailException(String message) {
		super(message);
	}

	public SendMailException(String message, Throwable cause) {
		super(message, cause);
	}
}
