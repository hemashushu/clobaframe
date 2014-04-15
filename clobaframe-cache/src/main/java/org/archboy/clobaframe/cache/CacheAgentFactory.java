package org.archboy.clobaframe.cache;

import java.util.List;

/**
 *
 * @author arch
 */
public interface CacheAgentFactory {

	/**
	 * 
	 * @return 
	 */
	List<CacheAgent> getCacheAgents();

	/**
	 * 
	 * @return 
	 */
	CacheAgent getCacheAgent();

	/**
	 * 
	 * @param name
	 * @return 
	 */
	CacheAgent getCacheAgent(String name);
}
