package org.archboy.clobaframe.media.image;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import org.archboy.clobaframe.media.MediaFactory;
import org.archboy.clobaframe.media.MetaData;
import org.archboy.clobaframe.io.ResourceInfoFactory;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author yang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class ExifMetaDataTest {
	
	private static final String DEFAULT_SAMPLE_IMAGE_FOLDER = "sample/image/";
	private String sampleImageFolder = DEFAULT_SAMPLE_IMAGE_FOLDER;
	
	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private MediaFactory mediaFactory;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testGetMetaData() throws IOException {
		Image image1 = (Image)mediaFactory.make(getFileByName("1.jpg"));
		MetaData metadata1 = image1.getMetaData();
		assertNotNull(metadata1);

		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(2011, 9, 16, 14, 46, 40); // the month is 0-base
		long timeExp = calendar.getTime().getTime();
		long timeAct = ((Date)metadata1.get(Image.MetaName.DateTimeOriginal)).getTime();
		long timeSpan = Math.abs(timeExp - timeAct);
		assertTrue(timeSpan<1000);
		
		assertEquals("1/120", metadata1.get(Image.MetaName.ExposureTime));
		assertEquals(Boolean.FALSE, metadata1.get(Image.MetaName.Flash));
		assertEquals("2.8", metadata1.get(Image.MetaName.fNumber));
		assertEquals("3.85", metadata1.get(Image.MetaName.FocalLength));
		assertEquals(new Integer(125), metadata1.get(Image.MetaName.ISOSpeedRatings));
		assertEquals("Apple", metadata1.get(Image.MetaName.Make));
		assertEquals("iPhone 4", metadata1.get(Image.MetaName.Model));
		assertEquals(Image.Orientation.Normal, metadata1.get(Image.MetaName.Orientation));
		assertTrue(Math.abs((Double)metadata1.get(Image.MetaName.GpsLatitude) - 22.5633F) < 0.01);
		assertTrue(Math.abs((Double)metadata1.get(Image.MetaName.GpsLongitude) - 113.8795F) < 0.01) ;

		Image image2 = (Image)mediaFactory.make(getFileByName("2.jpg"));
		MetaData metadata2 = image2.getMetaData();
		assertNotNull(metadata2);
		assertEquals(Image.Orientation.Rotate90CW, metadata2.get(Image.MetaName.Orientation));

		Image image3 = (Image)mediaFactory.make(getFileByName("3.png"));
		MetaData metadata3 = image3.getMetaData();
		assertNull(metadata3);
	}
	
	private File getFileByName(String fileName) throws IOException{
		Resource resource = resourceLoader.getResource(sampleImageFolder + fileName);
		return resource.getFile();
	}
}
