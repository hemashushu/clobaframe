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
package org.archboy.clobaframe.imaging.metadata.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.archboy.clobaframe.imaging.Image;
import org.archboy.clobaframe.imaging.metadata.ExifMetadata;
import org.archboy.clobaframe.imaging.metadata.ExifReader;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.GpsDirectory;
import java.util.Iterator;

/**
 *
 * @author young
 */
@Component
public class ExifReaderImpl implements ExifReader{


	@Override
	public ExifMetadata getMetaData(Image image) throws IOException {

		Image.Format format = image.getFormat();
		if (format == Image.Format.JPEG ||
				format == Image.Format.TIFF){
			byte[] data = image.getImageData();
			BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(data));
			try {
				Metadata metadata = ImageMetadataReader.readMetadata(in);

				Class<?> exifClass = ExifDirectory.class;
				Class<?> gpsClass = GpsDirectory.class;
				if (metadata.containsDirectory(exifClass)){
					Directory exifDirectory = metadata.getDirectory(exifClass);
					if (metadata.containsDirectory(gpsClass)) {
						Directory gpsDirectory = metadata.getDirectory(gpsClass);
						return new DefaultExifMetadata(exifDirectory, gpsDirectory);
					}else{
						return new DefaultExifMetadata(exifDirectory);
					}
				}
			} catch (ImageProcessingException ex) {
				throw new IOException(ex);
			} finally {
				IOUtils.closeQuietly(in);
			}
		}

		return null;
	}

	@Override
	public Orientation translateOrientation(int orientation) {

		Orientation value = Orientation.Normal; // default value
		if (orientation == 0) {
			return value;
		}

		switch (orientation) {
		case 1:
			value = Orientation.Normal;
			break;
		case 2:
			value = Orientation.MirrorHorizontal;
			break;
		case 3:
			value = Orientation.Rotate180CW;
			break;
		case 4:
			value = Orientation.MirrorVertical;
			break;
		case 5:
			value = Orientation.MirrorHorizontalAndRotate270CW;
			break;
		case 6:
			value = Orientation.Rotate90CW;
			break;
		case 7:
			value = Orientation.MirrorHorizontalAndRoate90CW;
			break;
		case 8:
			value = Orientation.Rotate270CW;
			break;
		}

		return value;
	}

	@Override
	public boolean translateFlash(int flash) {
		return ((flash & 0x1) !=0);
	}
}
