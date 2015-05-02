package org.archboy.clobaframe.io.file.impl;

import java.io.IOException;
import javax.inject.Named;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.TemporaryResources;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfoWrapper;

/**
 *
 * @author yang
 */
@Named
public class FileBaseResourceInfoWrapperImpl implements FileBaseResourceInfoWrapper{

	@Override
	public FileBaseResourceInfo wrap(ResourceInfo resourceInfo, TemporaryResources temporaryResources) throws IOException{
		return new TemporaryFileBaseResourceInfo(
				resourceInfo, temporaryResources);
	}
	
}
