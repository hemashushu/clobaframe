package org.archboy.clobaframe.webresource.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.io.file.impl.PartialFileInputStream;
import org.archboy.clobaframe.webresource.WebResourceInfo;

public class DefaultWebResourceInfo implements WebResourceInfo, FileBaseResourceInfo {

	private File file;

	private String name;
	private String uniqueName;
	private String contentType;

	public DefaultWebResourceInfo(
			File file, String name, String contentType) {
		this.file = file;
		this.name = name;
		this.contentType = contentType;
	}

	@Override
	public String getHash() {
		String hash = null;
		InputStream in = null;
		try {
			in = getInputStream();
			hash = DigestUtils.sha256Hex(in);
		}catch(IOException e){
			// ignore
		}finally{
			IOUtils.closeQuietly(in);
		}
		return hash;
	}

	@Override
	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}
	
	@Override
	public long getContentLength() {
		// get the length just in time.
		return file.length();
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

	@Override
	public InputStream getInputStream(long start, long length) throws IOException {
		return new PartialFileInputStream(file, start, length);
	}

	@Override
	public boolean isSeekable() {
		return true;
	}

	@Override
	public String getName() {
		return name;
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
