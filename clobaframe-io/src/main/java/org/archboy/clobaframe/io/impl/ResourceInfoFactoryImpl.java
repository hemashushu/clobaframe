package org.archboy.clobaframe.io.impl;

import org.archboy.clobaframe.io.file.impl.FileResourceInfo;
import java.io.File;
import java.io.InputStream;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.archboy.clobaframe.io.ContentTypeDetector;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.ResourceInfoFactory;

/**
 *
 * @author arch
 */
@Component
public class ResourceInfoFactoryImpl implements ResourceInfoFactory {

	@Autowired
	private ContentTypeDetector contentTypeAnalyzer;

	@Override
	public ResourceInfo make(File file) {
		return new FileResourceInfo(file, contentTypeAnalyzer);
	}

	@Override
	public ResourceInfo make(byte[] content, String contentType, Date lastModified) {
		return new ByteArrayResourceInfo(content, contentType, lastModified);
	}

	@Override
	public ResourceInfo make(InputStream inputStream, long contentLength, String contentType, Date lastModified) {
		return new InputStreamResourceInfo(inputStream, contentLength, contentType, lastModified);
	}
}
