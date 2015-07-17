package org.archboy.clobaframe.resource.local;

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
import org.archboy.clobaframe.io.NamedResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.io.file.local.DefaultLocalResourceProvider;
import org.archboy.clobaframe.io.file.local.LocalResourceProvider;
import org.archboy.clobaframe.resource.ContentHashResourceInfo;
import org.archboy.clobaframe.resource.ResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;

@Named
public class MultiPathLocalResourceProvider implements ResourceProvider {
	//, ResourceLoaderAware, InitializingBean {

	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private MimeTypeDetector mimeTypeDetector;
	
	// local resource path, usually relative to the 'src/main/webapp' folder.
	// to using this repository, the web application war package must be expended when running.
	public static final String DEFAULT_LOCAL_PATH = ""; // "resources/default";
	public static final String DEFAULT_RESOURCE_NAME_PREFIX = "";
	public static final String DEFAULT_OTHER_RESOURCE_PATH_AND_NAME_PREFIX = "";
	
	public static final String SETTING_KEY_LOCAL_PATH = "clobaframe.resource.repository.local.path";
	public static final String SETTING_KEY_RESOURCE_NAME_PREFIX = "clobaframe.resource.repository.local.resourceNamePrefix";
	public static final String SETTING_KEY_OTHER_RESOURCE_PATH_AND_NAME_PREFIX = "clobaframe.resource.repository.local.otherResourcePathAndNamePrefix";
	
	@Value("${" + SETTING_KEY_LOCAL_PATH + ":" + DEFAULT_LOCAL_PATH + "}")
	private String localPath;
	
	@Value("${" + SETTING_KEY_RESOURCE_NAME_PREFIX + ":" + DEFAULT_RESOURCE_NAME_PREFIX + "}")
	private String resourceNamePrefix;
	
	@Value("${" + SETTING_KEY_OTHER_RESOURCE_PATH_AND_NAME_PREFIX + ":" + DEFAULT_OTHER_RESOURCE_PATH_AND_NAME_PREFIX + "}")
	private String otherResourcePathAndNamePrefix;

	private List<LocalResourceProvider> localResourceProviders = new ArrayList<LocalResourceProvider>();
	
	private final Logger logger = LoggerFactory.getLogger(MultiPathLocalResourceProvider.class);

	public void setMimeTypeDetector(MimeTypeDetector mimeTypeDetector) {
		this.mimeTypeDetector = mimeTypeDetector;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public void setResourceNamePrefix(String resourceNamePrefix) {
		this.resourceNamePrefix = resourceNamePrefix;
	}

	public void setOtherResourcePathAndNamePrefix(String otherResourcePathAndNamePrefix) {
		this.otherResourcePathAndNamePrefix = otherResourcePathAndNamePrefix;
	}

	//@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	@Override
	public String getName() {
		return "local";
	}

	@Override
	public int getOrder() {
		return PRIORITY_NORMAL;
	}

	@PostConstruct
	//@Override
	public void init() throws Exception {
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
			
			LocalResourceNameStrategy localWebResourceNameStrategy = new DefaultLocalResourceNameStrategy(basePath, namePrefix);
			LocalResourceInfoFactory localWebResourceInfoFactory = new LocalResourceInfoFactory(mimeTypeDetector, localWebResourceNameStrategy);
			LocalResourceProvider localResourceProvider = new DefaultLocalResourceProvider(basePath, localWebResourceInfoFactory, localWebResourceNameStrategy);
			
			localResourceProviders.add(localResourceProvider);
			
		}catch(IOException e){
			logger.error("Load local web resource repository error, {}", e.getMessage());
		}
	}

	@Override
	public NamedResourceInfo getByName(String name) {
		for(LocalResourceProvider localResourceProvider : localResourceProviders) {
			NamedResourceInfo resourceInfo = (NamedResourceInfo)localResourceProvider.getByName(name);
			if (resourceInfo != null) {
				return resourceInfo;
			}
		}
		
		return null;
	}

	@Override
	public Collection<NamedResourceInfo> list() {
		List<NamedResourceInfo> resourceInfos = new ArrayList<NamedResourceInfo>();
		
		for(LocalResourceProvider localResourceProvider : localResourceProviders) {
			Collection<FileBaseResourceInfo> fileBaseResourceInfos = localResourceProvider.list();
			for(FileBaseResourceInfo fileBaseResourceInfo : fileBaseResourceInfos) {
				resourceInfos.add((NamedResourceInfo)fileBaseResourceInfo);
			}
		}
		
		return resourceInfos;
	}
}
