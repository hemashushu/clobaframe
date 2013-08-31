package org.archboy.clobaframe.blobstore.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;
import org.archboy.clobaframe.blobstore.BlobKey;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.io.file.impl.PartialFileInputStream;

/**
 *
 * @author arch
 */
public class LocalBlobResourceInfo implements BlobResourceInfo, FileBaseResourceInfo {

	private BlobKey blobKey;
	private File file;
	private String contentType;

	public LocalBlobResourceInfo(BlobKey blobKey, File file, String contentType) {
		this.blobKey = blobKey;
		this.file = file;
		this.contentType = contentType;
	}

	@Override
	public BlobKey getBlobKey() {
		return blobKey;
	}

	@Override
	public long getContentLength() {
		return file.length();
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public Date getLastModified() {
		return new Date(file.lastModified());
	}

	@Override
	public InputStream getInputStream() throws IOException{
		return new FileInputStream(file);
	}

	@Override
	public InputStream getInputStream(long start, long length) throws IOException{
		return new PartialFileInputStream(file, start, length);
	}

	@Override
	public Map<String, String> getMetadata() {
		throw new UnsupportedOperationException("Does not supported.");
	}

	@Override
	public void addMetadata(String key, String value) {
		throw new UnsupportedOperationException("Does not supported.");
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
