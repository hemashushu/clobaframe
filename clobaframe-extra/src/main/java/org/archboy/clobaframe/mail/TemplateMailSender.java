package org.archboy.clobaframe.mail;

/**
 *
 * @author arch
 */
public interface TemplateMailSender {

	/**
	 *
	 * @param recipient
	 * @param templateName The template name, commonly resist in the message source.
	 * @param args The place hold values.
	 * @throws SendMailException
	 */
	void send(String recipient, String templateName, Object[] args) throws SendMailException;
}
