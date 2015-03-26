package org.archboy.clobaframe.webresource.local;

import org.archboy.clobaframe.webresource.impl.CompositeWebResourceInfo;
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
import org.archboy.clobaframe.io.MimeTypeDetector;
import org.archboy.clobaframe.webresource.AbstractResourceRepository;
import org.archboy.clobaframe.webresource.ResourceRepository;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.springframework.util.Assert;

@Named
public class LocalWebResourceRepository extends AbstractResourceRepository{

	//private static final String REPOSITORY_NAME = "local";
	
	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private MimeTypeDetector mimeTypeDetector;

	@Value("${clobaframe.webresource.repository.local.path}")
	private String localPath;

//	@Value("${webresource.local.location}")
//	private String localLocationPrefix;
//	
//	@Value("${webresource.uniqueNameGenerator}")
//	private String uniqueNameGeneratorName;
//		
//	private static final String combineWebResourceConfigurationFileName = "classpath:combineWebResource.properties";
//
//	private ResourceLocationGenerator resourceLocationGenerator;
//	
//	@Inject
//	private List<UniqueNameGenerator> uniqueNameGenerators;
//	
//	// the default unique name generator
//	private UniqueNameGenerator uniqueNameGenerator;
//	
//	private List<WebResourceInfo> webResourceInfos;
	
	private File rootDir;
	
	private final Logger logger = LoggerFactory.getLogger(LocalWebResourceRepository.class);

	@Override
	public String getName() {
		return "local";
	}
//
//	@Override
//	public ResourceLocationGenerator getResourceLocationGenerator() {
//		return resourceLocationGenerator;
//	}
//	
//	@Override
//	public List<WebResourceInfo> getAll() {
//		return webResourceInfos;
//	}

	@PostConstruct
	public void init() throws IOException {
//		uniqueNameGenerator = getUniqueNameGenerator(uniqueNameGeneratorName);
//		logger.info("Using [{}] web resource unique name generator as the default.", uniqueNameGeneratorName);
		
		Resource resource = resourceLoader.getResource(localPath);
		File rootDir = resource.getFile();
		if (!rootDir.exists()){
//			logger.error("Current path is [{}], ",
//					resourceLoader.getResource(".").getFile().getAbsolutePath(),
//					localPath);
			throw new FileNotFoundException(String.format(
					"Can not find the file [%s], p.s. the current path is [%s].",
					localPath,
					resourceLoader.getResource(".").getFile().getAbsolutePath()));
		}

//		logger.debug("Scan web resource folder [{}].", rootDir.getAbsolutePath());
//		
//		webResourceInfos = getLocalWebResources(rootDir);
//		webResourceInfos.addAll(getCombineResources(webResourceInfos));
//		
//		resourceLocationGenerator = new LocalResourceLocationGenerator(localLocationPrefix);
	}

//	public List<UniqueNameGenerator> getUniqueNameGenerators() {
//		return uniqueNameGenerators;
//	}
//
//	public UniqueNameGenerator getUniqueNameGenerator(String name) {
//		Assert.hasText(name);
//		
//		for (UniqueNameGenerator generator : uniqueNameGenerators){
//			if (generator.getName().equals(name)){
//				return generator;
//			}
//		}
//		
//		throw new IllegalArgumentException(
//				String.format("The specify web resource unique name generator [%s] not found", name));
//	}

	@Override
	public WebResourceInfo getByName(String name) {
		File file = new File(rootDir, name);
		if (!file.exists()) {
			return null;
		}
		
		String mimeType = getMimeType(file);
		LocalWebResourceInfo webResourceInfo = new LocalWebResourceInfo(
				file, name, mimeType);
		return webResourceInfo;
	}
	
	
	/**
	 * Scan all resources in the specify local directory
	 *
	 * @param resourceDir
	 */
//	private List<WebResourceInfo> getLocalWebResources(File resourceDir) throws IOException{
//
//		List<WebResourceInfo> webResourcesInfos = new ArrayList<WebResourceInfo>();
//
//		Stack<File> dirs = new Stack<File>();
//		dirs.push(resourceDir);
//
//		while (!dirs.isEmpty()) {
//			File dir = dirs.pop();
//			File[] files = dir.listFiles();
//			for (File file : files) {
//				if (file.isDirectory()) {
//					dirs.push(file);
//				} else {
//					String name = getResourceName(resourceDir, file);
//					//String uniqueName = getUniqueName(resourceDir, file);
//					String mimeType = getMimeType(file);
//
//					LocalWebResourceInfo webResourceInfo = new LocalWebResourceInfo(
//							file, name, mimeType);
//					
//					String uniqueName = uniqueNameGenerator.getUniqueName(webResourceInfo);
//					webResourceInfo.setUniqueName(uniqueName);
//					
//					webResourcesInfos.add(webResourceInfo);
//
//					logger.debug(
//							"Web static resource: [{}] , unique name: [{}], mime type: [{}].",
//							new Object[]{
//							webResourceInfo.getName(),
//							webResourceInfo.getUniqueName(),
//							webResourceInfo.getMimeType()}
//							);
//				}
//			}
//		}
//
//		return webResourcesInfos;
//	}

//	private List<WebResourceInfo> getCombineResources(List<WebResourceInfo> webResourceInfos) throws FileNotFoundException {
//		Resource resource = resourceLoader.getResource(combineWebResourceConfigurationFileName);
//		if (!resource.exists()){
//			return webResourceInfos;
//		}
//		
//		Properties properties = new Properties();
//		InputStream in = null;
//		
//		try{
//			in = resource.getInputStream();
//			properties.load(in);
//			in.close();
//		}catch(IOException e){
//			//
//		}finally{
//			IOUtils.closeQuietly(in);
//		}
//		
//		List<WebResourceInfo> combineWebResourceInfos = new ArrayList<WebResourceInfo>();
//		
//		for(Object combineName : properties.keySet()){
//			String resourceNames = properties.getProperty((String)combineName);
//			String[] resourceNameArray = resourceNames.split(",");
//			
//			List<WebResourceInfo> selected = new ArrayList<WebResourceInfo>();
//			
//			for (String resourceName : resourceNameArray){
//				
//				boolean found = false;
//				for (WebResourceInfo r : webResourceInfos){
//					if (r.getName().equals(resourceName)){
//						found = true;
//						selected.add(r);
//						break;
//					}
//				}
//				
//				if (!found) {
//					throw new FileNotFoundException(String.format(
//							"Can not found the web resource [%s]", resourceName));
//				}
//			}
//			
//			CompositeWebResourceInfo resourceInfo = new CompositeWebResourceInfo(
//					selected, (String)combineName, selected.get(0).getMimeType());
//			
//			String uniqueName = uniqueNameGenerator.getUniqueName(resourceInfo);
//			resourceInfo.setUniqueName(uniqueName);
//			
//			combineWebResourceInfos.add(resourceInfo);
//		}		
//		
//		return combineWebResourceInfos;
//	}
	
	private String getMimeType(File file){
		String fileName = file.getName();
		return mimeTypeDetector.getByExtensionName(fileName);
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
//	protected String getResourceName(File resourceDir, File file){
//		String name = file.getPath().substring(resourceDir.getPath().length() + 1);
//		return name.replace('\\', '/');
//	}

}
