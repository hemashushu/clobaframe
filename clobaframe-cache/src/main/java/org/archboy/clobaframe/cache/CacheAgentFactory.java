package org.archboy.clobaframe.cache;

import java.util.List;

/**
 *
 * @author arch
 */
public interface CacheAgentFactory {

	List<CacheAgent> getCacheAgents();

	CacheAgent getCacheAgent();

	CacheAgent getCacheAgent(String name);
}
