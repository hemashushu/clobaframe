package org.archboy.clobaframe.mail.impl;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.mail.MailSender;
import org.archboy.clobaframe.mail.MailSenderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
@Named
public class MailSenderManagerImpl implements MailSenderManager {

	public static final String DEFAULT_MAIL_SENDER_NAME = "null";

	public static final String SETTING_KEY_MAIL_SENDER_NAME = "clobaframe.mail.default";
	
	@Value("${" + SETTING_KEY_MAIL_SENDER_NAME + ":" + DEFAULT_MAIL_SENDER_NAME + "}")
	private String defaultMailSenderName;

	@Inject
	private List<AbstractMailSender> mailSenders;

	private final Logger logger = LoggerFactory.getLogger(MailSenderManagerImpl.class);

	public void setDefaultMailSenderName(String defaultMailSenderName) {
		this.defaultMailSenderName = defaultMailSenderName;
	}

	public void setMailSenders(List<AbstractMailSender> mailSenders) {
		this.mailSenders = mailSenders;
	}

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
