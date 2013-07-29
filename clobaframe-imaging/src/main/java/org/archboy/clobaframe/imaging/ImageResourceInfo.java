package org.archboy.clobaframe.imaging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import org.archboy.clobaframe.webio.ResourceContent;
import org.archboy.clobaframe.webio.ResourceInfo;
import org.archboy.clobaframe.webio.impl.DefaultResourceContent;

/**
 *
 * @author arch
 */
public class ImageResourceInfo implements ResourceInfo {

	private byte[] content;
	private String contentType;
	private int contentLength;
	private Date lastModified;

	public ImageResourceInfo(Image image){
		this(image, new Date());
	}

	public ImageResourceInfo(Image image, Date lastModified) {
		Image.Format format = image.getFormat();
		switch(format){
			case JPEG:
				contentType = "image/jpeg";
				break;
			case PNG:
				contentType = "image/png";
				break;
			case BMP:
				contentType = "image/bmp";
				break;
			case GIF:
				contentType = "image/gif";
				break;
		}

		if (contentType == null) {
			throw new IllegalArgumentException("Unsupported image type.");
		}

		content = image.getImageData();
		contentLength = content.length;
		this.lastModified = lastModified;
	}

	@Override
	public long getContentLength() {
		return contentLength;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public ResourceContent getContentSnapshot() throws IOException {
		return new DefaultResourceContent(content);
	}

	@Override
	public ResourceContent getContentSnapshot(long start, long length) throws IOException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				content, (int)start, (int)length);
		return new DefaultResourceContent(inputStream, length);
	}

	@Override
	public boolean isContentSeekable() {
		return true;
	}

	@Override
	public Date getLastModified() {
		return lastModified;
	}
}
