package org.archboy.clobaframe.mail;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
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
		mailSender.send("who@where.com", "Hello world!", "Test.");
	}

	@Test
	public void testSendWithHtml() throws SendMailException {
		// check by manual.
		mailSender.sendWithHtml("who@where.com", "Hello world!", "<strong>Test.</strong>");
	}
}
