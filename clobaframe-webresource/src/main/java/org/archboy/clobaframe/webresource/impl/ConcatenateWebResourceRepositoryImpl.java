package org.archboy.clobaframe.webresource.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.webresource.ConcatenateWebResourceRepository;
import org.archboy.clobaframe.webresource.WebResourceRepository;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 */
@Named
public class ConcatenateWebResourceRepositoryImpl implements ConcatenateWebResourceRepository {

	@Value("${clobaframe.webresource.concatenateConfig}")
	private String concatenateConfig;
	
	@Inject
	private ResourceLoader resourceLoader;
		
	@Inject
	private List<WebResourceRepository> resourceRepositories;
	
	private Map<String, List<String>> concatenates = new HashMap<String, List<String>>();

	@PostConstruct
	public void init() throws IOException {
		
		Resource resource = resourceLoader.getResource(concatenateConfig);
		if (!resource.exists()){
			throw new FileNotFoundException(String.format(
					"Can not find the composite config file [%s].",
					concatenateConfig));
		}

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
			concatenates.put(key, Arrays.asList(values.split(",")));
		}
		
	}

	@Override
	public WebResourceInfo getByName(String name) {
		if (concatenates.containsKey(name)){
			return getConcatenateWebResourceInfo(name);
		}else{
			return getResourceFromRepositories(name);
		}
	}

	private WebResourceInfo getConcatenateWebResourceInfo(String name) {
		List<String> names = concatenates.get(name);
		List<WebResourceInfo> webResourceInfos = new ArrayList<WebResourceInfo>(names.size());
		for(String n : names){
			webResourceInfos.add(getResourceFromRepositories(n));
		}
		return new ConcatenateWebResourceInfo(webResourceInfos, name);
	}

	@Override
	public Collection<String> getAllNames() {
		Set<String> names = new HashSet<String>();
		
		for (WebResourceRepository resourceRepository : resourceRepositories){
			Collection<String> ns = resourceRepository.getAllNames(); 
			names.addAll(ns);
		}
			
		names.addAll(concatenates.keySet());
		
		return names;
	}
	
	
	private WebResourceInfo getResourceFromRepositories(String name){
		WebResourceInfo webResourceInfo = null;
		
		for (WebResourceRepository resourceRepository : resourceRepositories){
			webResourceInfo = resourceRepository.getByName(name);
			if (webResourceInfo != null) {
				break;
			}
		}
		return webResourceInfo;
	}
}
