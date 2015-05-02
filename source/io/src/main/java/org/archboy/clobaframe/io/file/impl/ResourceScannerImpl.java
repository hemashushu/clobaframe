package org.archboy.clobaframe.io.file.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import javax.inject.Named;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfoFactory;
import org.archboy.clobaframe.io.file.ResourceScanner;

/**
 *
 * @author yang
 */
@Named
public class ResourceScannerImpl implements ResourceScanner {

	@Override
	public Collection<ResourceInfo> scan(
			File basePath, 
			FileBaseResourceInfoFactory fileBaseResourceInfoFactory) {

		List<ResourceInfo> resourceInfos = new ArrayList<ResourceInfo>();

		Stack<File> dirs = new Stack<File>();
		dirs.push(basePath);

		while (!dirs.isEmpty()) {
			File dir = dirs.pop();
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					dirs.push(file);
				} else {
					ResourceInfo resourceInfo = fileBaseResourceInfoFactory.make(file);
					resourceInfos.add(resourceInfo);
				}
			}
		}

		return resourceInfos;
	}

	
}
