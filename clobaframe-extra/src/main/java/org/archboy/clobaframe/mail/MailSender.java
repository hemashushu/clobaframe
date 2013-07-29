package org.archboy.clobaframe.mail;

/**
 *
 * @author arch
 */
public interface MailSender {
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
