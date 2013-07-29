package org.archboy.clobaframe.blobstore;

import java.util.List;

/**
 *
 * @author arch
 */
public interface StoreAgentFactory {

	/**
	 * Get the default blob store agent.
	 *
	 * @return
	 */
	StoreAgent getStoreAgent();

	StoreAgent getStoreAgent(String name);

	List<StoreAgent> getStoreAgents();

}
