package org.archboy.clobaframe.blobstore.local;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import javax.inject.Named;
import org.archboy.clobaframe.blobstore.BlobResourceRepository;
import org.archboy.clobaframe.blobstore.impl.AbstractBlobstore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 *
 * Local blob store.
 * for test and development, or low demand environment.
 *
 * @author yang
 */
@Named
public class LocalBlobstore extends AbstractBlobstore {

	@Inject
	private ResourceLoader resourceLoader;

	@Value("${clobaframe.blobstore.local.path}")
	private String localPath;

	@Value("${clobaframe.blobstore.local.autoCreateRootFolder}")
	private boolean autoCreateRootFolder;

	// local blobstore root dir
	private File rootDir;

	private Map<String, BlobResourceRepository> repositories = new HashMap<String, BlobResourceRepository>();
	
	private final Logger logger = LoggerFactory.getLogger(LocalBlobstore.class);
	
	@PostConstruct
	public void init() throws IOException {
		// check the repository directory.
		Resource resource = resourceLoader.getResource(localPath);
		rootDir = resource.getFile();
		if (!rootDir.exists() && autoCreateRootFolder){
			rootDir.mkdirs();
		}
	}
	
	@PreDestroy
	public void destory(){
		for(BlobResourceRepository r : repositories.values()){
			try{
				((Closeable)r).close();
			}catch(IOException e){
				logger.error("Close blob resource repository {} failed, message: {}", 
						r.getName(), e.getMessage());
				// ignore
			}
		}
	}

	@Override
	public String getName() {
		return "local";
	}

	@Override
	public boolean exist(String bucketName) {
		Assert.hasText(bucketName);
		File bucket = new File(rootDir, bucketName);
		return (bucket.exists() && bucket.isDirectory());
	}

	@Override
	public void create(String bucketName) throws IOException {
		Assert.hasText(bucketName);
		
		File bucket = new File(rootDir, bucketName);
		if (bucket.exists() && bucket.isDirectory()) {
			return;
		}

		boolean result = bucket.mkdir();
		if (!result) {
			throw new IOException(
					String.format("Create bucket [%s] failed.", bucketName));
		}
	}

	@Override
	public void delete(String bucketName) throws IOException {
		Assert.hasText(bucketName);
		
		// close repository first
		BlobResourceRepository repository = repositories.get(bucketName);
		if (repository != null){
			((Closeable)repository).close();
		}
		
		// delete index file
		File indexFile = getIndexFile(bucketName);
		if (indexFile.exists()) {
			if (!indexFile.delete()){
				throw new IOException(
						String.format("Delete bucket [%s] index failed.", bucketName));
			}
		}
		
		// delete folder
		File bucket = new File(rootDir, bucketName);
		if (bucket.exists()) {
			if (!bucket.delete()) {
				throw new IOException(
						String.format("Delete bucket [%s] failed.", bucketName));
			}
		}
		
		repositories.remove(bucketName);
	}

	@Override
	public BlobResourceRepository getRepository(String bucketName) throws IOException {
		Assert.hasText(bucketName);
		
		BlobResourceRepository exists = repositories.get(bucketName);
		if (exists != null) {
			return exists;
		}
		
		File bucket = new File(rootDir, bucketName);
		
		if (!(bucket.exists() && bucket.isDirectory())){
			throw new FileNotFoundException(
					String.format("Bucket [%s] not found.", bucketName));
		}
		
		BlobResourceRepository repository = new LocalBlobResourceRepository(
				bucketName, getIndexFile(bucketName), bucket);
		
		repositories.put(bucketName, repository);
		return repository;
	}
	
	private File getIndexFile(String bucketName){
		return new File(rootDir, bucketName + ".idx");
	}

}
