package org.archboy.clobaframe.webresource.local;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import javax.inject.Named;
import org.archboy.clobaframe.io.MimeTypeDetector;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfoFactory;
import org.archboy.clobaframe.io.file.ResourceScanner;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceRepository;

@Named
public class LocalWebResourceRepository implements WebResourceRepository{

	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private MimeTypeDetector mimeTypeDetector;
	
	@Inject
	private ResourceScanner resourceScanner;
	
	private LocalWebResourceNameStrategy localWebResourceNameStrategy;
	
	private LocalWebResourceInfoFactory localWebResourceInfoGenerator;
	
	@Value("${clobaframe.webresource.repository.local.path}")
	private String localPath;
	
	private File baseDir;
	
	private final Logger logger = LoggerFactory.getLogger(LocalWebResourceRepository.class);

	@Override
	public String getName() {
		return "local";
	}

	@Override
	public int getPriority() {
		return PRIORITY_NORMAL;
	}

	@PostConstruct
	public void init() throws IOException {
		Resource resource = resourceLoader.getResource(localPath);
		baseDir = resource.getFile();
		if (!baseDir.exists()){
			throw new FileNotFoundException(String.format(
					"Can not find the file [%s], p.s. the current path is [%s].",
					localPath,
					resourceLoader.getResource(".").getFile().getAbsolutePath()));
		}

		localWebResourceNameStrategy = new DefaultLocalWebResourceNameStrategy(baseDir);
		localWebResourceInfoGenerator = new LocalWebResourceInfoFactory(mimeTypeDetector, localWebResourceNameStrategy);
	}

	@Override
	public WebResourceInfo getByName(String name) {
		File file = new File(baseDir, name);
		if (!file.exists()) {
			return null;
		}
		
		return (WebResourceInfo)localWebResourceInfoGenerator.make(file);
	}

	@Override
	public Collection<WebResourceInfo> getAll() {
		
		Collection<ResourceInfo> resourceInfos = resourceScanner.scan(baseDir, localWebResourceInfoGenerator);
		
		List<WebResourceInfo> webResourceInfos = new ArrayList<WebResourceInfo>();
		
		for(ResourceInfo resourceInfo : resourceInfos) {
			webResourceInfos.add((WebResourceInfo)resourceInfo);
		}
		
		return webResourceInfos;
	}
		

}
