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
public class DefaultFileBaseResourceInfoFactory implements FileBaseResourceInfoFactory {
	
	protected MimeTypeDetector mimeTypeDetector;

	public DefaultFileBaseResourceInfoFactory(MimeTypeDetector mimeTypeDetector) {
		this.mimeTypeDetector = mimeTypeDetector;
	}
	
	@Override
	public FileBaseResourceInfo make(File file) {
		String mimeType = getMimeType(file);
		return new DefaultFileBaseResourceInfo(file, mimeType);
	}
	
	protected String getMimeType(File file){
		String fileName = file.getName();
		return mimeTypeDetector.getByExtensionName(fileName);
	}
}
