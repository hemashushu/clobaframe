package org.archboy.clobaframe.media.image.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.inject.Named;
import org.archboy.clobaframe.media.Media;
import org.archboy.clobaframe.media.MediaLoader;
import org.archboy.clobaframe.media.image.Image;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.media.impl.MetaDataParser;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 *
 */
@Named
public class ImageLoader implements MediaLoader {

	public static final String CONTENT_TYPE_IMAGE_JPEG = "image/jpeg";
	public static final String CONTENT_TYPE_IMAGE_PNG = "image/png";
	public static final String CONTENT_TYPE_IMAGE_BMP = "image/bmp";
	public static final String CONTENT_TYPE_IMAGE_GIF = "image/gif";
	
	private List<String> supportContentTypes = Arrays.asList(
			CONTENT_TYPE_IMAGE_JPEG, 
			CONTENT_TYPE_IMAGE_PNG,
			CONTENT_TYPE_IMAGE_GIF, 
			CONTENT_TYPE_IMAGE_BMP);
	
	@Override
	public boolean support(String contentType) {
		for (String supportContentType : supportContentTypes){
			if (supportContentType.equals(contentType)){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public Media load(FileBaseResourceInfo fileBaseResourceInfo) throws IOException {
		Assert.notNull(fileBaseResourceInfo);
		
		// OpenJDK current support bmp, jpg, wbmp, jpeg, png, gif
		
		//ResourceContent resourceContent = null;
		//InputStream in = null;
		ImageInputStream stream = null;
		ImageReader reader = null;
		Image image = null;

		try {
			//resourceContent = resourceInfo.getContentSnapshot();
			//in = resourceContent.getInputStream();
			//in = resourceInfo.getInputStream();
			File file = fileBaseResourceInfo.getFile();
			stream = ImageIO.createImageInputStream(file);

			Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
			if (!readers.hasNext()) {
				throw new IOException("The image format is not supported.");
			}

			reader = readers.next();
			Image.Format format = Image.Format.fromFormatName(reader.getFormatName());

			if (format == null) {
				throw new IOException("The image format is not supported.");
			}

			reader.setInput(stream);
			BufferedImage bufferedImage = reader.read(0);

			image = new DefaultImageFromFactory(fileBaseResourceInfo, format, bufferedImage);

			if (image.getFormat() == Image.Format.JPEG){
				MetaDataParser metaDataParser = new ExifMetaDataPaser();
				image.setMetaData(metaDataParser.parse(fileBaseResourceInfo));
			}
			
		} finally {
			closeQuietly(reader);
			//IOUtils.closeQuietly(stream); // for java 7
			closeQuietly(stream);
			//IOUtils.closeQuietly(in);
			//IOUtils.closeQuietly(resourceContent);
		}

		return image;
	}
	
	/*
	 * java 6 ImageInputStream does not implement Closable interface
	 */
	private void closeQuietly(ImageInputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	private void closeQuietly(ImageReader reader) {
		if (reader != null) {
			reader.dispose();
		}
	}	
}
