package org.archboy.clobaframe.mail;

import java.util.List;

/**
 *
 * @author arch
 */
public interface SenderAgentFactory {

	List<SenderAgent> getSenderAgents();

	/**
	 * Get the default mail sender agent.
	 * @return
	 */
	SenderAgent getSenderAgent();

	/**
	 * Get the specify mail sender agent by name.
	 *
	 * @param name
	 * @return
	 */
	SenderAgent getSenderAgentByName(String name);
}
