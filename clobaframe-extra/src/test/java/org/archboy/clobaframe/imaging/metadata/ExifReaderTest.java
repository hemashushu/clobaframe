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
package org.archboy.clobaframe.imaging.metadata;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.archboy.clobaframe.imaging.Image;
import org.archboy.clobaframe.imaging.ImageFactory;

import static org.junit.Assert.*;

/**
 *
 * @author young
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class ExifReaderTest {

//	private static final String DEFAULT_SAMPLE_IMAGE_FOLDER = "sample/image/";
//	private String sampleImageFolder = DEFAULT_SAMPLE_IMAGE_FOLDER;

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private ExifReader exifReader;

	@Autowired
	private ImageFactory imageFactory;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetMetaData() throws IOException {
		Image image1 = imageFactory.makeImage(getFileByName("sample/image/1.jpg"));
		ExifMetadata metadata1 = exifReader.getMetaData(image1);
		assertNotNull(metadata1);

		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(2011, 9, 16, 14, 46, 40); // the month is 0-base
		long timeExp = calendar.getTime().getTime();
		long timeAct = metadata1.getDateTimeOriginal().getTime();
		long timeSpan = Math.abs(timeExp - timeAct);

		assertTrue(timeSpan<1000);
		assertEquals("1/120", metadata1.getExposureTime());
		assertFalse(exifReader.translateFlash(metadata1.getFlash()));
		assertEquals("2.8", metadata1.getFNumber());
		assertEquals("3.85", metadata1.getFocalLength());
		assertEquals(new Integer(125), metadata1.getISOSpeedRatings());
		assertEquals("Apple", metadata1.getMake());
		assertEquals("iPhone 4", metadata1.getModel());
		assertEquals(ExifReader.Orientation.Normal, exifReader.translateOrientation(metadata1.getOrientation()));
		assertTrue(Math.abs(metadata1.getGpsLatitude() - 22.5633F) < 0.01);
		assertTrue(Math.abs(metadata1.getGpsLongitude() - 113.8795F) < 0.01) ;

		Image image2 = imageFactory.makeImage(getFileByName("sample/image/2.jpg"));
		ExifMetadata metadata2 = exifReader.getMetaData(image2);
		assertNotNull(metadata2);
		assertEquals(ExifReader.Orientation.Rotate90CW, exifReader.translateOrientation(metadata2.getOrientation()));

		Image image3 = imageFactory.makeImage(getFileByName("sample/image/3.png"));
		ExifMetadata metadata3 = exifReader.getMetaData(image3);
		assertNull(metadata3);
	}

	/**
	 *
	 * @param fileName Relate to the 'src/test/resource' folder.
	 * @return
	 * @throws IOException
	 */
	private File getFileByName(String fileName) throws IOException{
		Resource resource = resourceLoader.getResource(fileName);
		return resource.getFile();
	}
}
