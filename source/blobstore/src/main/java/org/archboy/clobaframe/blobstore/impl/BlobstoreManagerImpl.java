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

	public static final String DEFAULT_BLOBSTORE_NAME = "local";
	
	public static final String SETTING_KEY_DEFAULT_BLOBSTORE_NAME = "clobaframe.blobstore.default";
	
	@Value("${" + SETTING_KEY_DEFAULT_BLOBSTORE_NAME + ":" + DEFAULT_BLOBSTORE_NAME + "}")
	private String defaultBlobstoreName;
	
	@Inject
	private List<Blobstore> blobstores;

	public void setDefaultBlobstoreName(String defaultBlobstoreName) {
		this.defaultBlobstoreName = defaultBlobstoreName;
	}

	public void setBlobstores(List<Blobstore> blobstores) {
		this.blobstores = blobstores;
	}
	
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
