package org.archboy.clobaframe.webresource.impl;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.webresource.LocationStrategy;
import org.archboy.clobaframe.webresource.VersionStrategy;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

/**
 * The default location generator.
 * The location format: base location + resource version name.
 * 
 * @author yang
 */
@Named
public class DefaultLocationStrategy implements LocationStrategy {

	@Value("${clobaframe.webresource.versionStrategy}")
	private String versionStrategyName;
		
	@Inject
	private List<VersionStrategy> versionStrategys;

	// fields
	private VersionStrategy versionStrategy;
	
	@Value("${clobaframe.webresource.baseLocation}")
	private String baseLocation;

	private final Logger logger = LoggerFactory.getLogger(DefaultLocationStrategy.class);
	
	@PostConstruct
	public void init(){
		
		for(VersionStrategy strategy : versionStrategys) {
			if (strategy.getName().equals(versionStrategyName)) {
				this.versionStrategy = strategy;
				break;
			}
		}
		
		if (versionStrategy == null) {
			throw new IllegalArgumentException(String.format(
					"Can not find the version strategy [%s]", versionStrategyName));
		}
		
		logger.info("Using [{}] web resource version name strategy.", versionStrategyName);
	}
	
	@Override
	public String getLocation(WebResourceInfo webResourceInfo) {
		String versionName = getVersionName(webResourceInfo);
		return baseLocation + versionName;
	}

	@Override
	public String getName() {
		return "default";
	}

	@Override
	public String getVersionName(WebResourceInfo webResourceInfo) {
		Assert.notNull(webResourceInfo);
		return versionStrategy.getVersionName(webResourceInfo);
	}

	@Override
	public String fromVersionName(String versionName) {
		return versionStrategy.revert(versionName);
	}
	
}
