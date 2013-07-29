package org.archboy.clobaframe.cache.impl;

import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.archboy.clobaframe.cache.CacheAgent;
import org.archboy.clobaframe.cache.CacheAgentFactory;

/**
 *
 * @author arch
 */
@Component
public class CacheAgentFactoryImpl implements CacheAgentFactory {

	private static final String DEFAULT_CACHE_AGENT_NAME = "null";

	@Value("${cache.agent}")
	private String cacheAgentName = DEFAULT_CACHE_AGENT_NAME;

	// the default cache agent
	private CacheAgent cacheAgent;

	private final Logger logger = LoggerFactory.getLogger(CacheAgentFactoryImpl.class);

	@Autowired
	private List<CacheAgent> cacheAgents;

	@PostConstruct
	public void init(){
		for (CacheAgent agent : cacheAgents){
			if (agent.getName().equals(cacheAgentName)){
				cacheAgent = agent;
				break;
			}
		}

		Assert.notNull(cacheAgent, "The specify cache agent not found.");

		logger.info("Using [{}] cache agent as the default.", cacheAgentName);
	}

	@Override
	public List<CacheAgent> getCacheAgents() {
		return cacheAgents;
	}

	@Override
	public CacheAgent getCacheAgent(String name) {
		for (CacheAgent agent : cacheAgents){
			if (agent.getName().equals(name)) {
				return agent;
			}
		}

		throw new IllegalArgumentException("The specify cache agent not found.");
	}

	@Override
	public CacheAgent getCacheAgent() {
		return cacheAgent;
	}

}
