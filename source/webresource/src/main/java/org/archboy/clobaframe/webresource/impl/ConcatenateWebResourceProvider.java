package org.archboy.clobaframe.webresource.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceProvider;
import org.archboy.clobaframe.webresource.WebResourceProviderSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 */
@Named
public class ConcatenateWebResourceProvider implements WebResourceProvider {

	private static final String DEFAULT_CONCATENATE_CONFIG = "";
	
	@Value("${clobaframe.webresource.concatenateConfig:" + DEFAULT_CONCATENATE_CONFIG + "}")
	private String concatenateConfig;
	
	@Inject
	private ResourceLoader resourceLoader;
	
	@Inject
	private WebResourceProviderSet webResourceProviderSet;

	private ObjectMapper objectMapper = new ObjectMapper();
	
	// the concatenate web resource
	private Map<String, List<String>> concatenates = new HashMap<String, List<String>>();

	private final Logger logger = LoggerFactory.getLogger(ConcatenateWebResourceProvider.class);
	
	@Override
	public String getName() {
		return "concatenate";
	}

	@Override
	public int getOrder() {
		return PRIORITY_HIGHEST;
	}

	@PostConstruct
	public void init() throws IOException {
		if (StringUtils.isEmpty(concatenateConfig)){
			return;
		}
		
		Resource resource = resourceLoader.getResource(concatenateConfig);
		
		// Do not throws exception because the web application maybe 
		// there is no concatenate config.
		if (!resource.exists()){
			logger.error("Can not find the concatenate config file [{}].", concatenateConfig);
			return;
		}

		//Properties properties = new Properties();
		InputStream in = null;
		
		try{
			in = resource.getInputStream();
			String text = IOUtils.toString(in, "UTF-8");
			Map<String, List<String>> map = objectMapper.readValue(text, new TypeReference<Map<String, List<String>>>() {});
			//properties.load(in);
			
			for(Map.Entry<String, List<String>> entry: map.entrySet()){
				//String key = keyObj.toString();
				//String values = properties.getProperty(key);
				//concatenates.put(key, Arrays.asList(values.split(",")));
				concatenates.put(entry.getKey(), entry.getValue());
			}
		}finally{
			IOUtils.closeQuietly(in);
		}
		
	}
	
	@Override
	public WebResourceInfo getByName(String name) {
		List<String> names = concatenates.get(name);
		
		if (names == null || names.isEmpty()){
			return null;
		}
		
		List<WebResourceInfo> webResourceInfos = new ArrayList<WebResourceInfo>(names.size());
		for(String n : names){
			webResourceInfos.add(webResourceProviderSet.getByName(n));
		}
		return new DefaultConcatenateWebResourceInfo(webResourceInfos, name);
	}

	@Override
	public Collection<WebResourceInfo> getAll() {
		List<WebResourceInfo> resourceInfos = new ArrayList<WebResourceInfo>();
		
		for (String name : concatenates.keySet()) {
			WebResourceInfo resourceInfo = getByName(name);
			if (resourceInfo != null) {
				resourceInfos.add(resourceInfo);
			}
		}
		
		return resourceInfos;
	}
	
}
