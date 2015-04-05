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
 * local blob store only for testing and development, or the low demand environment.
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
	public boolean exist(String repoName) {
		Assert.hasText(repoName);
		File bucket = new File(rootDir, repoName);
		return (bucket.exists() && bucket.isDirectory());
	}

	@Override
	public void create(String repoName) throws IOException {
		Assert.hasText(repoName);
		
		File repoDir = new File(rootDir, repoName);
		if (repoDir.exists() && repoDir.isDirectory()) {
			// duplicate
			return;
		}

		// create data folder
		boolean result = repoDir.mkdir();
		if (!result) {
			throw new IOException(
					String.format("Create blob repository [%s] failed.", repoName));
		}
		
		// the index file will create later automatically
		
	}

	@Override
	public void delete(String repoName) throws IOException {
		Assert.hasText(repoName);
		
		// close repository first
		BlobResourceRepository repository = repositories.get(repoName);
		if (repository != null){
			((Closeable)repository).close();
		}
		
		// delete index file
		File indexFile = getIndexFile(repoName);
		if (indexFile.exists()) {
			if (!indexFile.delete()){
				throw new IOException(
						String.format("Delete blob repository [%s] index failed.", repoName));
			}
		}
		
		// delete data folder
		File repoDir = new File(rootDir, repoName);
		if (repoDir.exists()) {
			if (!repoDir.delete()) {
				throw new IOException(
						String.format("Delete blob repository [%s] failed.", repoName));
			}
		}
		
		repositories.remove(repoName);
	}

	@Override
	public BlobResourceRepository getRepository(String repoName) {
		Assert.hasText(repoName);
		
		// fetch from cache first
		BlobResourceRepository exists = repositories.get(repoName);
		if (exists != null) {
			return exists;
		}
		
		File repoDir = new File(rootDir, repoName);
		
		if (!(repoDir.exists() && repoDir.isDirectory())){
//			throw new FileNotFoundException(
//					String.format("Bucket [%s] not found.", repoName));
			return null;
		}
		
		File indexFile = getIndexFile(repoName);
		BlobResourceRepository repository = new LocalBlobResourceRepository(
				repoName, indexFile, repoDir);
		
		// add to cache
		repositories.put(repoName, repository);
		return repository;
	}
	
	private File getIndexFile(String repoName){
		return new File(rootDir, repoName + ".idx");
	}

}
