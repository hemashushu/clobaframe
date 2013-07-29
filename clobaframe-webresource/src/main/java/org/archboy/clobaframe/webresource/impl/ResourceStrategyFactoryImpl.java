/*
 * Copyright 2011 Spark Young (sparkyoungs@gmail.com). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.archboy.clobaframe.webresource.impl;

import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.archboy.clobaframe.webresource.ResourceStrategy;
import org.archboy.clobaframe.webresource.ResourceStrategyFactory;

/**
 *
 * @author young
 */
@Component
public class ResourceStrategyFactoryImpl implements ResourceStrategyFactory{

	@Autowired
	private List<ResourceStrategy> resourceStrategies;

	@Value("${webresource.strategy}")
	private String strategyName;

	// the default strategy
	private ResourceStrategy resourceStrategy;

	private final Logger logger = LoggerFactory.getLogger(ResourceStrategyFactoryImpl.class);

	@PostConstruct
	public void init(){
		// get the config strategy
		for(ResourceStrategy strategy : resourceStrategies){
			if (strategy.getName().equals(strategyName)){
				resourceStrategy = strategy;
				break;
			}
		}

		Assert.notNull(resourceStrategy, "Web resource strategy not found.");

		logger.info("Using [{}] resource strategy as the default.", strategyName);
	}

	@Override
	public ResourceStrategy getResourceStrategy() {
		return resourceStrategy;
	}

	@Override
	public ResourceStrategy getResourceStrategy(String name) {
		for(ResourceStrategy strategy : resourceStrategies){
			if (strategy.getName().equals(name)){
				return strategy;
			}
		}

		throw new IllegalArgumentException("The specify web resource strategy not found.");
	}

	@Override
	public List<ResourceStrategy> getResourceStrategies() {
		return resourceStrategies;
	}

}
