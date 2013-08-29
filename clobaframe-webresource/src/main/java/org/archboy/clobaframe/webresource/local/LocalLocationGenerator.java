package org.archboy.clobaframe.webresource.local;


import org.springframework.beans.factory.annotation.Value;
import javax.inject.Named;
import org.archboy.clobaframe.webresource.LocationGenerator;
import org.archboy.clobaframe.webresource.WebResourceInfo;

/**
 *
 * @author young
 */
@Named
public class LocalLocationGenerator implements LocationGenerator {

	@Value("${webresource.local.location}")
	private String localLocationPrefix;

	@Override
	public String getLocation(WebResourceInfo webResource) {
		return localLocationPrefix + webResource.getUniqueName();
	}

}
