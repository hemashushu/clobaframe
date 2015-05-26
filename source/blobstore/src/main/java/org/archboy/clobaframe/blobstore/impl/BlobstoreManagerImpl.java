package org.archboy.clobaframe.blobstore.impl;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.blobstore.Blobstore;
import org.archboy.clobaframe.blobstore.BlobstoreManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
@Named
public class BlobstoreManagerImpl implements BlobstoreManager {

	private static final String DEFAULT_BLOBSTORE_NAME = "local";

	@Value("${clobaframe.blobstore.default:" + DEFAULT_BLOBSTORE_NAME + "}")
	private String defaultBlobstoreName;
	
	@Inject
	private List<Blobstore> blobstores;
	
	@Override
	public Blobstore getBlobstore(String name) {
		Assert.hasText(name);
		
		for (Blobstore blobstore : blobstores){
			if (blobstore.getName().equals(name)) {
				return blobstore;
			}
		}

		throw new IllegalArgumentException(
				String.format("Can not find the specify blobstore implementation [%s].", name));
	}

	//@Bean(name="defaultBlobstore")
	@Override
	public Blobstore getDefault() {
		return getBlobstore(defaultBlobstoreName);
	}
	
}
