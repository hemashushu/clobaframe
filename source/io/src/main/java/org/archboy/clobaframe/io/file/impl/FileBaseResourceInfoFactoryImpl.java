package org.archboy.clobaframe.io.file.impl;

import java.io.File;
import javax.inject.Inject;
import javax.inject.Named;
import org.archboy.clobaframe.io.MimeTypeDetector;
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
		String mimeType = mimeTypeDetector.getByExtensionName(file.getName());
		return new DefaultFileBaseResourceInfo(file, mimeType);
	}
}
