package org.archboy.clobaframe.media.image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.media.MediaFactory;
import org.archboy.clobaframe.media.image.impl.ImageLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import org.archboy.clobaframe.io.MimeTypeDetector;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.archboy.clobaframe.io.ResourceInfo;
import static org.junit.Assert.*;
import org.archboy.clobaframe.io.TemporaryResources;
import org.archboy.clobaframe.io.file.FileBaseResourceInfoFactory;
import org.archboy.clobaframe.io.file.impl.DefaultFileBaseResourceInfoFactory;
import org.archboy.clobaframe.io.impl.DefaultTemporaryResources;
import org.archboy.clobaframe.media.MetaData;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class ImageFactoryTest {

	private static final String DEFAULT_SAMPLE_IMAGE_FOLDER = "sample/image/";
	private String sampleImageFolder = DEFAULT_SAMPLE_IMAGE_FOLDER;

	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private MediaFactory mediaFactory;

	@Inject
	private MimeTypeDetector mimeTypeDetector;
	
	private FileBaseResourceInfoFactory fileBaseResourceInfoFactory;
	
	private TemporaryResources temporaryResources = new DefaultTemporaryResources();
	
	@Before
	public void setUp() throws Exception {
		fileBaseResourceInfoFactory = new DefaultFileBaseResourceInfoFactory(mimeTypeDetector);
	}

	@After
	public void tearDown() throws Exception {
		temporaryResources.close();
	}

	@Test
	public void testMakeImageFromFile() throws IOException {
		File file1 = getFileByName("test.jpg");
		Image image1 = (Image)mediaFactory.make(file1, temporaryResources);
		assertEquals(Image.Format.JPEG, image1.getFormat());
		assertEquals(480, image1.getWidth());
		assertEquals(360, image1.getHeight());

		File file2 = getFileByName("test.bmp");
		Image image2 = (Image)mediaFactory.make(file2, temporaryResources);
		assertEquals(Image.Format.BMP, image2.getFormat());
		assertEquals(48, image2.getWidth());
		assertEquals(48, image2.getHeight());

		File file3 = getFileByName("test.gif");
		Image image3 = (Image)mediaFactory.make(file3, temporaryResources);
		assertEquals(Image.Format.GIF, image3.getFormat());
		assertEquals(16, image3.getWidth());
		assertEquals(16, image3.getHeight());

		File file4 = getFileByName("test.png");
		Image image4 = (Image)mediaFactory.make(file4, temporaryResources);
		assertEquals(Image.Format.PNG, image4.getFormat());
		assertEquals(64, image4.getWidth());
		assertEquals(64, image4.getHeight());

		try{
			File file5 = new File("non-exists");
			mediaFactory.make(file5, temporaryResources);
			fail();
		}catch(IllegalArgumentException e) {
			// pass
		}
		
		// test image resource info
		ResourceInfo info1 = image1.getResourceInfo();
		assertEquals(file1.length(), info1.getContentLength());
		assertEquals("image/jpeg", info1.getMimeType());
		assertEquals(file1.lastModified(), info1.getLastModified().getTime());

		//ResourceContent content1 = info1.getContentSnapshot();
		InputStream in = info1.getContent();
		byte[] contentData1 = IOUtils.toByteArray(in);
		in.close();

		InputStream in1 = new FileInputStream(file1);
		byte[] data1 = IOUtils.toByteArray(in1);
		in1.close();
		
		assertArrayEquals(data1, contentData1);
	}
	
	@Test
	public void testMakeImageFromByteArray() throws IOException {
		byte[] data = getFileContent("test.png");
		Image image1 = (Image)mediaFactory.make(data, ImageLoader.MIME_TYPE_IMAGE_PNG, null, temporaryResources);
		assertEquals(Image.Format.PNG, image1.getFormat());
		assertEquals(64, image1.getWidth());
		assertEquals(64, image1.getHeight());

		// test get image data from Image
		//byte[] imageData = image1.getImageData();

		Image image2 = (Image)mediaFactory.make(image1.getResourceInfo(), temporaryResources);
		assertEquals(Image.Format.PNG, image2.getFormat());
		assertEquals(64, image2.getWidth());
		assertEquals(64, image2.getHeight());
	}

	@Test
	public void testMakeImageFromStream() throws IOException {
		InputStream in = getFileInputStream("test.png");
		Image image1 = (Image)mediaFactory.make(in, ImageLoader.MIME_TYPE_IMAGE_PNG,null, temporaryResources);
		assertEquals(Image.Format.PNG, image1.getFormat());
		assertEquals(64, image1.getWidth());
		assertEquals(64, image1.getHeight());
	}

//	@Test
//	public void testMakeImageFromURL() throws MalformedURLException, IOException {
//		// can be safetly replace this image url.
//		URL url = new URL("https://img-prod-cms-rt-microsoft-com.akamaized.net/cms/api/am/imageFileData/RE1Mu3b");
//		Image image1 = (Image)mediaFactory.make(url, temporaryResources);
//		assertEquals(Image.Format.PNG, image1.getFormat());
//		assertTrue(image1.getWidth() > 1);
//		assertTrue(image1.getHeight() > 1);
//
//		// manual check
//		Utils.saveImage(image1, "mediaFactory-from-url");
//	}

	@Test
	public void testMakeImageFromResourceInfo() throws IOException{
		File file = getFileByName("test.jpg");
		ResourceInfo resourceInfo = fileBaseResourceInfoFactory.make(file);
		Image image1 = (Image)mediaFactory.make(resourceInfo, temporaryResources);
		assertEquals(Image.Format.JPEG, image1.getFormat());
		assertEquals(480, image1.getWidth());
		assertEquals(360, image1.getHeight());
	}

	@Test
	public void testGetMetaData() throws IOException {
		Image image1 = (Image)mediaFactory.make(getFileByName("meta1.jpg"), temporaryResources);
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

		Image image2 = (Image)mediaFactory.make(getFileByName("meta2-vertical.jpg"), temporaryResources);
		MetaData metadata2 = image2.getMetaData();
		assertNotNull(metadata2);
		assertEquals(Image.Orientation.Rotate90CW, metadata2.get(Image.MetaName.Orientation));

		Image image3 = (Image)mediaFactory.make(getFileByName("meta3-no-meta.png"), temporaryResources);
		MetaData metadata3 = image3.getMetaData();
		assertNull(metadata3);
	}
	
	private File getFileByName(String fileName) throws IOException{
		Resource resource = resourceLoader.getResource(sampleImageFolder + fileName);
		return resource.getFile();
	}

	private InputStream getFileInputStream(String fileName) throws IOException{
		File file = getFileByName(fileName);
		return new FileInputStream(file);
	}

	private byte[] getFileContent(String fileName) throws IOException{
		InputStream in = getFileInputStream(fileName);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[16 * 1024];
		while(true){
			int readBytes = in.read(buffer);
			if (readBytes < 0 ){
				break;
			}
			out.write(buffer, 0, readBytes);
		}
		in.close();
		byte[] data = out.toByteArray();
		out.close();
		return data;
	}
}
