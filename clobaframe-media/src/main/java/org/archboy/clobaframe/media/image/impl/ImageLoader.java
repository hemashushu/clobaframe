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
import org.apache.commons.io.IOUtils;
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

	public static final String MIME_TYPE_IMAGE_JPEG = "image/jpeg";
	public static final String MIME_TYPE_IMAGE_PNG = "image/png";
	public static final String MIME_TYPE_IMAGE_BMP = "image/bmp";
	public static final String MIME_TYPE_IMAGE_GIF = "image/gif";
	
	private List<String> supportMimeTypes = Arrays.asList(MIME_TYPE_IMAGE_JPEG, 
			MIME_TYPE_IMAGE_PNG,
			MIME_TYPE_IMAGE_GIF, 
			MIME_TYPE_IMAGE_BMP);
	
	@Override
	public boolean support(String mimeType) {
		for (String supportMimeType : supportMimeTypes){
			if (supportMimeType.equals(mimeType)){
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
		ImageInputStream in = null;
		ImageReader imageReader = null;
		Image image = null;

		try {
			//resourceContent = resourceInfo.getContentSnapshot();
			//in = resourceContent.getInputStream();
			//in = resourceInfo.getInputStream();
			File file = fileBaseResourceInfo.getFile();
			in = ImageIO.createImageInputStream(file);

			Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			if (!readers.hasNext()) {
				throw new IOException("The image format is not supported.");
			}

			imageReader = readers.next();
			Image.Format format = Image.Format.fromFormatName(imageReader.getFormatName());

			if (format == null) {
				throw new IOException("The image format is not supported.");
			}

			imageReader.setInput(in);
			
			// Optionally, listen for read warnings, progress, etc.
//        reader.addIIOReadWarningListener(...);
//        reader.addIIOReadProgressListener(...);
//
//        ImageReadParam param = reader.getDefaultReadParam();

        // Optionally, control read settings like sub sampling, source region or destination etc.
//        param.setSourceSubsampling(...);
//        param.setSourceRegion(...);
//        param.setDestination(...);
		
			BufferedImage bufferedImage = imageReader.read(0);

			image = new DefaultImageFromFactory(fileBaseResourceInfo, format, bufferedImage);

			if (image.getFormat() == Image.Format.JPEG){
				MetaDataParser metaDataParser = new ExifMetaDataPaser();
				image.setMetaData(metaDataParser.parse(fileBaseResourceInfo));
			}
			
		} finally {
			closeQuietly(imageReader);
			IOUtils.closeQuietly(in); // for java 7
			//closeQuietly(stream); // for java 6
			//IOUtils.closeQuietly(in);
			//IOUtils.closeQuietly(resourceContent);
		}

		return image;
	}
	
	/*
	 * java 6 ImageInputStream does not implement Closable interface
	 */
//	private void closeQuietly(ImageInputStream stream) {
//		if (stream != null) {
//			try {
//				stream.close();
//			} catch (IOException e) {
//				// ignore
//			}
//		}
//	}

	private void closeQuietly(ImageReader reader) {
		if (reader != null) {
			reader.dispose();
		}
	}	
}
