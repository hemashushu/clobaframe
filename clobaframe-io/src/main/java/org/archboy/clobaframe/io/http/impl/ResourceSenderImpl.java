package org.archboy.clobaframe.io.http.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.http.ResourceSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 *
 */
@Named
public class ResourceSenderImpl implements ResourceSender {

	private static final boolean DEFAULT_ENABLE_GZIP = false;
	
	@Value("${clobaframe.io.http.gzip}")
	private boolean enableGzip = DEFAULT_ENABLE_GZIP;

	// only the content length large than this value would be compress
	private static final int DEFAULT_MIN_COMPRESS_SIZE = 1024;
	
	@Value("${clobaframe.io.http.gzip.minCompressSize}")
	private int minCompressSize = DEFAULT_MIN_COMPRESS_SIZE;
	
	private static final String DEFAULT_COMPRESSIBLE_MIME_TYPE_LIST = "classpath:org/archboy/clobaframe/io/compressibleMimeType.list";
	
	@Value("${clobaframe.io.http.gzip.mimeTypeList}")
	private String compressibleMimeTypeList = DEFAULT_COMPRESSIBLE_MIME_TYPE_LIST;
		
	@Inject
	private ResourceLoader resourceLoader;
	
	private ResourceSender resourceSender;
	
	@PostConstruct
	public void init() throws IOException{
		resourceSender = new DefaultResourceSender();
		
		if (enableGzip) {
			Set<String> mimeTypeList = getCompressibleMimeTypeList();
			resourceSender = new GZipResourceSender(resourceSender, mimeTypeList, minCompressSize);
		}
		
		resourceSender = new PartialResourceSender(resourceSender);
		resourceSender = new LastModifiedCheckingResourceSender(resourceSender);
	}
	
	private Set<String> getCompressibleMimeTypeList() throws IOException{
		Resource resource = resourceLoader.getResource(compressibleMimeTypeList);
		if (!resource.exists()){
			throw new FileNotFoundException(String.format(
					"Can not find the compressible-mimetype list file [%s].",
					compressibleMimeTypeList));
		}

		Set<String> mimeTypes = new HashSet<String>();
		InputStream in = resource.getInputStream();
		
		try{
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			while(true){
				String line = r.readLine();
				if (line == null) break;
				if (line.startsWith("#")) continue;
				mimeTypes.add(line);
			}
		}finally{
			IOUtils.closeQuietly(in);
		}
		
		return mimeTypes;
	}
	
	@Override
	public void send(ResourceInfo resourceInfo, Map<String, Object> extraHeaders, HttpServletRequest request, HttpServletResponse response) throws IOException {
		resourceSender.send(resourceInfo, extraHeaders, request, response);
	}
}
