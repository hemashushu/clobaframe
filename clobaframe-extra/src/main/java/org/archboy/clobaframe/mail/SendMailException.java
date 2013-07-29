package org.archboy.clobaframe.mail;

import java.io.IOException;

/**
 *
 * @author arch
 */
public class SendMailException extends IOException{

	private static final long serialVersionUID = 1L;

	public SendMailException(String message) {
		super(message);
	}

	public SendMailException(Throwable cause) {
		super(cause);
	}
}
