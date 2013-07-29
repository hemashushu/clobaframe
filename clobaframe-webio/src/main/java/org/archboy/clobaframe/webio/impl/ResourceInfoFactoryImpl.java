package org.archboy.clobaframe.webio.impl;

import java.io.File;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.archboy.clobaframe.webio.ContentTypeAnalyzer;
import org.archboy.clobaframe.webio.ResourceInfo;
import org.archboy.clobaframe.webio.ResourceInfoFactory;

/**
 *
 * @author arch
 */
@Component
public class ResourceInfoFactoryImpl implements ResourceInfoFactory {

	@Autowired
	private ContentTypeAnalyzer contentTypeAnalyzer;

	@Override
	public ResourceInfo make(File file) {
		return new FileResourceInfo(file, contentTypeAnalyzer);
	}

	@Override
	public ResourceInfo make(byte[] content, String contentType, Date lastModified) {
		return new ByteArrayResourceInfo(content, contentType, lastModified);
	}
}
