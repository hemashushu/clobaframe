package org.archboy.clobaframe.blobstore.impl;

import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;
import org.archboy.clobaframe.blobstore.BlobResourceInfoPartialCollection;
import org.archboy.clobaframe.blobstore.BlobKey;
import org.archboy.clobaframe.blobstore.Blobstore;
import org.archboy.clobaframe.blobstore.StoreAgent;
import org.archboy.clobaframe.blobstore.StoreAgentFactory;

/**
 *
 * @author arch
 */
@Named
public class BlobstoreImpl implements Blobstore{

	@Inject
	private StoreAgentFactory storeAgentFactory;

	private StoreAgent storeAgent;

	@PostConstruct
	public void init() {
		storeAgent = storeAgentFactory.getStoreAgent();
	}

	@Override
	public boolean existBucket(String name) {
		return storeAgent.existBucket(name);
	}

	@Override
	public void createBucket(String name) throws IOException {
		storeAgent.createBucket(name);
	}

	@Override
	public void deleteBucket(String name) throws IOException {
		storeAgent.deleteBucket(name);
	}

	@Override
	public void put(BlobResourceInfo blobInfo) throws IOException {
		put(blobInfo, false, false);
	}

	@Override
	public void put(BlobResourceInfo blobInfo, boolean publicReadable, boolean minor) throws IOException {
		storeAgent.put(blobInfo, publicReadable, minor);
	}

	@Override
	public BlobResourceInfo get(BlobKey blobKey) throws IOException {
		return storeAgent.get(blobKey);
	}

	@Override
	public void delete(BlobKey blobKey) throws IOException {
		storeAgent.delete(blobKey);
	}

	@Override
	public BlobResourceInfoPartialCollection list(BlobKey prefix) {
		return storeAgent.list(prefix);
	}

	@Override
	public BlobResourceInfoPartialCollection listNext(BlobResourceInfoPartialCollection collection) {
		return storeAgent.listNext(collection);
	}

}
