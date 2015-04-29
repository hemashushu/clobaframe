package org.archboy.clobaframe.blobstore.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;
import org.archboy.clobaframe.blobstore.impl.AbstractBlobResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.io.file.impl.PartialFileInputStream;

/**
 *
 * @author yang
 */
public class LocalBlobResourceInfo extends AbstractBlobResourceInfo implements FileBaseResourceInfo {

	private String repositoryName;
	private String key;
	private File file;
	private String mimeType;
	private Date lastModified;
	private Map<String, Object> metadata;

	public LocalBlobResourceInfo(String repositoryName, String key,
			File file, String mimeType, 
			Date lastModified, Map<String, Object> metadata) {
		this.repositoryName = repositoryName;
		this.key = key;
		this.file = file;
		this.mimeType = mimeType;
		this.lastModified = lastModified;
		this.metadata = metadata;
	}
	
	@Override
	public String getRepositoryName() {
		return repositoryName;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public long getContentLength() {
		return file.length();
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public Date getLastModified() {
		return lastModified;
	}

	@Override
	public InputStream getContent() throws IOException{
		return new FileInputStream(file);
	}

	@Override
	public InputStream getContent(long start, long length) throws IOException{
		return new PartialFileInputStream(file, start, length);
	}

	@Override
	public Map<String, Object> getMetadata() {
		return metadata;
	}

	@Override
	public boolean isSeekable() {
		return true;
	}

	@Override
	public File getFile() {
		return file;
	}
}
