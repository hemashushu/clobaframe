package org.archboy.clobaframe.mail.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Named;

/**
 * A mail agent for development and test environments.
 *
 * This agent do not send mail actually, just print message content on console.
 *
 * @author yang
 */
@Named
public class NullMailSenderClientAdapter implements MailSenderClientAdapter{

	private final Logger logger = LoggerFactory.getLogger(NullMailSenderClientAdapter.class);

	@Override
	public String getName() {
		return "null";
	}

	@Override
	public void send(String recipient, String subject, String content) {
		String result = String.format(
				"Recipient: %s\nSubject: %s\nContent: %s",
				recipient, subject, content);

		logger.info(result);
	}

	@Override
	public void sendWithHtml(String recipient, String subject, String content) {
		send(recipient, subject, content);
	}
}
