package org.archboy.clobaframe.mail;


import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author yang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class MailSenderTest {

	@Inject
	@Named("defaultMailSender")
	private MailSender mailSender;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSend() throws SendMailException  {
		// check by manual.
		mailSender.send("hippospark@gmail.com", 
				"Hello world!", 
				"Test.\n Send by clobaframe-mail.");
	}

	@Test
	public void testSendWithHtml() throws SendMailException {
		// check by manual.
		mailSender.sendWithHtml("hippospark@gmail.com", 
				"Hello world!", 
				"<strong>Test.</strong>\n Send by clobaframe-mail.");
	}
	
	@Test
	public void testSendWithTemplate() throws SendMailException {
		// check by manual.
		mailSender.sendWithTemplate("hippospark@gmail.com", 
				"hello", 
				new Object[]{"Hippo", new Date()});
	}
	
}
