package org.archboy.clobaframe.io.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.io.MimeTypeDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
@Named
public class ExtensionNameMimeTypeDetector implements MimeTypeDetector {
	//, ResourceLoaderAware, InitializingBean {

	private Map<String, String> mimeTypes = new HashMap<String, String>();
	
	private static final String UNKNOWN_MIME_TYPE = "application/octet-stream";
	
	// the default mime type list file is from Apache httpd
	public static final String DEFAULT_MIME_TYPE_LIST_FILE = "classpath:org/archboy/clobaframe/io/mime.types";
	
	// the custom mime type list
	public static final String DEFAULT_EXTRA_MIME_TYPE_LIST_FILE = "classpath:org/archboy/clobaframe/io/extra.mime.types.json";
	
	public static final String SETTING_KEY_MIME_TYPE_LIST_FILE = "clobaframe.io.mimeTypeListFile";
	public static final String SETTING_KEY_EXTRA_MIME_TYPE_LIST_FILE = "clobaframe.io.extraMimeTypeListFile";
	
	@Value("${" + SETTING_KEY_MIME_TYPE_LIST_FILE + ":" + DEFAULT_MIME_TYPE_LIST_FILE + "}")
	private String mimeTypeListFile = DEFAULT_MIME_TYPE_LIST_FILE;
	
	@Value("${" + SETTING_KEY_EXTRA_MIME_TYPE_LIST_FILE + ":" + DEFAULT_EXTRA_MIME_TYPE_LIST_FILE + "}")
	private String extraMimeTypeListFile = DEFAULT_EXTRA_MIME_TYPE_LIST_FILE;
	
	@Inject
	private ResourceLoader resourceLoader;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private final Logger logger = LoggerFactory.getLogger(ExtensionNameMimeTypeDetector.class);

	//@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public void setMimeTypeListFile(String mimeTypeListFile) {
		this.mimeTypeListFile = mimeTypeListFile;
	}

	public void setExtraMimeTypeListFile(String extraMimeTypeListFile) {
		this.extraMimeTypeListFile = extraMimeTypeListFile;
	}
	
	@PostConstruct
	//@Override
	public void init() throws Exception {
		loadMimeTypesList(mimeTypeListFile);
		loadExtraMimeTypesList(extraMimeTypeListFile);
	}
	
	private void loadMimeTypesList(String resourceName) throws IOException {
		Resource resource = resourceLoader.getResource(resourceName);
		if (!resource.exists()) {
			//logger.error("Can not find the mime type list file.");
			throw new FileNotFoundException(
					String.format("Can not find the mime type list file [%s].", resourceName));
		}
		
		InputStream in = null;
		try{
			in = resource.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			while(true){
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				
				if (line.startsWith("#") || StringUtils.isBlank(line)){
					continue;
				}
				
				int pos = line.indexOf('\t');
				String name = line.substring(0, pos).trim();
				String list = line.substring(pos).trim();
				
				if (StringUtils.isBlank(list)) {
					continue;
				}
				
				String[] extensions = list.split("\\s");
				for(String e:extensions){
					//if (!mimeTypes.containsKey(e)){
					mimeTypes.put(e, name);
					//}
				}
			}
		}finally{
			IOUtils.closeQuietly(in);
		}
	}
	
	private void loadExtraMimeTypesList(String resourceName) throws IOException {
		Resource resource = resourceLoader.getResource(resourceName);
		if (!resource.exists()) {
			//logger.error("Can not find the mime type list file.");
			throw new FileNotFoundException(
					String.format("Can not find the extra mime type list file [%s].", resourceName));
		}
		
		InputStream in = null;
		try{
			in = resource.getInputStream();
			String text = IOUtils.toString(in, "UTF-8");
			Map<String, String> map = objectMapper.readValue(text, new TypeReference<Map<String, String>>() {});
			for(Map.Entry<String, String> entry : map.entrySet()){
				mimeTypes.put(entry.getKey(), entry.getValue());
			}
		}finally{
			IOUtils.closeQuietly(in);
		}
	}
	
	@Override
	public String getByFile(File file) {
		Assert.notNull(file);
		
		String extension = FilenameUtils.getExtension(file.getName());
		String name = mimeTypes.get(extension);
		return (name == null ? UNKNOWN_MIME_TYPE : name);
	}

	@Override
	public String getByExtensionName(String filename) {
		Assert.hasText(filename);
		
		String extension = FilenameUtils.getExtension(filename);
		String name = mimeTypes.get(extension);
		return (name == null ? UNKNOWN_MIME_TYPE : name);
	}

	@Override
	public String getByContent(InputStream in) {
		Assert.notNull(in);
		
		throw new UnsupportedOperationException("Does not supported.");
	}
	
	
	
}
