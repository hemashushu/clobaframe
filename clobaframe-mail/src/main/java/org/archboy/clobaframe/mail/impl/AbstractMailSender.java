package org.archboy.clobaframe.mail.impl;

import java.text.MessageFormat;
import java.util.Locale;
import javax.inject.Inject;
import org.archboy.clobaframe.mail.MailSender;
import org.archboy.clobaframe.mail.SendMailException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
public abstract class AbstractMailSender implements MailSender {

	private static final String messageCodeSubject = "mail.template.{0}.subject";
	private static final String messageCodeContent = "mail.template.{0}.content";

	@Inject
	private MessageSource messageSource;
	
	/**
	 * The implementation name.
	 * 
	 * @return 
	 */
	public abstract String getName();

	@Override
	public void sendWithTemplate(String recipient, String templateName, Object[] args) throws SendMailException {
		Assert.hasText(recipient);
		Assert.hasText(templateName);
		Assert.notNull(args);
		
		String subjectCode = MessageFormat.format(messageCodeSubject, templateName);
		String contentCode = MessageFormat.format(messageCodeContent, templateName);

		Locale locale = LocaleContextHolder.getLocale();
		String subject = messageSource.getMessage(subjectCode, null, locale);
		String content = messageSource.getMessage(contentCode, args, locale);

		send(recipient, subject, content);
	}
}
