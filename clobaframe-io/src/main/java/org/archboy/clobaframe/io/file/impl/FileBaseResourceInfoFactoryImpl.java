package org.archboy.clobaframe.io.file.impl;

import java.io.File;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.io.ContentTypeDetector;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.TemporaryResources;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfoFactory;
import org.archboy.clobaframe.io.file.TemporaryResourcesAutoCleaner;

/**
 *
 * @author yang
 */
@Named
public class FileBaseResourceInfoFactoryImpl implements FileBaseResourceInfoFactory {
	
	@Inject
	private ContentTypeDetector contentTypeAnalyzer;

	@Inject
	private TemporaryResourcesAutoCleaner temporaryResourcesAutoCleaner;
	
	@Override
	public FileBaseResourceInfo make(File file) {
		return new FileResourceInfo(file, contentTypeAnalyzer);
	}

	@Override
	public FileBaseResourceInfo wrap(ResourceInfo resourceInfo, TemporaryResources temporaryResources) throws IOException{
		return new TemporaryFileBaseResourceInfo(
				resourceInfo, temporaryResources);
		//return fb;
	}
}
