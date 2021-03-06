package org.archboy.clobaframe.media.image.impl;

import java.awt.image.BufferedImage;
import java.util.Date;
import org.archboy.clobaframe.media.MetaData;
import org.archboy.clobaframe.media.image.OutputSettings;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.impl.ByteArrayResourceInfo;

public class DefaultImage extends AbstractImage {

	private MetaData metaData;
	private Date lastModified;
	private Format format;
	private BufferedImage bufferedImage;

	/**
	 * 
	 * @param lastModified NULL for the current date.
	 * @param format
	 * @param bufferedImage 
	 */
	public DefaultImage( //String contentType, 
			Date lastModified, Format format, BufferedImage bufferedImage) {
		this.lastModified = (lastModified==null?new Date():lastModified);
		this.format = format;
		this.bufferedImage = bufferedImage;
	}

	@Override
	public Format getFormat() {
		return format;
	}

	@Override
	public int getWidth() {
		return bufferedImage.getWidth();
	}

	@Override
	public int getHeight() {
		return bufferedImage.getHeight();
	}

	@Override
	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	@Override
	public MetaData getMetaData() {
		return metaData;
	}

	@Override
	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	@Override
	public ResourceInfo getResourceInfo() {
		return getResourceInfo(lastModified);
	}

	@Override
	public ResourceInfo getResourceInfo(Date lastModified) {
		OutputSettings outputSettings = new OutputSettings();
		if (format == Format.PNG){
			outputSettings.setOutputEncoding(OutputSettings.OutputEncoding.PNG);
		}else if(format == Format.JPEG){
			outputSettings.setOutputEncoding(OutputSettings.OutputEncoding.JPEG);
		}else{
			throw new IllegalArgumentException(
					String.format("The image format [%s] does not support yet.",
						format.toString())
					);
		}
		return getResourceInfo(lastModified, outputSettings);
	}

	@Override
	public ResourceInfo getResourceInfo(Date lastModified, OutputSettings outputSettings) {
		byte[] data = getData(bufferedImage, outputSettings);
		
		String mimeType = 
				(outputSettings.getOutputEncoding() == OutputSettings.OutputEncoding.JPEG) ?
				ImageLoader.MIME_TYPE_IMAGE_JPEG :
				ImageLoader.MIME_TYPE_IMAGE_PNG;
		
		return new ByteArrayResourceInfo(data, mimeType, 
				(lastModified ==null ? this.lastModified:lastModified));
	}
}
