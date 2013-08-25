/*
 * Copyright 2011 Spark Young (sparkyoungs@gmail.com). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.archboy.clobaframe.media.image.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import org.archboy.clobaframe.media.MetaData;
import org.archboy.clobaframe.media.image.Image;
import org.archboy.clobaframe.media.image.OutputSettings;
import org.archboy.clobaframe.webio.ResourceInfo;
import org.archboy.clobaframe.webio.impl.ByteArrayResourceInfo;

public class DefaultImage extends AbstractImage implements Image {

	//private ResourceInfo resourceInfo;
	private MetaData metaData;
	private String contentType;
	private Date lastModified;
	
	private Format format;
	private BufferedImage bufferedImage;

	/**
	 * 
	 * @param contentType
	 * @param lastModified NULL for the current date.
	 * @param format
	 * @param bufferedImage 
	 */
	public DefaultImage(String contentType, Date lastModified, Format format, BufferedImage bufferedImage) {
		this.contentType = contentType;
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

//	@Override
//	public InputStream getInputStream() {

//	}
//
//	
//	@Override
//	public InputStream getInputStream(OutputSettings outputSettings) {
//		
//	}

	@Override
	public String getContentType() {
		return contentType;
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
		return new ByteArrayResourceInfo(data, contentType, 
				(lastModified ==null? this.lastModified:lastModified));
	}
}
