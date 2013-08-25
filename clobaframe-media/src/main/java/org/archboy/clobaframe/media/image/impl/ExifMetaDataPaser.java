package org.archboy.clobaframe.media.image.impl;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.GpsDirectory;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Date;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.media.Media;
import org.archboy.clobaframe.media.MetaData;
import org.archboy.clobaframe.media.MetaDataParser;
import org.archboy.clobaframe.media.image.Image;
import org.archboy.clobaframe.webio.ResourceContent;
import org.archboy.clobaframe.webio.ResourceInfo;
import org.springframework.stereotype.Component;

/**
 *
 * Exif directory.
 * see: http://www.awaresystems.be/imaging/tiff/tifftags/privateifd/exif.html
 * 
 * @author yang
 */
@Component
public class ExifMetaDataPaser implements MetaDataParser {

	private final static String CONTENT_TYPE_IMAGE_JPEG = "image/jpeg";
	private final static String CONTENT_TYPE_IMAGE_TIFF = "image/tiff";
	
	private static final DecimalFormat decimalPoint2formatter = new DecimalFormat("0.0##");
	private static final DecimalFormat decimalPoint1formatter = new DecimalFormat("0.#");
	
	@Override
	public boolean support(String contentType) {
		return (CONTENT_TYPE_IMAGE_JPEG.equals(contentType) || 
				CONTENT_TYPE_IMAGE_TIFF.equals(contentType));
	}

	@Override
	public MetaData parse(Media media) {
		Image image = (Image)media;
		ResourceInfo resourceInfo = image.getResourceInfo();
		ResourceContent resourceContent = null;
		
		try {
			resourceContent = resourceInfo.getContentSnapshot();
			InputStream in = resourceContent.getInputStream();
			BufferedInputStream bin = new BufferedInputStream(in);
			Metadata metadata = ImageMetadataReader.readMetadata(bin);

			Class<?> exifClass = ExifDirectory.class;
			Class<?> gpsClass = GpsDirectory.class;
			if (metadata.containsDirectory(exifClass)){
				Directory exifDirectory = metadata.getDirectory(exifClass);
				Directory gpsDirectory = null;
				if (metadata.containsDirectory(gpsClass)) {
					gpsDirectory = metadata.getDirectory(gpsClass);
				}
				
				return makeFromDirectory(exifDirectory, gpsDirectory);
			}
		} catch (ImageProcessingException ex) {
			//
		} catch (IOException ex){
			//
		} catch (MetadataException ex){
			//
		} finally {
			IOUtils.closeQuietly(resourceContent);
		}
		
		return null;
	}
	
	private MetaData makeFromDirectory(Directory exifDirectory, Directory gpsDirectory) throws MetadataException{
		
		MetaData metaData = new MetaData();
		
		metaData.put(Image.MetaName.ExposureTime, getStringValue(exifDirectory, ExifDirectory.TAG_EXPOSURE_TIME));
		metaData.put(Image.MetaName.Make, getStringValue(exifDirectory, ExifDirectory.TAG_MAKE));
		metaData.put(Image.MetaName.Model, getStringValue(exifDirectory, ExifDirectory.TAG_MODEL));
		metaData.put(Image.MetaName.Software, getStringValue(exifDirectory, ExifDirectory.TAG_SOFTWARE));
		metaData.put(Image.MetaName.DateTimeOriginal, getDateValue(exifDirectory, ExifDirectory.TAG_DATETIME_ORIGINAL));
		
		if (exifDirectory.containsTag(ExifDirectory.TAG_FOCAL_LENGTH)) {
			Rational rational = exifDirectory.getRational(ExifDirectory.TAG_FOCAL_LENGTH);
			metaData.put(Image.MetaName.FocalLength, decimalPoint2formatter.format(rational.doubleValue()));
		}
		
		if (exifDirectory.containsTag(ExifDirectory.TAG_FNUMBER)) {
			Rational rational = exifDirectory.getRational(ExifDirectory.TAG_FNUMBER);
			metaData.put(Image.MetaName.fNumber, decimalPoint1formatter.format(rational.doubleValue()));
		}
		
		if (exifDirectory.containsTag(ExifDirectory.TAG_FLASH)){
			Integer val = getIntValue(exifDirectory, ExifDirectory.TAG_FLASH);
			metaData.put(Image.MetaName.Flash, translateFlash(val));
		}
		
		if (exifDirectory.containsTag(ExifDirectory.TAG_ISO_EQUIVALENT)){
			Integer val = getIntValue(exifDirectory, ExifDirectory.TAG_ISO_EQUIVALENT);
			metaData.put(Image.MetaName.ISOSpeedRatings, translateISOSpeedRatings(val));
		}
		
		if (exifDirectory.containsTag(ExifDirectory.TAG_ORIENTATION)){
			Integer val = getIntValue(exifDirectory, ExifDirectory.TAG_ORIENTATION);
			metaData.put(Image.MetaName.Orientation, translateOrientation(val));
		}
		
		if (gpsDirectory != null){
			
			if (gpsDirectory.containsTag(GpsDirectory.TAG_GPS_LONGITUDE)){
				float value = getDegree(gpsDirectory, GpsDirectory.TAG_GPS_LONGITUDE);
				if ("E".equals(gpsDirectory.getString(GpsDirectory.TAG_GPS_LONGITUDE_REF))){
					metaData.put(Image.MetaName.GpsLongitude, value);
				}else{
					metaData.put(Image.MetaName.GpsLongitude, -value);
				}
			}
			
			if (gpsDirectory.containsTag(GpsDirectory.TAG_GPS_LATITUDE)) {
				float value = getDegree(gpsDirectory, GpsDirectory.TAG_GPS_LATITUDE);
				if ("N".equals(gpsDirectory.getString(GpsDirectory.TAG_GPS_LATITUDE_REF))){
					metaData.put(Image.MetaName.GpsLatitude, value);
				}else{
					metaData.put(Image.MetaName.GpsLatitude, -value);
				}
			}
			
			if (gpsDirectory.containsTag(GpsDirectory.TAG_GPS_ALTITUDE)) {
				float value = gpsDirectory.getRational(GpsDirectory.TAG_GPS_ALTITUDE).floatValue();
				if (gpsDirectory.getInt(GpsDirectory.TAG_GPS_ALTITUDE_REF) == 0){
					metaData.put(Image.MetaName.GpsAltitude, value);
				}else{
					metaData.put(Image.MetaName.GpsAltitude, -value);
				}
			}
		} // end gps
		
		return metaData;
	}
	
	
	private static Image.Orientation translateOrientation(int orientation) {

		Image.Orientation value = null;

		switch (orientation) {
			case 0:
			case 1:
				value = Image.Orientation.Normal; // default value
				break;
			case 2:
				value = Image.Orientation.MirrorHorizontal;
				break;
			case 3:
				value = Image.Orientation.Rotate180CW;
				break;
			case 4:
				value = Image.Orientation.MirrorVertical;
				break;
			case 5:
				value = Image.Orientation.MirrorHorizontalAndRotate270CW;
				break;
			case 6:
				value = Image.Orientation.Rotate90CW;
				break;
			case 7:
				value = Image.Orientation.MirrorHorizontalAndRoate90CW;
				break;
			case 8:
				value = Image.Orientation.Rotate270CW;
				break;
		}

		return value;
	}

	private static boolean translateFlash(int flash) {
		return ((flash & 0x1) !=0);
	}
	
	private static int translateISOSpeedRatings(int iso){
		return (iso < 50? iso * 200:iso);
	}
	
	private static String getStringValue(Directory exifDirectory, int tagType) {
		return exifDirectory.getString(tagType);
	}

	private static Date getDateValue(Directory exifDirectory, int tagType) throws MetadataException {
		if (exifDirectory.containsTag(tagType)) {
			return exifDirectory.getDate(tagType);
		}else{
			return null;
		}
	}

	private static Integer getIntValue(Directory exifDirectory, int tagType) throws MetadataException {
		if (exifDirectory.containsTag(tagType)) {
			return exifDirectory.getInt(tagType);
		}else{
			return null;
		}
	}

	/**
	 * only for GPS directory.
	 * HoursMinutesSeconds see
	 * http://www.cipa.jp/english/hyoujunka/kikaku/pdf/DC-008-2010_E.pdf
	 *
	 * @param tagType
	 * @return
	 * @throws MetadataException
	 */
	private static Float getDegree(Directory gpsDirectory, int tagType) throws MetadataException {
		if (gpsDirectory.containsTag(tagType)){
			Rational[] components = gpsDirectory.getRationalArray(tagType);
			int deg = components[0].intValue();
			float min = components[1].floatValue();
			float sec = components[2].floatValue();
			return (float) deg + (min + sec / 60F) / 60F;
		}else{
			return null;
		}
	}
}
