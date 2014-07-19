package org.archboy.clobaframe.webresource.local;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import javax.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import javax.inject.Named;
import org.archboy.clobaframe.io.ContentTypeDetector;
import org.archboy.clobaframe.webresource.ResourceLocationGenerator;
import org.archboy.clobaframe.webresource.ResourceRepository;
import org.archboy.clobaframe.webresource.UniqueNameGenerator;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.springframework.util.Assert;

@Named
public class LocalWebResourceRepository implements ResourceRepository{

	private static final String REPOSITORY_NAME = "local";
	
	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private ContentTypeDetector contentTypeAnalyzer;

	@Value("${webresource.local.path}")
	private String localPath;

	@Value("${webresource.local.location}")
	private String localLocationPrefix;
	
	@Value("${webresource.uniqueNameGenerator}")
	private String uniqueNameGeneratorName;
		
	private static final String combineWebResourceConfigurationFileName = "classpath:combineWebResource.properties";

	private ResourceLocationGenerator resourceLocationGenerator;
	
	@Inject
	private List<UniqueNameGenerator> uniqueNameGenerators;
	
	// the default unique name generator
	private UniqueNameGenerator uniqueNameGenerator;
	
	private List<WebResourceInfo> webResourceInfos;
	
	private final Logger logger = LoggerFactory.getLogger(LocalWebResourceRepository.class);

	@Override
	public String getName() {
		return REPOSITORY_NAME;
	}

	@Override
	public ResourceLocationGenerator getResourceLocationGenerator() {
		return resourceLocationGenerator;
	}
	
	@Override
	public List<WebResourceInfo> findAll() {
		return webResourceInfos;
	}

	@PostConstruct
	public void init() throws IOException {
		uniqueNameGenerator = getUniqueNameGenerator(uniqueNameGeneratorName);
		logger.info("Using [{}] web resource unique name generator as the default.", uniqueNameGeneratorName);
		
		Resource resource = resourceLoader.getResource(localPath);
		File file = resource.getFile();
		if (!file.exists()){
			logger.error("Current default path is [{}], can not find the file [{}].",
					resourceLoader.getResource(".").getFile().getAbsolutePath(),
					localPath);
			throw new FileNotFoundException();
		}

		logger.debug("Scan web resource folder [{}].", file.getAbsolutePath());
		
		webResourceInfos = getLocalWebResources(file);
		webResourceInfos.addAll(getCombineResources(webResourceInfos));
		
		resourceLocationGenerator = new LocalResourceLocationGenerator(localLocationPrefix);
	}

	public List<UniqueNameGenerator> getUniqueNameGenerators() {
		return uniqueNameGenerators;
	}

	public UniqueNameGenerator getUniqueNameGenerator(String name) {
		Assert.hasText(name);
		
		for (UniqueNameGenerator generator : uniqueNameGenerators){
			if (generator.getName().equals(name)){
				return generator;
			}
		}
		
		throw new IllegalArgumentException(
				String.format("The specify web resource unique name generator [%s] not found", name));
	}
	
	/**
	 * Scan all resources in the specify local directory
	 *
	 * @param resourceDir
	 */
	private List<WebResourceInfo> getLocalWebResources(File resourceDir) throws IOException{

		List<WebResourceInfo> webResourcesInfos = new ArrayList<WebResourceInfo>();

		Stack<File> dirs = new Stack<File>();
		dirs.push(resourceDir);

		while (!dirs.isEmpty()) {
			File dir = dirs.pop();
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					dirs.push(file);
				} else {
					String name = getResourceName(resourceDir, file);
					//String uniqueName = getUniqueName(resourceDir, file);
					String contentType = getContentType(file);

					DefaultWebResourceInfo webResourceInfo = new DefaultWebResourceInfo(
							file, name, contentType);
					
					String uniqueName = uniqueNameGenerator.getUniqueName(webResourceInfo);
					webResourceInfo.setUniqueName(uniqueName);
					
					webResourcesInfos.add(webResourceInfo);

					logger.debug(
							"Web static resource: [{}] , unique name: [{}], content type: [{}].",
							new Object[]{
							webResourceInfo.getName(),
							webResourceInfo.getUniqueName(),
							webResourceInfo.getContentType()}
							);
				}
			}
		}

		return webResourcesInfos;
	}

	private List<WebResourceInfo> getCombineResources(List<WebResourceInfo> webResourceInfos) throws FileNotFoundException {
		Resource resource = resourceLoader.getResource(combineWebResourceConfigurationFileName);
		if (!resource.exists()){
			return webResourceInfos;
		}
		
		Properties properties = new Properties();
		InputStream in = null;
		
		try{
			in = resource.getInputStream();
			properties.load(in);
			in.close();
		}catch(IOException e){
			//
		}finally{
			IOUtils.closeQuietly(in);
		}
		
		List<WebResourceInfo> combineWebResourceInfos = new ArrayList<WebResourceInfo>();
		
		for(Object combineName : properties.keySet()){
			String resourceNames = properties.getProperty((String)combineName);
			String[] resourceNameArray = resourceNames.split(",");
			
			List<WebResourceInfo> selected = new ArrayList<WebResourceInfo>();
			
			for (String resourceName : resourceNameArray){
				
				boolean found = false;
				for (WebResourceInfo r : webResourceInfos){
					if (r.getName().equals(resourceName)){
						found = true;
						selected.add(r);
						break;
					}
				}
				
				if (!found) {
					throw new FileNotFoundException(String.format(
							"Can not found the web resource [%s]", resourceName));
				}
			}
			
			CombineWebResourceInfo resourceInfo = new CombineWebResourceInfo(
					selected, (String)combineName, selected.get(0).getContentType());
			
			String uniqueName = uniqueNameGenerator.getUniqueName(resourceInfo);
			resourceInfo.setUniqueName(uniqueName);
			
			combineWebResourceInfos.add(resourceInfo);
		}		
		
		return combineWebResourceInfos;
	}
	
	private String getContentType(File file){
		String fileName = file.getName();
		return contentTypeAnalyzer.getByExtensionName(fileName);
	}

	
	/**
	 * Return resource name by file.
	 * By default, the resource name is the file name that excludes the resource dir path.
	 * such as 'css/common.css', 'js/moments.js'.
	 * 
	 * @param resourceDir
	 * @param file
	 * @return 
	 */
	protected String getResourceName(File resourceDir, File file){
		String name = file.getPath().substring(resourceDir.getPath().length() + 1);
		return name.replace('\\', '/');
	}

}
