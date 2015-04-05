package org.archboy.clobaframe.mail.impl;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.mail.MailSender;
import org.archboy.clobaframe.mail.MailSenderManager;
import org.archboy.clobaframe.mail.SendMailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
@Named
public class MailSenderManagerImpl implements MailSenderManager {

	private static final String DEFAULT_MAIL_SENDER_NAME = "null";

	@Value("${clobaframe.mail.default}")
	private String defaultMailSenderName = DEFAULT_MAIL_SENDER_NAME;

	@Inject
	private List<AbstractMailSender> mailSenders;

	private final Logger logger = LoggerFactory.getLogger(MailSenderManagerImpl.class);

	@Bean(name = "defaultMailSender")
	@Override
	public MailSender getDefault() {
		return getMailSender(defaultMailSenderName);
	}
	
	@Override
	public AbstractMailSender getMailSender(String name) {
		Assert.hasText(name);
		
		for(AbstractMailSender sender : mailSenders){
			if (sender.getName().equals(name)){
				return sender;
			}
		}

		throw new IllegalArgumentException(
				String.format("Can not find the specify email sender implementation [%s].", name));
	}

}
