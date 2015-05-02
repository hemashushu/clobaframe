package org.archboy.clobaframe.webresource.local;

import java.io.File;
import org.archboy.clobaframe.io.MimeTypeDetector;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfoFactory;

/**
 *
 * @author yang
 */
public class LocalWebResourceInfoFactory implements FileBaseResourceInfoFactory {

	private MimeTypeDetector mimeTypeDetector;
	private LocalWebResourceNameStrategy resourceNameStrategy;

	public LocalWebResourceInfoFactory(MimeTypeDetector mimeTypeDetector, LocalWebResourceNameStrategy resourceNameStrategy) {
		this.mimeTypeDetector = mimeTypeDetector;
		this.resourceNameStrategy = resourceNameStrategy;
	}
	
	@Override
	public FileBaseResourceInfo make(File file) {
		String name = resourceNameStrategy.getName(file);
		String mimeType = getMimeType(file);
		
		LocalWebResourceInfo webResourceInfo = new LocalWebResourceInfo(
				file, name, mimeType);
		return webResourceInfo;
	}
	
	private String getMimeType(File file){
		String fileName = file.getName();
		return mimeTypeDetector.getByExtensionName(fileName);
	}
	
}
