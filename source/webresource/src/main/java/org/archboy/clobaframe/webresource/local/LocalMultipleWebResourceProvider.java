package org.archboy.clobaframe.webresource.local;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.io.MimeTypeDetector;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.io.file.local.DefaultLocalResourceProvider;
import org.archboy.clobaframe.io.file.local.LocalResourceProvider;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

@Named
public class LocalMultipleWebResourceProvider implements WebResourceProvider {

	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private MimeTypeDetector mimeTypeDetector;
	
	// local resource path, usually relative to the 'src/main/webapp' folder.
	// to using this repository, the web application war package must be expended when running.
	private static final String DEFAULT_LOCAL_PATH = ""; // "resources/default";
	private static final String DEFAULT_RESOURCE_NAME_PREFIX = "";
	private static final String DEFAULT_OTHER_RESOURCE_PATH_AND_NAME_PREFIX = "";
	
	@Value("${clobaframe.webresource.repository.local.path:" + DEFAULT_LOCAL_PATH + "}")
	private String localPath;
	
	@Value("${clobaframe.webresource.repository.local.resourceNamePrefix:" + DEFAULT_RESOURCE_NAME_PREFIX + "}")
	private String resourceNamePrefix;
	
	@Value("${clobaframe.webresource.repository.local.otherResourcePathAndNamePrefix:" + DEFAULT_OTHER_RESOURCE_PATH_AND_NAME_PREFIX + "}")
	private String otherResourcePathAndNamePrefix;

	private List<LocalResourceProvider> localResourceProviders = new ArrayList<LocalResourceProvider>();
	
	private final Logger logger = LoggerFactory.getLogger(LocalMultipleWebResourceProvider.class);
	
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
		// add base local resource path
		if (StringUtils.isNotEmpty(localPath)) {
			addLocalResource(localPath, resourceNamePrefix);
		}
		
		// add other local resource path
		if (StringUtils.isNotEmpty(otherResourcePathAndNamePrefix)){
			String[] lines = otherResourcePathAndNamePrefix.split(";");
			for(String line : lines){
				int pos = line.indexOf("|");
				if (pos > 0) {
					addLocalResource(line.substring(0, pos), line.substring(pos + 1));
				}else{
					addLocalResource(line, null);
				}
			}
		}
	}

	protected void addLocalResource(String path, String namePrefix) {
		Resource resource = resourceLoader.getResource(path);
		
		try{
			File basePath = resource.getFile();
			
			// Do not throws exception because the web application maybe running in the
			// WAR package.
			if (!basePath.exists()){
				logger.error("Can not find the web resource folder [{}], please ensure " +
						"unpackage the WAR if you are running web application.", path);
				return;
			}
			
			LocalWebResourceNameStrategy localWebResourceNameStrategy = new DefaultLocalWebResourceNameStrategy(basePath, namePrefix);
			LocalWebResourceInfoFactory localWebResourceInfoFactory = new LocalWebResourceInfoFactory(mimeTypeDetector, localWebResourceNameStrategy);
			LocalResourceProvider localResourceProvider = new DefaultLocalResourceProvider(basePath, localWebResourceInfoFactory, localWebResourceNameStrategy);
			
			localResourceProviders.add(localResourceProvider);
			
		}catch(IOException e){
			logger.error("Load local web resource repository error, {}", e.getMessage());
		}
	}

	@Override
	public WebResourceInfo getByName(String name) {
		for(LocalResourceProvider localResourceProvider : localResourceProviders) {
			WebResourceInfo webResourceInfo = (WebResourceInfo)localResourceProvider.getByName(name);
			if (webResourceInfo != null) {
				return webResourceInfo;
			}
		}
		
		return null;
	}

	@Override
	public Collection<WebResourceInfo> getAll() {
		List<WebResourceInfo> webResourceInfos = new ArrayList<WebResourceInfo>();
		
		for(LocalResourceProvider localResourceProvider : localResourceProviders) {
			Collection<FileBaseResourceInfo> fileBaseResourceInfos = localResourceProvider.getAll();
			for(FileBaseResourceInfo fileBaseResourceInfo : fileBaseResourceInfos) {
				webResourceInfos.add((WebResourceInfo)fileBaseResourceInfo);
			}
		}
		
		return webResourceInfos;
	}
}
