package org.archboy.clobaframe.webresource.sync;


import org.springframework.beans.factory.annotation.Value;
import javax.inject.Named;
import org.archboy.clobaframe.webresource.ResourceLocationGenerator;
import org.archboy.clobaframe.webresource.WebResourceInfo;

/**
 *
 * @author yang
 */
public class BlobstoreLocationGenerator implements ResourceLocationGenerator{

	private String keyNamePrefix;
	private String location;

	public BlobstoreLocationGenerator(String keyNamePrefix, String location) {
		this.keyNamePrefix = keyNamePrefix;
		this.location = location;
	}

	@Override
	public String getLocation(WebResourceInfo webResource) {
		return location + keyNamePrefix + webResource.getUniqueName();
	}

}
