package org.archboy.clobaframe.mail.smtp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.mail.SendMailException;
import org.archboy.clobaframe.mail.impl.AbstractMailSender;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;


/**
 *
 * @author yang
 */
@Named
public class SmtpMailSender extends AbstractMailSender {

	private static final int DEFAULT_PORT = 25; // smtp standard port, the port with TLS usually is 587.
	private static final boolean DEFAULT_TLS = false; // do not use TLS by default.
	
	private String host;
	private int port = DEFAULT_PORT;
	private boolean tls = DEFAULT_TLS;
	private String loginName;
	private String loginPassword;
	private String fromAddress;

	private static final String DEFAULT_SMTP_CONFIG = "classpath:org/archboy/clobaframe/mail/smtp/config-example.properties";
	
	@Value("${clobaframe.mail.smtp.config}")
	private String smtpConfig = DEFAULT_SMTP_CONFIG;
	
	@Inject
	private ResourceLoader resourceLoader;
	
	private final Logger logger = LoggerFactory.getLogger(SmtpMailSender.class);

	@PostConstruct
	public void init() throws IOException{
		Resource resource = resourceLoader.getResource(smtpConfig);
		if (!resource.exists()){
			throw new FileNotFoundException(String.format(
					"Can not find the smtp config file [%s].",
					smtpConfig));
		}

		Properties properties = new Properties();
		InputStream in = null;
		
		try{
			in = resource.getInputStream();
			properties.load(in);
			
			this.host = properties.getProperty("host");
			this.port = Integer.parseInt(properties.getProperty("port"));
			this.tls = Boolean.parseBoolean(properties.getProperty("tls"));
			this.loginName = properties.getProperty("loginName");
			this.loginPassword = properties.getProperty("loginPassword");
			this.fromAddress = properties.getProperty("fromAddress");

		}finally{
			IOUtils.closeQuietly(in);
		}
	}
	
	@Override
	public String getName() {
		return "smtp";
	}

	@Override
	public void send(String recipient, String subject, String content) throws SendMailException {
		Assert.hasText(recipient);
		Assert.hasText(subject);
		Assert.hasText(content);
		
		Email email = new SimpleEmail();
		email.setHostName(host);
		email.setAuthenticator(new DefaultAuthenticator(loginName, loginPassword));
		email.setSmtpPort(port);
		email.setTLS(tls);

		try {
			email.setCharset("UTF-8"); // specify the charset.
			email.setFrom(fromAddress);
			email.setSubject(subject);
			email.setMsg(content);
			email.addTo(recipient);
			email.send();
		} catch (EmailException e) {
			throw new SendMailException(
					String.format("Failed to send mail to %s.", recipient), e);

		}
	}

	@Override
	public void sendWithHtml(String recipient, String subject, String content) throws SendMailException {
		Assert.hasText(recipient);
		Assert.hasText(subject);
		Assert.hasText(content);
		
		HtmlEmail email = new HtmlEmail();
		email.setHostName(host);
		email.setAuthenticator(new DefaultAuthenticator(loginName, loginPassword));
		email.setSmtpPort(port);
		email.setTLS(tls);

		try {
			email.setCharset("UTF-8"); // specify the charset.
			email.setFrom(fromAddress);
			email.setSubject(subject);
			email.setHtmlMsg(content);
			//email.setMsg(""); // can set plain text either
			email.addTo(recipient);
			email.send();
		} catch (EmailException e) {
			throw new SendMailException(
					String.format("Failed to send mail to %s.", recipient), e);
		}
	}
	
}
