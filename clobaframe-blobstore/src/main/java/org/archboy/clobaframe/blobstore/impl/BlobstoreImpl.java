package org.archboy.clobaframe.blobstore.impl;

import java.io.IOException;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.archboy.clobaframe.blobstore.BlobInfo;
import org.archboy.clobaframe.blobstore.BlobInfoPartialCollection;
import org.archboy.clobaframe.blobstore.BlobKey;
import org.archboy.clobaframe.blobstore.Blobstore;
import org.archboy.clobaframe.blobstore.StoreAgent;
import org.archboy.clobaframe.blobstore.StoreAgentFactory;

/**
 *
 * @author arch
 */
@Component
public class BlobstoreImpl implements Blobstore{

	@Autowired
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
	public void put(BlobInfo blobInfo) throws IOException {
		put(blobInfo, false, false);
	}

	@Override
	public void put(BlobInfo blobInfo, boolean publicReadable, boolean minor) throws IOException {
		storeAgent.put(blobInfo, publicReadable, minor);
	}

	@Override
	public BlobInfo get(BlobKey blobKey) throws IOException {
		return storeAgent.get(blobKey);
	}

	@Override
	public void delete(BlobKey blobKey) throws IOException {
		storeAgent.delete(blobKey);
	}

	@Override
	public BlobInfoPartialCollection list(BlobKey prefix) {
		return storeAgent.list(prefix);
	}

	@Override
	public BlobInfoPartialCollection listNext(BlobInfoPartialCollection collection) {
		return storeAgent.listNext(collection);
	}

}
