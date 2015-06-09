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
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.io.MimeTypeDetector;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.io.file.local.DefaultLocalResourceProvider;
import org.archboy.clobaframe.io.file.local.LocalResourceProvider;
import org.archboy.clobaframe.io.file.local.LocalResourceScanner;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceProvider;

@Named
public class LocalRootWebResourceProvider implements WebResourceProvider{

	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private MimeTypeDetector mimeTypeDetector;
	
	// local resource path, usually relative to the 'src/main/webapp' folder.
	// to using this repository, the web application war package must be expended when running.
	private static final String DEFAULT_LOCAL_PATH = ""; //"resources/root";
	private static final String DEFAULT_RESOURCE_NAME_PREFIX = "root/";
	
	@Value("${clobaframe.webresource.repository.localRoot.path:" + DEFAULT_LOCAL_PATH + "}")
	private String localPath;
	
	@Value("${clobaframe.webresource.repository.localRoot.namePrefix:" + DEFAULT_RESOURCE_NAME_PREFIX + "}")
	private String resourceNamePrefix;
	
	//private File baseDir;
	private LocalResourceProvider localResourceProvider;
			
	private final Logger logger = LoggerFactory.getLogger(LocalRootWebResourceProvider.class);

	@Override
	public String getName() {
		return "localRoot";
	}

	@Override
	public int getOrder() {
		return PRIORITY_NORMAL;
	}

	@PostConstruct
	public void init() {
		if (StringUtils.isEmpty(localPath)) {
			return;
		}
		
		Resource resource = resourceLoader.getResource(localPath);
		
		try{
			File basePath = resource.getFile();
			
			// Do not throws exception because the web application maybe running in the
			// WAR package.
			if (!basePath.exists()){
				logger.error("Can not find the web resource folder [{}], please ensure " +
						"unpackage the WAR if you are running web application.", localPath);
				return;
			}
			
			LocalWebResourceNameStrategy localWebResourceNameStrategy = new DefaultLocalWebResourceNameStrategy(basePath, resourceNamePrefix);
			LocalWebResourceInfoFactory localWebResourceInfoFactory = new LocalWebResourceInfoFactory(mimeTypeDetector, localWebResourceNameStrategy);
			localResourceProvider = new DefaultLocalResourceProvider(basePath, localWebResourceInfoFactory, localWebResourceNameStrategy);
			
		}catch(IOException e){
			logger.error("Load local web resource repository error, {}", e.getMessage());
		}
	}

	@Override
	public WebResourceInfo getByName(String name) {
		if (StringUtils.isEmpty(localPath)) {
			return null;
		}
		
		return (WebResourceInfo)localResourceProvider.getByName(name);
	}

	@Override
	public Collection<WebResourceInfo> getAll() {
		List<WebResourceInfo> webResourceInfos = new ArrayList<WebResourceInfo>();
		
		if (StringUtils.isEmpty(localPath)) {
			return webResourceInfos;
		}
		
		Collection<FileBaseResourceInfo> fileBaseResourceInfos = localResourceProvider.getAll();
		for(FileBaseResourceInfo fileBaseResourceInfo : fileBaseResourceInfos) {
			webResourceInfos.add((WebResourceInfo)fileBaseResourceInfo);
		}
		
		return webResourceInfos;
	}
}
