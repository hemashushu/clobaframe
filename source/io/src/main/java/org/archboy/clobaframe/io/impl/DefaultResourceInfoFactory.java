package org.archboy.clobaframe.io.impl;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;
import javax.inject.Named;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.ResourceInfoFactory;
import org.archboy.clobaframe.io.TextResourceInfo;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
public class DefaultResourceInfoFactory implements ResourceInfoFactory {

	@Override
	public ResourceInfo make(byte[] content, String mimeType, Date lastModified) {
		Assert.notNull(content);
		Assert.hasText(mimeType);
		Assert.notNull(lastModified);
		
		return new ByteArrayResourceInfo(content, mimeType, lastModified);
	}

	@Override
	public ResourceInfo make(InputStream inputStream, long contentLength, String mimeType, Date lastModified) {
		Assert.notNull(inputStream);
		Assert.isTrue(contentLength > 0);
		Assert.hasText(mimeType);
		Assert.notNull(lastModified);
		
		return new InputStreamResourceInfo(inputStream, contentLength, mimeType, lastModified);
	}

	@Override
	public TextResourceInfo make(String text, Charset charset, String mimeType, Date lastModified) {
		Assert.hasText(text);
		Assert.notNull(charset);
		Assert.hasText(mimeType);
		Assert.notNull(lastModified);
		
		return new DefaultTextResourceInfo(text, charset, mimeType, lastModified);
	}

}
