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

	//private boolean wrapped;
	
	private File file;
	private long contentLength;
	private String contentType;
	private Date lastModified;

	public TemporaryFileBaseResourceInfo(
			ResourceInfo resourceInfo, 
			TemporaryResources temporaryResources) throws IOException {
		
		this.contentType = resourceInfo.getContentType();
		this.contentLength = resourceInfo.getContentLength();
		this.lastModified = resourceInfo.getLastModified();
			
		if (resourceInfo instanceof FileBaseResourceInfo){
			//this.wrapped = false;
			this.file = ((FileBaseResourceInfo)resourceInfo).getFile();
		}else{
			// write all data into a temporary file.
			//this.wrapped = true;
			//this.file = File.createTempFile("clobaframe-io-", ".tmp");
			this.file = temporaryResources.createTemporaryFile();
			
			InputStream in = resourceInfo.getInputStream();
			OutputStream out = new FileOutputStream(file);
			IOUtils.copy(in, out);
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(in);
			
			//temporaryResourcesAutoCleaner.track(file, this);
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
	public Date getLastModified() {
		return lastModified;
	}
}