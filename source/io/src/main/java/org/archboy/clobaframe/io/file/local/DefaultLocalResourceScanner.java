package org.archboy.clobaframe.io.file.local;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import javax.inject.Named;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfoFactory;

/**
 *
 * @author yang
 */
public class DefaultLocalResourceScanner implements LocalResourceScanner {

	@Override
	public Collection<FileBaseResourceInfo> list(
			File basePath, 
			FileBaseResourceInfoFactory fileBaseResourceInfoFactory) {

		List<FileBaseResourceInfo> resourceInfos = new ArrayList<FileBaseResourceInfo>();

		Stack<File> dirs = new Stack<File>();
		dirs.push(basePath);

		while (!dirs.isEmpty()) {
			File dir = dirs.pop();
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					dirs.push(file);
				} else {
					FileBaseResourceInfo resourceInfo = fileBaseResourceInfoFactory.make(file);
					resourceInfos.add(resourceInfo);
				}
			}
		}

		return resourceInfos;
	}

	
}
