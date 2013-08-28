package org.archboy.clobaframe.io.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import org.springframework.util.Assert;
import org.archboy.clobaframe.io.ResourceContent;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author arch
 */
public class ByteArrayResourceInfo implements ResourceInfo {

	private long contentLength;
	private String contentType;
	private Date lastModified;
	private byte[] content;

	public ByteArrayResourceInfo(byte[] content,
			String contentType,
			Date lastModified) {
		this.contentLength = content.length;
		this.contentType = contentType;
		this.lastModified = lastModified;
		this.content = content;
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
	public ResourceContent getContentSnapshot() throws IOException {
		return new DefaultResourceContent(content);
	}

	@Override
	public ResourceContent getContentSnapshot(long start, long length) throws IOException {
//		Assert.isTrue(start>=0);
//		Assert.isTrue(length>0);
//		Assert.isTrue(start + length <= contentLength);
//		byte[] partialData = Arrays.copyOfRange(data, (int)start, (int)(start + length));
//		return new DefaultResourceContent(partialData);

		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				content, (int)start, (int)length);
		return new DefaultResourceContent(inputStream, length);
	}

	@Override
	public boolean isContentSeekable() {
		return true;
	}

	@Override
	public Date getLastModified() {
		return lastModified;
	}
}