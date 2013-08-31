package org.archboy.clobaframe.blobstore.local;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import javax.inject.Named;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;
import org.archboy.clobaframe.blobstore.BlobResourceInfoPartialCollection;
import org.archboy.clobaframe.blobstore.BlobKey;
import org.archboy.clobaframe.blobstore.StoreAgent;

/**
 *
 * Local blob store for test and development only.
 *
 * @author arch
 */
@Named
public class LocalStoreAgentImpl implements StoreAgent {

	@Inject
	private ResourceLoader resourceLoader;

	@Value("${blobstore.local.path}")
	private String localPath;

	@Value("${blobstore.local.autoCreateFolder}")
	private boolean autoCreateFolder;

	private File localDir;

	@PostConstruct
	public void init() throws IOException {
		// check the repository directory.
		Resource resource = resourceLoader.getResource(localPath);
		localDir = resource.getFile();
		if (!localDir.exists() && autoCreateFolder){
			localDir.mkdirs();
		}
	}

	@Override
	public String getName() {
		return "local";
	}

	@Override
	public boolean existBucket(String name) {
		File bucket = new File(localDir, name);
		return (bucket.exists() && bucket.isDirectory());
	}

	@Override
	public void createBucket(String name) throws IOException {
		File bucket = new File(localDir, name);
		if (bucket.exists() && bucket.isDirectory()) {
			return;
		}

		boolean result = bucket.mkdir();
		if (!result) {
			throw new IOException("Create bucket failed.");
		}
	}

	@Override
	public void deleteBucket(String name) throws IOException {
		File bucket = new File(localDir, name);
		if (!bucket.exists()) {
			return;
		}

		boolean result = bucket.delete();
		if (!result) {
			throw new IOException("Delete bucket failed.");
		}
	}

	@Override
	public void put(BlobResourceInfo blobResourceInfo, boolean publicReadable, boolean minor) throws IOException {
		BlobKey blobKey = blobResourceInfo.getBlobKey();
		File bucket = new File(localDir, blobKey.getBucketName());
		File file = new File(bucket, blobKey.getKey());

		//ResourceContent content = blobInfo.getContentSnapshot();
		InputStream in = blobResourceInfo.getInputStream(); // content.getInputStream();
		FileOutputStream out = new FileOutputStream(file);
		IOUtils.copy(in, out);

		IOUtils.closeQuietly(out);
		IOUtils.closeQuietly(in);
	}

	@Override
	public BlobResourceInfo get(BlobKey blobKey) throws IOException {
		File bucket = new File(localDir, blobKey.getBucketName());
		File file = new File(bucket, blobKey.getKey());
		if (!file.exists() || file.isDirectory()) {
			throw new FileNotFoundException(
					String.format("File [%s] not found in bucket [%s].",
					blobKey.getKey(), blobKey.getBucketName()));
		}

		String contentType = getContentType(file);
		return new LocalBlobResourceInfo(blobKey, file, contentType);
	}

	@Override
	public void delete(BlobKey blobKey) throws IOException {
		File bucket = new File(localDir, blobKey.getBucketName());
		File file = new File(bucket, blobKey.getKey());

		if (!file.exists()){
			return;
		}

		boolean result = file.delete();
		if (!result) {
			throw new IOException("Delete blob failed.");
		}
	}

	@Override
	public BlobResourceInfoPartialCollection list(BlobKey prefix) {
		String bucketName = prefix.getBucketName();
		File bucket = new File(localDir, bucketName);
		final String startName = prefix.getKey();

		File[] files = bucket.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return (startName == null || name.startsWith(startName));
			}
		});

		LocalBlobResourceInfoPartialCollection collection = new LocalBlobResourceInfoPartialCollection();
		for (File file : files) {
			BlobKey blobKey = new BlobKey(bucketName, file.getName());
			String contentType = getContentType(file);
			collection.add(new LocalBlobResourceInfo(blobKey, file, contentType));
		}

		return collection;
	}

	@Override
	public BlobResourceInfoPartialCollection listNext(BlobResourceInfoPartialCollection collection) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	private String getContentType(File file){
		// does not support the specify content type yet.
		return "application/octet-stream";
	}
}
