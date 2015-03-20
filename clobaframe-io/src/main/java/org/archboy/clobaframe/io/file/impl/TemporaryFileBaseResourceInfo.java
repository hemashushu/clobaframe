package org.archboy.clobaframe.io.file.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.TemporaryResources;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;

/**
 *
 * @author yang
 */
public class TemporaryFileBaseResourceInfo implements FileBaseResourceInfo{

	private File file;
	private long contentLength;
	private String mimeType;
	private Date lastModified;

	public TemporaryFileBaseResourceInfo(
			ResourceInfo resourceInfo, 
			TemporaryResources temporaryResources) throws IOException {
		
		this.mimeType = resourceInfo.getMimeType();
		this.contentLength = resourceInfo.getContentLength();
		this.lastModified = resourceInfo.getLastModified();
			
		if (resourceInfo instanceof FileBaseResourceInfo){
			this.file = ((FileBaseResourceInfo)resourceInfo).getFile();
		}else{
			// write all data into a temporary file.
			this.file = temporaryResources.createTemporaryFile();
			
			InputStream in = resourceInfo.getContent();
			OutputStream out = new FileOutputStream(file);
			IOUtils.copy(in, out);
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(in);
		}
	}
	
	@Override
	public File getFile() {
		return file;
	}

	@Override
	public long getContentLength() {
		return contentLength;
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
		return lastModified;
	}
}
