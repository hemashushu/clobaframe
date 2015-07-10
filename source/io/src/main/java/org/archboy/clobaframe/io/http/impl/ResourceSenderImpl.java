package org.archboy.clobaframe.io.http.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.ResourceInfoFactory;
import org.archboy.clobaframe.io.http.ResourceSender;
import org.archboy.clobaframe.io.impl.DefaultResourceInfoFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author yang
 *
 */
@Named
public class ResourceSenderImpl implements ResourceSender, ResourceLoaderAware, InitializingBean {

	public static final String SETTING_KEY_ENABLE_GZIP = "clobaframe.io.http.gzip";
	public static final String SETTING_KEY_MIN_COMPRESS_SIZE = "clobaframe.io.http.gzip.minCompressSize";
	public static final String SETTING_KEY_COMPRESSIBLE_MIME_TYPE_LIST = "clobaframe.io.http.gzip.mimeTypeList";
	
	public static final boolean DEFAULT_ENABLE_GZIP = false;
	// only the content length large than this value would be compress
	public static final int DEFAULT_MIN_COMPRESS_SIZE = 1024;
	public static final String DEFAULT_COMPRESSIBLE_MIME_TYPE_LIST = "classpath:org/archboy/clobaframe/io/compressibleMimeType.json";
	
	@Value("${" + SETTING_KEY_ENABLE_GZIP + ":"+ DEFAULT_ENABLE_GZIP + "}")
	private boolean enableGzip = DEFAULT_ENABLE_GZIP;

	@Value("${" + SETTING_KEY_MIN_COMPRESS_SIZE + ":" + DEFAULT_MIN_COMPRESS_SIZE +"}")
	private int minCompressSize = DEFAULT_MIN_COMPRESS_SIZE;
	
	/**
	 * IoC auto inject the setting value.
	 * SETTING_KEY_COMPRESSIBLE_MIME_TYPE_LIST for setting key.
	 * DEFAULT_COMPRESSIBLE_MIME_TYPE_LIST for default setting value if the key does not exist or unset.
	 * compressibleMimeTypeList = DEFAULT_COMPRESSIBLE_MIME_TYPE_LIST for non-IoC application.
	 */
	@Value("${" + SETTING_KEY_COMPRESSIBLE_MIME_TYPE_LIST + ":" + DEFAULT_COMPRESSIBLE_MIME_TYPE_LIST + "}")
	private String compressibleMimeTypeList = DEFAULT_COMPRESSIBLE_MIME_TYPE_LIST;
		
	//@Inject
	private ResourceLoader resourceLoader;
	
	private ResourceInfoFactory resourceInfoFactory = new DefaultResourceInfoFactory();
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private ResourceSender resourceSender;

	public void setEnableGzip(boolean enableGzip) {
		this.enableGzip = enableGzip;
	}

	public void setMinCompressSize(int minCompressSize) {
		this.minCompressSize = minCompressSize;
	}

	public void setCompressibleMimeTypeList(String compressibleMimeTypeList) {
		this.compressibleMimeTypeList = compressibleMimeTypeList;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	//@PostConstruct
	@Override
	public void afterPropertiesSet() throws Exception {
		resourceSender = new DefaultResourceSender();
		
		if (enableGzip) {
			Set<String> mimeTypeList = getCompressibleMimeTypeList();
			resourceSender = new GZipResourceSender(resourceSender, mimeTypeList, minCompressSize, resourceInfoFactory);
		}
		
		resourceSender = new PartialResourceSender(resourceSender, resourceInfoFactory);
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
		InputStream in = null;
		
		try{
//			BufferedReader r = new BufferedReader(new InputStreamReader(in));
//			while(true){
//				String line = r.readLine();
//				if (line == null) break;
//				if (line.startsWith("#")) continue;
//				mimeTypes.add(line);
//			}
			in = resource.getInputStream();
			String text = IOUtils.toString(in, "UTF-8");
			List<String> list = objectMapper.readValue(text, new TypeReference<List<String>>() {});
			for(String item : list){
				mimeTypes.add(item);
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
