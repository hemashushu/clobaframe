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
	
	@Override
	public FileBaseResourceInfo getByName(String name) {
		File file = null;
		
		try{
			file = localFileNameStrategy.getFile(name);
		}catch(IllegalArgumentException e){
			return null;
		}
		
		if (!file.exists()) {
			return null;
		}

		return fileBaseResourceInfoFactory.make(file);
	}

	@Override
	public Collection<FileBaseResourceInfo> list() {
		LocalResourceScanner resourceScanner = new DefaultLocalResourceScanner();
		return resourceScanner.list(
				basePath, fileBaseResourceInfoFactory);
	}
}
