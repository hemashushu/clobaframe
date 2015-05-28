package org.archboy.clobaframe.mail.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Named;
import org.springframework.util.Assert;

/**
 * A mail sender for development and testing.
 *
 * This implementation does not send mail actually, it just print message content on console.
 *
 * @author yang
 */
@Named
public class NullMailSender extends AbstractMailSender {

	private final Logger logger = LoggerFactory.getLogger(NullMailSender.class);

	@Override
	public String getName() {
		return "null";
	}

	@Override
	public void send(String recipient, String subject, String content) {
		Assert.hasText(recipient);
		Assert.hasText(subject);
		Assert.hasText(content);
		
		String result = String.format(
				"Recipient: %s\n" +
						"Subject: %s\n" +
						"Content: %s\n",
				recipient, subject, content);

		logger.info(result);
	}

	@Override
	public void sendWithHtml(String recipient, String subject, String content) {
		Assert.hasText(recipient);
		Assert.hasText(subject);
		Assert.hasText(content);
		
		send(recipient, subject, content);
	}
	
}
