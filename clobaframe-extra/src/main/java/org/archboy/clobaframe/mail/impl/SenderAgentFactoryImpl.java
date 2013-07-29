package org.archboy.clobaframe.mail.impl;

import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.archboy.clobaframe.mail.SenderAgent;
import org.archboy.clobaframe.mail.SenderAgentFactory;

/**
 *
 * @author arch
 */
@Component
public class SenderAgentFactoryImpl implements SenderAgentFactory{

	private static final String DEFAULT_SENDER_AGENT_NAME = "null";

	@Value("${mail.agent}")
	private String senderAgentName = DEFAULT_SENDER_AGENT_NAME;

	private final Logger logger = LoggerFactory.getLogger(SenderAgentFactoryImpl.class);

	@Autowired
	private List<SenderAgent> senderAgents;

	// the default sender agent
	private SenderAgent senderAgent;

	@PostConstruct
	public void init() {
		// get the config agent
		for(SenderAgent agent : senderAgents){
			if (agent.getName().equals(senderAgentName)){
				senderAgent = agent;
				break;
			}
		}

		Assert.notNull(senderAgent, "The specify gmail agent not found.");
		logger.info("Using the [{}] mail sender agent as the default.", senderAgentName);
	}

	@Override
	public List<SenderAgent> getSenderAgents() {
		return senderAgents;
	}

	@Override
	public SenderAgent getSenderAgent() {
		return senderAgent;
	}

	@Override
	public SenderAgent getSenderAgentByName(String name) {
		for(SenderAgent agent : senderAgents){
			if (agent.getName().equals(senderAgentName)){
				return agent;
			}
		}

		throw new IllegalArgumentException("The specify gmail agent not found.");
	}
}
