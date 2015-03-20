package org.archboy.clobaframe.io.file.impl;

import java.io.File;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.io.MimeTypeDetector;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.TemporaryResources;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfoFactory;

/**
 *
 * @author yang
 */
@Named
public class FileBaseResourceInfoFactoryImpl implements FileBaseResourceInfoFactory {
	
	@Inject
	private MimeTypeDetector mimeTypeDetector;
	
	@Override
	public FileBaseResourceInfo make(File file) {
		return new FileResourceInfo(file, mimeTypeDetector);
	}

	@Override
	public FileBaseResourceInfo wrap(ResourceInfo resourceInfo, TemporaryResources temporaryResources) throws IOException{
		return new TemporaryFileBaseResourceInfo(
				resourceInfo, temporaryResources);
	}

}
