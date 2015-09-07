package org.archboy.clobaframe.io.file.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;

public class DefaultFileBaseResourceInfo implements FileBaseResourceInfo {

	protected File file;
	protected String mimeType;

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
		Date date = new Date(file.lastModified());
		return date;
	}

	@Override
	public File getFile() {
		return file;
	}
	
}
