package org.archboy.clobaframe.media.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.media.Media;
import org.archboy.clobaframe.media.MediaFactory;
import org.archboy.clobaframe.media.MediaDataSizeLimitExceededException;
import org.archboy.clobaframe.media.MediaLoader;
import org.archboy.clobaframe.media.UnsupportedMediaException;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import javax.inject.Named;
import org.archboy.clobaframe.io.MimeTypeDetector;
import org.archboy.clobaframe.io.ResourceInfoFactory;
import org.archboy.clobaframe.io.TemporaryResources;
import org.archboy.clobaframe.io.file.FileBaseResourceInfoFactory;
import org.archboy.clobaframe.io.file.FileBaseResourceInfoWrapper;
import org.archboy.clobaframe.io.file.impl.DefaultFileBaseResourceInfoFactory;
import org.archboy.clobaframe.io.impl.DefaultResourceInfoFactory;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
@Named
public class MediaFactoryImpl implements MediaFactory { //, InitializingBean{

	// default 32 MB
	public static final long DEFAULT_MAX_HANDLE_SIZE_BYTE = 32L * 1024 * 1024;
	
	public static final String SETTING_KEY_MAX_HANDLE_SIZE_BYTE = "clobaframe.media.maxHandleSize";
	
	@Value("${" + SETTING_KEY_MAX_HANDLE_SIZE_BYTE + ":" + DEFAULT_MAX_HANDLE_SIZE_BYTE + "}")
	private long maxHandleSizeByte = DEFAULT_MAX_HANDLE_SIZE_BYTE;

	private final Logger logger = LoggerFactory.getLogger(MediaFactoryImpl.class);

	private ResourceInfoFactory resourceInfoFactory = new DefaultResourceInfoFactory();

	private FileBaseResourceInfoFactory fileBaseResourceInfoFactory;
	
	@Inject
	private MimeTypeDetector mimeTypeDetector;
	
	@Inject
	private FileBaseResourceInfoWrapper fileBaseResourceInfoWrapper;
	
	@Inject
	private List<MediaLoader> mediaLoaders;

	public void setMaxHandleSizeByte(long maxHandleSizeByte) {
		this.maxHandleSizeByte = maxHandleSizeByte;
	}

	public void setMimeTypeDetector(MimeTypeDetector mimeTypeDetector) {
		this.mimeTypeDetector = mimeTypeDetector;
	}

	public void setFileBaseResourceInfoWrapper(FileBaseResourceInfoWrapper fileBaseResourceInfoWrapper) {
		this.fileBaseResourceInfoWrapper = fileBaseResourceInfoWrapper;
	}

	public void setMediaLoaders(List<MediaLoader> mediaLoaders) {
		this.mediaLoaders = mediaLoaders;
	}

	@PostConstruct
	//@Override
	public void init() throws Exception {

		fileBaseResourceInfoFactory = new DefaultFileBaseResourceInfoFactory(mimeTypeDetector);
	}
	
	@Override
	public Media make(byte[] data, String mimeType, Date lastModified, TemporaryResources temporaryResources) throws IOException {
		Assert.notNull(data);
		Assert.hasText(mimeType);
		Assert.notNull(temporaryResources);
		
		if (lastModified == null) {
			lastModified = new Date();
		}
		
		ResourceInfo resourceInfo = resourceInfoFactory.make(data, mimeType, lastModified);
		return make(resourceInfo, temporaryResources);
	}

	@Override
	public Media make(InputStream inputStream, String mimeType, Date lastModified, TemporaryResources temporaryResources) throws IOException {
		Assert.notNull(inputStream);
		Assert.hasText(mimeType);
		Assert.notNull(temporaryResources);
		
		if (lastModified == null) {
			lastModified = new Date();
		}
		
		try{
			byte[] data = toByteArrayWithSizeLimit(inputStream);
			ResourceInfo resourceInfo = resourceInfoFactory.make(data, mimeType, lastModified);
			return make(resourceInfo, temporaryResources);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	@Override
	public Media make(File file, TemporaryResources temporaryResources) throws IOException {
		Assert.notNull(file);
		Assert.notNull(temporaryResources);
		
		ResourceInfo resourceInfo = fileBaseResourceInfoFactory.make(file);
		return make(resourceInfo, temporaryResources);
	}

	@Override
	public Media make(URL url, TemporaryResources temporaryResources) throws IOException {
		Assert.isTrue("http".equals(url.getProtocol()) || "https".equals(url.getProtocol()), "Only support the http and https protocol.");
		Assert.notNull(temporaryResources);
		
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		
		String mimeType = connection.getContentType();
		Date lastModified = null;
		
		long lastModifiedLong = connection.getLastModified();
		if (lastModifiedLong == 0){
			lastModified = new Date();
		}else{
			lastModified = new Date(lastModifiedLong);
		}
		
		InputStream in = null;
		try {
			in = connection.getInputStream();
			byte[] data = toByteArrayWithSizeLimit(in);
			ResourceInfo resourceInfo = resourceInfoFactory.make(data, mimeType, lastModified);
			return make(resourceInfo, temporaryResources);
			
		} finally {
			IOUtils.closeQuietly(in);
			connection.disconnect();
		}
	}

	@Override
	public Media make(ResourceInfo resourceInfo, TemporaryResources temporaryResources) throws IOException {
		Assert.notNull(resourceInfo);
		Assert.notNull(temporaryResources);
		Assert.isTrue(resourceInfo.getContentLength() > 0, "ResourceInfo content should not empty.");
		
		if (resourceInfo.getContentLength() > maxHandleSizeByte){
			throw new MediaDataSizeLimitExceededException(
					String.format("Max supports %s bytes, actually is %s bytes.", 
							maxHandleSizeByte, resourceInfo.getContentLength()));
		}
		
		FileBaseResourceInfo fileBaseResourceInfo = fileBaseResourceInfoWrapper.wrap(
				resourceInfo, temporaryResources);
		
		//try{
		Media media = null;

		for (MediaLoader mediaLoader : mediaLoaders){
			if (mediaLoader.support(resourceInfo.getMimeType())){
				media = mediaLoader.load(fileBaseResourceInfo);
				break;
			}
		}

		if (media == null){
			throw new UnsupportedMediaException(
					String.format("Does not support resource with mime type %s.", 
							resourceInfo.getMimeType()));
		}

		return media;
	}
	
	private byte[] toByteArrayWithSizeLimit(InputStream inputStream) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		int totalReadBytes = 0;
		byte[] buffer = new byte[16 * 1024];

		while (true) {
			int readBytes = inputStream.read(buffer);
			if (readBytes < 0) {
				break;
			}
			totalReadBytes += readBytes;
			if (totalReadBytes > maxHandleSizeByte) {
				throw new MediaDataSizeLimitExceededException(
					String.format("Max supports %s bytes, actually is %s bytes.", 
							maxHandleSizeByte, totalReadBytes));
			}
			out.write(buffer, 0, readBytes);
		}

		byte[] data = out.toByteArray();
		out.close();
		return data;
	}
}
