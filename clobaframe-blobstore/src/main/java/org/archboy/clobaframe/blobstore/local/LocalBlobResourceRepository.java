package org.archboy.clobaframe.blobstore.local;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;
import org.archboy.clobaframe.blobstore.BlobResourceRepository;
import org.archboy.clobaframe.blobstore.PartialCollection;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
public class LocalBlobResourceRepository implements BlobResourceRepository, Closeable{

	private String name;
	private File bucketDir;
	
	private DB db;
	
	public LocalBlobResourceRepository(String name, File indexFile,  File bucketDir) {
		this.name = name;
		this.bucketDir = bucketDir;
		
		db = DBMaker.newFileDB(indexFile)
           //.closeOnJvmShutdown()
           .make();
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void close() throws IOException {
		// close meta db
		db.close();
	}
	
	@Override
	public void put(BlobResourceInfo blobResourceInfo) throws IOException {
		Assert.notNull(blobResourceInfo);
		
		String key = blobResourceInfo.getKey();
		File file = new File(bucketDir, key);

		// copy content
		InputStream in = blobResourceInfo.getContent();
		FileOutputStream out = new FileOutputStream(file);
		IOUtils.copy(in, out);

		IOUtils.closeQuietly(out);
		IOUtils.closeQuietly(in);
		
		// write meta data
	}
	
	@Override
	public void put(BlobResourceInfo blobResourceInfo, boolean publicReadable, int priority) throws IOException {
		put(blobResourceInfo);
	}

	@Override
	public BlobResourceInfo get(String key) throws IOException {
		Assert.notNull(key);
		
		File file = new File(bucketDir, key);
		if (!file.exists() || file.isDirectory()) {
			throw new FileNotFoundException(
					String.format("File [%s] not found in bucket [%s].",
					key, name));
		}

		// read meta
		return new LocalBlobResourceInfo(name, key, file, null, null, null);
	}

	@Override
	public void delete(String key) throws IOException {
		Assert.notNull(key);
		
		File file = new File(bucketDir, key);

		if (!file.exists()){
			return;
		}

		boolean result = file.delete();
		if (!result) {
			throw new IOException("Delete blob failed.");
		}
	}

	@Override
	public PartialCollection<BlobResourceInfo> list() {
//		File[] files = bucket.listFiles(new FilenameFilter() {
//			@Override
//			public boolean accept(File dir, String name) {
//				return (startName == null || name.startsWith(startName));
//			}
//		});
//
//		for (File file : files) {
//			BlobKey blobKey = new BlobKey(bucketName, file.getName());
//			String contentType = getContentType(file);
//			collection.add(new LocalBlobResourceInfo(blobKey, file, contentType));
//		}
		
		PartialArrayList<BlobResourceInfo> infos = new PartialArrayList<BlobResourceInfo>(
				new ArrayList<BlobResourceInfo>(), false);
		return infos;
	}

	@Override
	public PartialCollection<BlobResourceInfo> listNext(PartialCollection<BlobResourceInfo> collection) {
		if (!collection.hasMore()) {
			throw new IllegalArgumentException("There is no more items.");
		}
		
		return new PartialArrayList<BlobResourceInfo>(
				new ArrayList<BlobResourceInfo>(), false);
	}
	
}
