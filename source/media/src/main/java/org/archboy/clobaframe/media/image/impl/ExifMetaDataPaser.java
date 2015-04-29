package org.archboy.clobaframe.media.image.impl;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import org.archboy.clobaframe.media.MetaData;
import org.archboy.clobaframe.media.impl.MetaDataParser;
import org.archboy.clobaframe.media.image.Image;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.springframework.util.Assert;

/**
 *
 * Exif directory.
 * see: http://www.awaresystems.be/imaging/tiff/tifftags/privateifd/exif.html
 * 
 * @author yang
 */
public class ExifMetaDataPaser implements MetaDataParser {

//	private final static String CONTENT_TYPE_IMAGE_JPEG = "image/jpeg";
//	private final static String CONTENT_TYPE_IMAGE_TIFF = "image/tiff";
	
	private static final DecimalFormat decimalPoint2Formatter = new DecimalFormat("0.0##");
	private static final DecimalFormat decimalPoint1Formatter = new DecimalFormat("0.#");
	private static final SimpleDateFormat noTimezoneFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	@Override
	public MetaData parse(Object object) {
		Assert.isTrue(object instanceof FileBaseResourceInfo, 
				"Support FileBaseResourceInfo class only.");
		
		FileBaseResourceInfo resourceInfo = (FileBaseResourceInfo)object;
		
		try {
			File file = resourceInfo.getFile();
			Metadata metadata = ImageMetadataReader.readMetadata(file);

			MetaData metaData = new MetaData();
			
			Iterator<Directory> iterator = metadata.getDirectories().iterator();
			while(iterator.hasNext()){
				Directory directory = iterator.next();
				
				if (directory instanceof ExifIFD0Directory){
					handleExifIFD0Directory(metaData, directory);
				}else if (directory instanceof ExifSubIFDDirectory) {
					handleExifSubIfDirectory(metaData, directory);
				}else if (directory instanceof GpsDirectory) {
					handleGpsDirectory(metaData, directory);
				}
			}
			
			return metaData;
			
		} catch (ImageProcessingException ex) {
			//
		} catch (IOException ex){
			//
		} catch (MetadataException ex){
			//
		}
		
		return null;
	}
	
	private void handleGpsDirectory(MetaData metaData, Directory directory) throws MetadataException{
			
		GpsDirectory gd = (GpsDirectory)directory;
		GeoLocation geoLocation = gd.getGeoLocation();

		if (geoLocation != null){
			metaData.put(Image.MetaName.GpsLongitude, geoLocation.getLongitude());
			metaData.put(Image.MetaName.GpsLatitude, geoLocation.getLatitude());
		}
	}
	
	private void handleExifSubIfDirectory(MetaData metaData, Directory directory) throws MetadataException{
		
		if (directory.containsTag(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)) {
			metaData.put(Image.MetaName.DateTimeOriginal, getDateValue(directory, ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
		}
		
		metaData.put(Image.MetaName.ExposureTime, getStringValue(directory, ExifSubIFDDirectory.TAG_EXPOSURE_TIME));
		
		if (directory.containsTag(ExifSubIFDDirectory.TAG_FOCAL_LENGTH)) {
			Rational rational = directory.getRational(ExifSubIFDDirectory.TAG_FOCAL_LENGTH);
			if (rational != null){
				metaData.put(Image.MetaName.FocalLength, decimalPoint2Formatter.format(rational.doubleValue()));
			}
		}
		
		if (directory.containsTag(ExifSubIFDDirectory.TAG_FNUMBER)) {
			Rational rational = directory.getRational(ExifSubIFDDirectory.TAG_FNUMBER);
			if (rational != null){
				metaData.put(Image.MetaName.fNumber, decimalPoint1Formatter.format(rational.doubleValue()));
			}
		}
		
		if (directory.containsTag(ExifSubIFDDirectory.TAG_FLASH)){
			Integer val = getIntValue(directory, ExifSubIFDDirectory.TAG_FLASH);
			metaData.put(Image.MetaName.Flash, translateFlash(val));
		}
		
		if (directory.containsTag(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT)){
			Integer val = getIntValue(directory, ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
			metaData.put(Image.MetaName.ISOSpeedRatings, translateISOSpeedRatings(val));
		}
	}
	
	private void handleExifIFD0Directory(MetaData metaData, Directory directory) throws MetadataException{
		
		metaData.put(Image.MetaName.Make, getStringValue(directory, ExifIFD0Directory.TAG_MAKE));
		metaData.put(Image.MetaName.Model, getStringValue(directory, ExifIFD0Directory.TAG_MODEL));
		metaData.put(Image.MetaName.Software, getStringValue(directory, ExifIFD0Directory.TAG_SOFTWARE));

		// Date/Time Original overrides value from ExifDirectory.TAG_DATETIME
		// Unless we have GPS time we don't know the time zone so date must be set
		// as ISO 8601 datetime without timezone suffix (no Z or +/-)
		if (directory.containsTag(ExifIFD0Directory.TAG_DATETIME)) {
			metaData.put(Image.MetaName.DateTimeOriginal, getDateValue(directory, ExifIFD0Directory.TAG_DATETIME));
		}		

		if (directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)){
			Integer val = getIntValue(directory, ExifIFD0Directory.TAG_ORIENTATION);
			metaData.put(Image.MetaName.Orientation, translateOrientation(val));
		}
	
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
		if (exifDirectory.containsTag(tagType)) {
			return exifDirectory.getString(tagType);
		}else{
			return null;
		}
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
