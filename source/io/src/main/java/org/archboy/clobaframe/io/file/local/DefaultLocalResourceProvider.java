package org.archboy.clobaframe.io.file.local;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfoFactory;

/**
 *
 * @author yang
 */
public class DefaultLocalResourceProvider implements LocalResourceProvider {
	
	private File basePath;
	private FileBaseResourceInfoFactory fileBaseResourceInfoFactory;
	private LocalFileNameStrategy localFileNameStrategy;

	public DefaultLocalResourceProvider(File basePath, FileBaseResourceInfoFactory fileBaseResourceInfoFactory, LocalFileNameStrategy localFileNameStrategy) {
		this.basePath = basePath;
		this.fileBaseResourceInfoFactory = fileBaseResourceInfoFactory;
		this.localFileNameStrategy = localFileNameStrategy;
	}
	
	public FileBaseResourceInfo getByName(String name) {
		File file = localFileNameStrategy.getFile(name); // new File(baseDir, name);
		if (!file.exists()) {
			return null;
		}
		
		return fileBaseResourceInfoFactory.make(file);
	}

	public Collection<FileBaseResourceInfo> getAll() {
		LocalResourceScanner resourceScanner = new DefaultLocalResourceScanner();
		return resourceScanner.scan(
				basePath, fileBaseResourceInfoFactory);
	}
}
