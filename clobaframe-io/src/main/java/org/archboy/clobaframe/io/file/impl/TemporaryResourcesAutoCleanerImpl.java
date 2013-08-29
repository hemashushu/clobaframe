package org.archboy.clobaframe.io.file.impl;

import java.io.File;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;
import org.apache.commons.io.FileCleaningTracker;
import org.archboy.clobaframe.io.file.TemporaryResourcesAutoCleaner;

/**
 *
 * @author yang
 */
@Named
public class TemporaryResourcesAutoCleanerImpl implements TemporaryResourcesAutoCleaner{
	
	private FileCleaningTracker fileCleaningTracker;

	@PostConstruct
	public void init(){
		fileCleaningTracker = new FileCleaningTracker();
	}

	@PreDestroy
	public void destory(){
		fileCleaningTracker.exitWhenFinished();
	}

//	@Override
//	public FileCleaningTracker getFileCleaningTracker() {
//		return fileCleaningTracker;
//	}

	@Override
	public void track(File file, Object marker) {
		fileCleaningTracker.track(file, marker);
	}
}
