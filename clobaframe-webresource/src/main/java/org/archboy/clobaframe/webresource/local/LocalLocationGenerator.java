package org.archboy.clobaframe.webresource.local;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.archboy.clobaframe.webresource.LocationGenerator;
import org.archboy.clobaframe.webresource.WebResourceInfo;

/**
 *
 * @author young
 */
@Component
public class LocalLocationGenerator implements LocationGenerator {

	@Value("${webresource.local.location}")
	private String localLocationPrefix;

	@Override
	public String getLocation(WebResourceInfo webResource) {
		return localLocationPrefix + webResource.getUniqueName();
	}

}
