package org.archboy.clobaframe.media.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.media.Media;
import org.archboy.clobaframe.media.MediaFactory;
import org.archboy.clobaframe.media.MediaDataSizeLimitExceededException;
import org.archboy.clobaframe.media.MediaLoader;
import org.archboy.clobaframe.media.MediaNotSupportException;
import org.archboy.clobaframe.media.MetaData;
import org.archboy.clobaframe.media.MetaDataParser;
import org.archboy.clobaframe.media.image.impl.DefaultImageFromFactory;
import org.archboy.clobaframe.media.image.impl.ImageLoaderImpl;
import org.archboy.clobaframe.webio.ContentTypeAnalyzer;
import org.archboy.clobaframe.webio.ResourceInfo;
import org.archboy.clobaframe.webio.ResourceInfoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
@Component
public class MediaFactoryImpl implements MediaFactory{

	// default 32 MB
	private static final long DEFAULT_MAX_HANDLE_SIZE = 32L * 1024 * 1024;
	private long maxHandleSize = DEFAULT_MAX_HANDLE_SIZE;

	private Logger logger = LoggerFactory.getLogger(ImageLoaderImpl.class);

	@Autowired
	private ResourceInfoFactory resourceInfoFactory;
	
	@Autowired
	private List<MediaLoader> mediaLoaders;

	@Autowired
	private List<MetaDataParser> metaDataParsers;
	
	@Value("${media.maxHandleSize}")
	public void setMaxHandleSizeKB(int maxHandleSizeKB) {
		this.maxHandleSize = maxHandleSizeKB * 1024L;
	}
	
	@Override
	public Media make(byte[] data, String contentType, Date lastModified) throws IOException {
//		if (data.length > maxHandleSize) {
//			throw new MediaDataSizeLimitExceededException();
//		}
		if (lastModified == null) {
			lastModified = new Date();
		}
		
		ResourceInfo resourceInfo = resourceInfoFactory.make(data, contentType, lastModified);
		return make(resourceInfo);
	}

	@Override
	public Media make(InputStream inputStream, String contentType, Date lastModified) throws IOException {
		
		if (lastModified == null) {
			lastModified = new Date();
		}
		
		try{
			byte[] data = toByteArray(inputStream);
			ResourceInfo resourceInfo = resourceInfoFactory.make(data, contentType, lastModified);
			return make(resourceInfo);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	@Override
	public Media make(File file) throws IOException {
//		if (file.length() > maxHandleSize) {
//			throw new MediaDataSizeLimitExceededException();
//		}
		
		ResourceInfo resourceInfo = resourceInfoFactory.make(file);
		return make(resourceInfo);
	}

	@Override
	public Media make(URL url) throws IOException {
		Assert.isTrue("http".equals(url.getProtocol()) || "https".equals(url.getProtocol()));

		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		
		String contentType = connection.getContentType();
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
			byte[] data = toByteArray(in);
			ResourceInfo resourceInfo = resourceInfoFactory.make(data, contentType, lastModified);
			return make(resourceInfo);
			
		} finally {
			IOUtils.closeQuietly(in);
			connection.disconnect();
		}
	}

	@Override
	public Media make(ResourceInfo resourceInfo) throws IOException {
		Assert.isTrue(resourceInfo.getContentLength() > 0);
		
		if (resourceInfo.getContentLength() > maxHandleSize){
			throw new MediaDataSizeLimitExceededException();
		}
		
		Media media = null;
		
		for (MediaLoader mediaLoader : mediaLoaders){
			if (mediaLoader.support(resourceInfo.getContentType())){
				media = mediaLoader.load(resourceInfo);
				break;
			}
		}
		
		if (media == null){
			throw new MediaNotSupportException("Content type is: " + resourceInfo.getContentType());
		}
		
		if (media instanceof AbstractMedia){
			for(MetaDataParser metaDataParser : metaDataParsers){
				if (metaDataParser.support(media.getContentType())){
					MetaData metaData = metaDataParser.parse(media);
					((AbstractMedia)media).setMetaData(metaData);
					break;
				}
			}
		}
		
		return media;
//
//		// OpenJDK current support bmp, jpg, wbmp, jpeg, png, gif
//		InputStream in = null;
//		ImageInputStream stream = null;
//		ImageReader reader = null;
//		Image image = null;
//
//		try {
//			in = new ByteArrayInputStream(imageData);
//			stream = ImageIO.createImageInputStream(in);
//
//			Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
//			if (!readers.hasNext()) {
//				throw new IOException("The image format is not supported.");
//			}
//
//			reader = readers.next();
//			Image.Format format = Image.Format.fromFormatName(reader.getFormatName());
//
//			if (format == null) {
//				throw new IOException("The image format is not supported.");
//			}
//
//			reader.setInput(stream);
//			BufferedImage bufferedImage = reader.read(0);
//
//			image = new DefaultImageFromFactory(format, bufferedImage, imageData);
//
//		} finally {
//			closeQuietly(reader);
//			//IOUtils.closeQuietly(stream); // for java 7
//			closeQuietly(stream);
//			IOUtils.closeQuietly(in);
//		}
//
//		return image;
	}

//	/*
//	 * java 6 ImageInputStream does not implement Closable interface
//	 */
//	private void closeQuietly(ImageInputStream stream) {
//		if (stream != null) {
//			try {
//				stream.close();
//			} catch (IOException e) {
//				// ignore
//			}
//		}
//	}
//
//	private void closeQuietly(ImageReader reader) {
//		if (reader != null) {
//			reader.dispose();
//		}
//	}
	
	private byte[] toByteArray(InputStream inputStream) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		int totalReadBytes = 0;
		byte[] buffer = new byte[16 * 1024];

		while (true) {
			int readBytes = inputStream.read(buffer);
			if (readBytes < 0) {
				break;
			}
			totalReadBytes += readBytes;
			if (totalReadBytes > maxHandleSize) {
				throw new MediaDataSizeLimitExceededException();
			}
			out.write(buffer, 0, readBytes);
		}

		byte[] data = out.toByteArray();
		out.close();
		return data;
	}
}
