package org.archboy.clobaframe.blobstore.impl;

import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;
import org.archboy.clobaframe.blobstore.BlobResourceInfoPartialCollection;
import org.archboy.clobaframe.blobstore.BlobKey;
import org.archboy.clobaframe.blobstore.Blobstore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
@Named
public class BlobstoreImpl implements Blobstore{

	public static final String DEFAULT_ADAPTER_NAME = "local";
	
	// the default blob store client proxy
	private BlobstoreClientAdapter defaultAdapter;

	@Inject
	private List<BlobstoreClientAdapter> blobstoreClientAdapters;

	@Value("${blobstore.agent}")
	private String defaultApapterName = DEFAULT_ADAPTER_NAME;

	private final Logger logger = LoggerFactory.getLogger(BlobstoreImpl.class);

	@PostConstruct
	public void init(){
		defaultAdapter = getBlobstoreClientAdapter(defaultApapterName);
		logger.info("Using [{}] blob agent as the default.", defaultApapterName);
	}

	public List<BlobstoreClientAdapter> getBlobstoreClientAdapters() {
		return blobstoreClientAdapters;
	}

	public BlobstoreClientAdapter getBlobstoreClientAdapter(String name) {
		Assert.hasText(name);
		
		for(BlobstoreClientAdapter clientAdapter : blobstoreClientAdapters){
			if (clientAdapter.getName().equals(defaultApapterName)){
				return clientAdapter;
			}
		}

		throw new IllegalArgumentException(String.format("Blob store client adapter [%s] not found.", name));
	}

	@Override
	public boolean existBucket(String name) {
		Assert.hasText(name);
		return defaultAdapter.existBucket(name);
	}

	@Override
	public void createBucket(String name) throws IOException {
		Assert.hasText(name);
		defaultAdapter.createBucket(name);
	}

	@Override
	public void deleteBucket(String name) throws IOException {
		Assert.hasText(name);
		defaultAdapter.deleteBucket(name);
	}

	@Override
	public void put(BlobResourceInfo blobResourceInfo) throws IOException {
		Assert.notNull(blobResourceInfo);
		put(blobResourceInfo, false, DEFAULT_STORE_PRIOTITY);
	}

	@Override
	public void put(BlobResourceInfo blobResourceInfo, boolean publicReadable, int priority) throws IOException {
		Assert.notNull(blobResourceInfo);
		defaultAdapter.put(blobResourceInfo, publicReadable, priority);
	}

	@Override
	public BlobResourceInfo get(BlobKey blobKey) throws IOException {
		Assert.notNull(blobKey);
		return defaultAdapter.get(blobKey);
	}

	@Override
	public void delete(BlobKey blobKey) throws IOException {
		Assert.notNull(blobKey);
		defaultAdapter.delete(blobKey);
	}

	@Override
	public BlobResourceInfoPartialCollection list(BlobKey prefix) {
		Assert.notNull(prefix);
		return defaultAdapter.list(prefix);
	}

	@Override
	public BlobResourceInfoPartialCollection listNext(BlobResourceInfoPartialCollection collection) {
		Assert.notNull(collection);
		return defaultAdapter.listNext(collection);
	}

}
