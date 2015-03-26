package org.archboy.clobaframe.webresource.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.archboy.clobaframe.webresource.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.webresource.ResourceRepository;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.CollectionUtils;

/**
 *
 * @author yang
 */
@Named
public class CompositeResourceRepositoryImpl implements CompositeResourceRepository {

	private static final String ENV_KEY_COMPOSITE_CONFIG = "clobaframe.webresource.compositeConfig";
	
	//@Value("${clobaframe.webresource.compositeConfig}")
	//private String compositeConfig;
	
	@Inject
	private ResourceLoader resourceLoader;
		
	@Inject
	private Environment environment;
	
	@Inject
	private List<ResourceRepository> resourceRepositories;
	
	private Map<String, List<String>> composites = new HashMap<String, List<String>>();

	@PostConstruct
	public void init() throws IOException {

		if (!environment.containsProperty(ENV_KEY_COMPOSITE_CONFIG)){
			return;
		}
		
		String compositeConfig = environment.getProperty(ENV_KEY_COMPOSITE_CONFIG);
		Resource resource = resourceLoader.getResource(compositeConfig);
		File rootDir = resource.getFile();
		if (!rootDir.exists()){
			throw new FileNotFoundException(String.format(
					"Can not find the composite config file [%s].",
					compositeConfig));
		}

//		logger.debug("Scan web resource folder [{}].", rootDir.getAbsolutePath());
//		
//		webResourceInfos = getLocalWebResources(rootDir);
//		webResourceInfos.addAll(getCombineResources(webResourceInfos));
//		
//		resourceLocationGenerator = new LocalResourceLocationGenerator(localLocationPrefix);
		
		Properties properties = new Properties();
		InputStream in = null;
		
		try{
			in = resource.getInputStream();
			properties.load(in);
		}finally{
			IOUtils.closeQuietly(in);
		}
		
		for(Object keyObj : properties.keySet()){
			String key = keyObj.toString();
			String values = properties.getProperty(key);
			composites.put(key, Arrays.asList(values.split(",")));
		}
		
	}

	@Override
	public WebResourceInfo getByName(String name) {
		if (composites.containsKey(name)){
			List<String> names = composites.get(name);
			List<WebResourceInfo> webResourceInfos = new ArrayList<WebResourceInfo>(names.size());
			for(String n : names){
				webResourceInfos.add(getResourceFromRepositories(n));
			}
			return new CompositeWebResourceInfo(webResourceInfos, name);
		}else{
			return getResourceFromRepositories(name);
		}
	}
	
	private WebResourceInfo getResourceFromRepositories(String name){
		WebResourceInfo webResourceInfo = null;
		
		for (ResourceRepository resourceRepository : resourceRepositories){
			webResourceInfo = resourceRepository.getByName(name);
			if (webResourceInfo != null) {
				break;
			}
		}
		return webResourceInfo;
	}
}
