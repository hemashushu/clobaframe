package org.archboy.clobaframe.mail.impl;

import java.text.MessageFormat;
import java.util.Locale;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.archboy.clobaframe.mail.SendMailException;
import org.archboy.clobaframe.mail.SenderAgent;
import org.archboy.clobaframe.mail.SenderAgentFactory;
import org.archboy.clobaframe.mail.TemplateMailSender;

/**
 *
 * @author arch
 */
@Component
public class TemplateMailSenderImpl implements TemplateMailSender {

	private static final String subjectCodeExp = "mail.template.{0}.subject";
	private static final String contentCodeExp = "mail.template.{0}.content";

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private SenderAgentFactory senderAgentFactory;

	private SenderAgent senderAgent;

	@PostConstruct
	public void init() {
		senderAgent = senderAgentFactory.getSenderAgent();
	}

	@Override
	public void send(String recipient, String templateName, Object[] args) throws SendMailException{
		String subjectCode = MessageFormat.format(subjectCodeExp, templateName);
		String contentCode = MessageFormat.format(contentCodeExp, templateName);

		Locale locale = LocaleContextHolder.getLocale();
		String subject = messageSource.getMessage(subjectCode, null, locale);
		String content = messageSource.getMessage(contentCode, args, locale);

		senderAgent.send(recipient, subject, content);
	}
}
