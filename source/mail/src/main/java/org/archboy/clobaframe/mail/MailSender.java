package org.archboy.clobaframe.mail;

/**
 *
 * @author yang
 */
public interface MailSender {
/**
	 *
	 * @param recipient
	 * @param subject
	 * @param content
	 * @throws SendMailException If send mail failed.
	 */
	void send(String recipient, String subject, String content) throws SendMailException;

	/**
	 *
	 * @param recipient
	 * @param subject
	 * @param content
	 * @throws SendMailException If send mail failed.
	 */
	void sendWithHtml(String recipient, String subject, String content) throws SendMailException;
	
	/**
	 *
	 * @param recipient
	 * @param templateName The template name, that defined in the message source.
	 * @param args The place hold values.
	 * @throws SendMailException If send mail failed.
	 */
	void sendWithTemplate(String recipient, String templateName, Object[] args) throws SendMailException;
}
