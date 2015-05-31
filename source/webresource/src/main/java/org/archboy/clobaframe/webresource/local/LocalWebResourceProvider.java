package org.archboy.clobaframe.webresource.local;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import org.archboy.clobaframe.io.file.ResourceScanner;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceProvider;

@Named
public class LocalWebResourceProvider implements WebResourceProvider{

	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private MimeTypeDetector mimeTypeDetector;
	
	@Inject
	private ResourceScanner resourceScanner;
	
	private LocalWebResourceNameStrategy localWebResourceNameStrategy;
	
	private LocalWebResourceInfoFactory localWebResourceInfoGenerator;
	
	// local resource path, usually relative to the 'src/main/webapp' folder.
	// to using this repository, the web application war package must be expended when running.
	private static final String DEFAULT_LOCAL_PATH = "resources";
	
	@Value("${clobaframe.webresource.repository.local.path:" + DEFAULT_LOCAL_PATH + "}")
	private String localPath;
	
	private File baseDir;
	
	private final Logger logger = LoggerFactory.getLogger(LocalWebResourceProvider.class);

	@Override
	public String getName() {
		return "local";
	}

	@Override
	public int getOrder() {
		return PRIORITY_NORMAL;
	}

	@PostConstruct
	public void init() {
		Resource resource = resourceLoader.getResource(localPath);
		
		try{
			baseDir = resource.getFile();
			
			// Do not throws exception because the web application maybe running in the
			// WAR package.
			if (!baseDir.exists()){
				logger.error("Can not find the web resource folder [{}], please ensure " +
						"unpackage the WAR if you are running web application.", localPath);
				return;
			}
			
			localWebResourceNameStrategy = new DefaultLocalWebResourceNameStrategy(baseDir);
			localWebResourceInfoGenerator = new LocalWebResourceInfoFactory(mimeTypeDetector, localWebResourceNameStrategy);
			
		}catch(IOException e){
			logger.error("Load local web resource repository error, {}", e.getMessage());
		}
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
		List<WebResourceInfo> webResourceInfos = new ArrayList<WebResourceInfo>();
		
		Collection<ResourceInfo> resourceInfos = resourceScanner.scan(baseDir, localWebResourceInfoGenerator);
		for(ResourceInfo resourceInfo : resourceInfos) {
			webResourceInfos.add((WebResourceInfo)resourceInfo);
		}
		
		return webResourceInfos;
	}
}
