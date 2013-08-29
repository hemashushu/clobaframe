package org.archboy.clobaframe.mail.impl;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.mail.MailSender;
import org.archboy.clobaframe.mail.SendMailException;
import org.archboy.clobaframe.mail.SenderAgent;
import org.archboy.clobaframe.mail.SenderAgentFactory;

/**
 *
 * @author arch
 */
@Named
public class MailSenderImpl implements MailSender {

	@Inject
	private SenderAgentFactory senderAgentFactory;

	private SenderAgent senderAgent;

	@PostConstruct
	public void init(){
		senderAgent = senderAgentFactory.getSenderAgent();
	}

	@Override
	public void send(String recipient, String subject, String content) throws SendMailException {
		senderAgent.send(recipient, subject, content);
	}

	@Override
	public void sendWithHtml(String recipient, String subject, String content) throws SendMailException {
		senderAgent.sendWithHtml(recipient, subject, content);
	}

}
