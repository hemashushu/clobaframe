package org.archboy.clobaframe.io.http.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.apache.commons.fileupload.FileItem;
import org.archboy.clobaframe.io.http.MultipartFormResourceInfo;

/**
 *
 * @author yang
 */
public class DefaultMultipartFormResourceInfo implements MultipartFormResourceInfo{

	private FileItem fileItem;

	public DefaultMultipartFormResourceInfo(FileItem fileItem){ 
		this.fileItem = fileItem;
	}

	@Override
	public boolean isFormField() {
		return fileItem.isFormField();
	}

	@Override
	public boolean isFileField() {
		return !fileItem.isFormField();
	}

	@Override
	public String getFileName() {
		return fileItem.getName();
	}

	@Override
	public String getContentAsString() {
		return fileItem.getString();
	}

	@Override
	public long getContentLength() {
		return fileItem.getSize();
	}

	@Override
	public String getMimeType() {
		return fileItem.getContentType();
	}

	@Override
	public InputStream getContent() throws IOException {
		return fileItem.getInputStream();
	}

	@Override
	public InputStream getContent(long start, long length) throws IOException {
		throw new UnsupportedOperationException("Does not supported.");
	}

	@Override
	public boolean isSeekable() {
		return false;
	}

	@Override
	public String getFieldName() {
		return fileItem.getFieldName();
	}

	@Override
	public Date getLastModified() {
		return new Date();
	}
}
