package org.archboy.clobaframe.webresource.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.webresource.AbstractWebResourceRepository;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceRepositorySet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 */
@Named
public class ConcatenateWebResourceRepository extends AbstractWebResourceRepository {

	@Value("${clobaframe.webresource.concatenateConfig}")
	private String concatenateConfig;
	
	@Inject
	private ResourceLoader resourceLoader;
	
	@Inject
	private WebResourceRepositorySet webResourceRepositorySet;

	private ObjectMapper objectMapper = new ObjectMapper();
	
	// the concatenate web resource
	private Map<String, List<String>> concatenates = new HashMap<String, List<String>>();

	@Override
	public String getName() {
		return "concatenate";
	}

	@Override
	public int getPriority() {
		return PRIORITY_HIGHEST;
	}

	@PostConstruct
	public void init() throws IOException {
		
		if (StringUtils.isEmpty(concatenateConfig)){
			return;
		}
		
		Resource resource = resourceLoader.getResource(concatenateConfig);
		if (!resource.exists()){
			throw new FileNotFoundException(String.format(
					"Can not find the composite config file [%s].",
					concatenateConfig));
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
			webResourceInfos.add(webResourceRepositorySet.getByName(n));
		}
		return new ConcatenateWebResourceInfo(webResourceInfos, name);
	}

	@Override
	public Collection<String> getAllNames() {
		return concatenates.keySet();
	}
	
}
