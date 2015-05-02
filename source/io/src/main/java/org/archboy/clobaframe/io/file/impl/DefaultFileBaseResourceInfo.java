package org.archboy.clobaframe.io.file.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.archboy.clobaframe.io.MimeTypeDetector;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;

public class DefaultFileBaseResourceInfo implements FileBaseResourceInfo {

	private File file;
	private String mimeType;

	public DefaultFileBaseResourceInfo(File file, String mimeType) {
		this.file = file;
		this.mimeType = mimeType;
	}

	@Override
	public long getContentLength() {
		// get the length just in time.
		return file.length();
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public InputStream getContent() throws IOException {
		return new FileInputStream(file);
	}

	@Override
	public InputStream getContent(long start, long length) throws IOException {
		return new PartialFileInputStream(file, start, length);
	}

	@Override
	public boolean isSeekable() {
		return true;
	}

	@Override
	public Date getLastModified() {
		// get the last modified time just in time.
		return new Date(file.lastModified());
	}

	@Override
	public File getFile() {
		return file;
	}
	
}
