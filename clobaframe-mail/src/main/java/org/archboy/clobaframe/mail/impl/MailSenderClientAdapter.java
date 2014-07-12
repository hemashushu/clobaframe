package org.archboy.clobaframe.mail.impl;

import org.archboy.clobaframe.mail.SendMailException;

/**
 *
 * @author yang
 */
public interface MailSenderClientAdapter {

	String getName();

	/**
	 *
	 * @param recipient
	 * @param subject
	 * @param content
	 * @throws SendMailException If send mail fail.
	 */
	void send(String recipient, String subject, String content) throws SendMailException;

	/**
	 *
	 * @param recipient
	 * @param subject
	 * @param content
	 * @throws SendMailException If send mail fail.
	 */
	void sendWithHtml(String recipient, String subject, String content) throws SendMailException;
}
