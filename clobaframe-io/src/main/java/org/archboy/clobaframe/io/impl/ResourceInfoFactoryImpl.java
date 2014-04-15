package org.archboy.clobaframe.io.impl;

import java.io.InputStream;
import java.util.Date;
import javax.inject.Named;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.ResourceInfoFactory;
import org.springframework.util.Assert;

/**
 *
 * @author arch
 */
@Named
public class ResourceInfoFactoryImpl implements ResourceInfoFactory {

	@Override
	public ResourceInfo make(byte[] content, String contentType, Date lastModified) {
		Assert.notNull(content);
		Assert.hasText(contentType);
		Assert.notNull(lastModified);
		
		return new ByteArrayResourceInfo(content, contentType, lastModified);
	}

	@Override
	public ResourceInfo make(InputStream inputStream, long contentLength, String contentType, Date lastModified) {
		Assert.notNull(inputStream);
		Assert.isTrue(contentLength > 0);
		Assert.hasText(contentType);
		Assert.notNull(lastModified);
		
		return new InputStreamResourceInfo(inputStream, contentLength, contentType, lastModified);
	}
}
