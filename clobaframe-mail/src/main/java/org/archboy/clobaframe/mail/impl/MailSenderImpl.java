package org.archboy.clobaframe.mail.impl;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.mail.MailSender;
import org.archboy.clobaframe.mail.SendMailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
@Named
public class MailSenderImpl implements MailSender {

	private static final String DEFAULT_SENDER_CLIENT_ADAPTER_NAME = "null";

	@Value("${mail.agent}")
	private String defaultSenderClientAdapterName = DEFAULT_SENDER_CLIENT_ADAPTER_NAME;

	private MailSenderClientAdapter defaultAdapter;
	
	@Inject
	private List<MailSenderClientAdapter> clientAdapters;

	private final Logger logger = LoggerFactory.getLogger(MailSenderImpl.class);
	
	@PostConstruct
	public void init() {
		defaultAdapter = getClientAdapter(defaultSenderClientAdapterName);
		logger.info("Using the [{}] mail sender client adapter as default.", defaultSenderClientAdapterName);
	}

	public List<MailSenderClientAdapter> getClientAdapters() {
		return clientAdapters;
	}

	public MailSenderClientAdapter getClientAdapter(String name) {
		Assert.hasText(name);
		
		for(MailSenderClientAdapter adapter : clientAdapters){
			if (adapter.getName().equals(defaultSenderClientAdapterName)){
				return adapter;
			}
		}

		throw new IllegalArgumentException(
				String.format("The specify email sender client adapter [%s] not found.", name));
	}

	@Override
	public void send(String recipient, String subject, String content) throws SendMailException {
		Assert.hasText(recipient);
		Assert.hasText(subject);
		Assert.hasText(content);
		
		defaultAdapter.send(recipient, subject, content);
	}

	@Override
	public void sendWithHtml(String recipient, String subject, String content) throws SendMailException {
		Assert.hasText(recipient);
		Assert.hasText(subject);
		Assert.hasText(content);
		
		defaultAdapter.sendWithHtml(recipient, subject, content);
	}

}
