package org.archboy.clobaframe.io.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.archboy.clobaframe.io.ContentTypeDetector;

/**
 *
 * @author yang
 */
@Component
public class ExtensionNameContentTypeDetector implements ContentTypeDetector {

	private Map<String, String> mimeTypes = new HashMap<String, String>();
	
	private static final String UNKNOWN_MIME_TYPE = "application/octet-stream";
	private static final String mimeTypeListFile = "classpath:org/archboy/clobaframe/webio/mime.types";
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	private final Logger logger = LoggerFactory.getLogger(ExtensionNameContentTypeDetector.class);
	
	@PostConstruct
	public void init() throws IOException {
		
		Resource resource = resourceLoader.getResource(mimeTypeListFile);
		if (!resource.exists()) {
			logger.error("Can not load the mime type list file.");
			throw new FileNotFoundException();
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
				String name = line.substring(0, pos);
				String list = line.substring(pos).trim();
				
				if (StringUtils.isBlank(list)) {
					continue;
				}
				
				String[] extensions = list.split("\\s");
				for(String e:extensions){
					if (!mimeTypes.containsKey(e)){
						mimeTypes.put(e, name);
					}
				}
			}
		}finally{
			IOUtils.closeQuietly(in);
		}
	}
	
	@Override
	public String getByFile(File file) {
		String extension = FilenameUtils.getExtension(file.getName());
		String name = mimeTypes.get(extension);
		return (name == null?UNKNOWN_MIME_TYPE:name);
	}

	@Override
	public String getByExtensionName(String filename) {
		String extension = FilenameUtils.getExtension(filename);
		String name = mimeTypes.get(extension);
		return (name == null?UNKNOWN_MIME_TYPE:name);
	}

	@Override
	public String getByContent(InputStream in) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	
	
}
