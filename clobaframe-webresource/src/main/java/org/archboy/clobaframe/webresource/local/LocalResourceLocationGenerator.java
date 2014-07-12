package org.archboy.clobaframe.webresource.local;


import org.springframework.beans.factory.annotation.Value;
import javax.inject.Named;
import org.archboy.clobaframe.webresource.ResourceLocationGenerator;
import org.archboy.clobaframe.webresource.WebResourceInfo;

/**
 *
 * @author yang
 */
public class LocalResourceLocationGenerator implements ResourceLocationGenerator {

	private String localLocationPrefix;

	public LocalResourceLocationGenerator(String localLocationPrefix) {
		this.localLocationPrefix = localLocationPrefix;
	}
	
	@Override
	public String getLocation(WebResourceInfo webResource) {
		return localLocationPrefix + webResource.getUniqueName();
	}

}
