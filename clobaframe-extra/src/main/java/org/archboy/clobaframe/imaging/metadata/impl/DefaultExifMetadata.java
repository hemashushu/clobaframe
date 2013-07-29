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

import java.text.DecimalFormat;
import java.util.Date;
import org.archboy.clobaframe.imaging.metadata.ExifMetadata;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.GpsDirectory;

/**
 *
 * @author young
 */
public class DefaultExifMetadata implements ExifMetadata {

	private static final DecimalFormat decimalPoint2formatter = new DecimalFormat("0.0##");
	private static final DecimalFormat decimalPoint1formatter = new DecimalFormat("0.#");

	private Directory exifDirectory;
	private Directory gpsDirectory;

	public DefaultExifMetadata(Directory exifDirectory) {
		this.exifDirectory = exifDirectory;
	}

	public DefaultExifMetadata(Directory exifDirectory, Directory gpsDirectory) {
		this.exifDirectory = exifDirectory;
		this.gpsDirectory = gpsDirectory;
	}

	@Override
	public String getExposureTime() {
		return getStringValue(ExifDirectory.TAG_EXPOSURE_TIME);
	}

	@Override
	public String getMake() {
		return getStringValue(ExifDirectory.TAG_MAKE);
	}

	@Override
	public String getModel() {
		return getStringValue(ExifDirectory.TAG_MODEL);
	}

	@Override
	public String getSoftware() {
		return getStringValue(ExifDirectory.TAG_SOFTWARE);
	}

	@Override
	public String getFocalLength() {
		if (!exifDirectory.containsTag(ExifDirectory.TAG_FOCAL_LENGTH)) {
			return null;
		}

		try{
			Rational rational = exifDirectory.getRational(ExifDirectory.TAG_FOCAL_LENGTH);
			return decimalPoint2formatter.format(rational.doubleValue());
		}catch (MetadataException e){
			return null;
		}
	}

	@Override
	public String getFNumber() {
		if (!exifDirectory.containsTag(ExifDirectory.TAG_FNUMBER)) {
			return null;
		}

		try{
			Rational rational = exifDirectory.getRational(ExifDirectory.TAG_FNUMBER);
			return decimalPoint1formatter.format(rational.doubleValue());
		}catch (MetadataException e){
			return null;
		}
	}

	@Override
	public Integer getFlash() {
		return getIntValue(ExifDirectory.TAG_FLASH);
	}

	@Override
	public Integer getISOSpeedRatings() {
		Integer isoEquivalent = getIntValue(ExifDirectory.TAG_ISO_EQUIVALENT);
		if (isoEquivalent != null && isoEquivalent < 50) {
			return isoEquivalent * 200;
		}else{
			return isoEquivalent;
		}
	}

	@Override
	public Integer getOrientation() {
		return getIntValue(ExifDirectory.TAG_ORIENTATION);
	}

	@Override
	public Date getDateTimeOriginal() {
		return getDateValue(ExifDirectory.TAG_DATETIME_ORIGINAL);
	}

	@Override
	public Float getGpsLongitude() {
		if (gpsDirectory == null || !gpsDirectory.containsTag(GpsDirectory.TAG_GPS_LONGITUDE)) {
			return null;
		}

		float value = getDegree(GpsDirectory.TAG_GPS_LONGITUDE);
		if ("E".equals(gpsDirectory.getString(GpsDirectory.TAG_GPS_LONGITUDE_REF))){
			return value;
		}else{
			return -value;
		}
	}

	@Override
	public Float getGpsLatitude() {
		if (gpsDirectory == null || !gpsDirectory.containsTag(GpsDirectory.TAG_GPS_LATITUDE)) {
			return null;
		}

		float value = getDegree(GpsDirectory.TAG_GPS_LATITUDE);
		if ("N".equals(gpsDirectory.getString(GpsDirectory.TAG_GPS_LATITUDE_REF))){
			return value;
		}else{
			return -value;
		}
	}

	@Override
	public Float getGpsAltitude() {
		if (gpsDirectory == null || !gpsDirectory.containsTag(GpsDirectory.TAG_GPS_ALTITUDE)) {
			return null;
		}

		try{
			float value = gpsDirectory.getRational(GpsDirectory.TAG_GPS_ALTITUDE).floatValue();
			if (gpsDirectory.getInt(GpsDirectory.TAG_GPS_ALTITUDE_REF) == 0){
				return value;
			}else{
				return -value;
			}
		}catch(MetadataException e){
			return null;
		}
	}

	private String getStringValue(int tagType) {
		return exifDirectory.getString(tagType);
	}

	private Date getDateValue(int tagType) {
		try {
			if (exifDirectory.containsTag(tagType)) {
				return exifDirectory.getDate(tagType);
			}
		} catch (MetadataException e) {
			//
		}
		return null;
	}

	private Integer getIntValue(int tagType) {
		try {
			if (exifDirectory.containsTag(tagType)) {
				return exifDirectory.getInt(tagType);
			}
		} catch (MetadataException e) {
			//
		}
		return null;
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
	private Float getDegree(int tagType) {
		try{
			Rational[] components = gpsDirectory.getRationalArray(tagType);
			int deg = components[0].intValue();
			float min = components[1].floatValue();
			float sec = components[2].floatValue();
			return (float) deg + (min + sec / 60F) / 60F;
		}catch(MetadataException e) {
			return null;
		}
	}
}
