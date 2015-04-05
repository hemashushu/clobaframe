package org.archboy.clobaframe.mail;

/**
 *
 * @author yang
 */
public interface MailSenderManager {
	
	/**
	 * 
	 * @return 
	 */
	MailSender getDefault();
	
	/**
	 * 
	 * @param name The implementation name.
	 * @return 
	 */
	MailSender getMailSender(String name);
}
