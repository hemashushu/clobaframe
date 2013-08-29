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
package org.archboy.clobaframe.media.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.media.MediaFactory;
import org.archboy.clobaframe.media.image.impl.ImageLoaderImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.archboy.clobaframe.io.ContentTypeDetector;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.io.file.impl.FileResourceInfo;
import static org.junit.Assert.*;
import org.archboy.clobaframe.io.ResourceInfoFactory;
import org.archboy.clobaframe.io.TemporaryResources;
import org.archboy.clobaframe.io.file.FileBaseResourceInfoFactory;
import org.archboy.clobaframe.io.impl.DefaultTemporaryResources;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class ImageFactoryTest {

	private static final String DEFAULT_SAMPLE_IMAGE_FOLDER = "sample/image/";
	private String sampleImageFolder = DEFAULT_SAMPLE_IMAGE_FOLDER;

	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private MediaFactory mediaFactory;
	//private ImageGenerator mediaFactory;

	@Inject
	private ResourceInfoFactory resourceInfoFactory;

	@Inject
	private FileBaseResourceInfoFactory fileBaseResourceInfoFactory;
	
	private TemporaryResources temporaryResources = new DefaultTemporaryResources();
	
	@Before
	public void setUp() throws Exception {
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
		assertEquals("image/jpeg", info1.getContentType());
		assertEquals(file1.lastModified(), info1.getLastModified().getTime());

		//ResourceContent content1 = info1.getContentSnapshot();
		InputStream in = info1.getInputStream();
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
		Image image1 = (Image)mediaFactory.make(data, ImageLoaderImpl.CONTENT_TYPE_PNG, null, temporaryResources);
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
		Image image1 = (Image)mediaFactory.make(in, ImageLoaderImpl.CONTENT_TYPE_PNG,null, temporaryResources);
		assertEquals(Image.Format.PNG, image1.getFormat());
		assertEquals(64, image1.getWidth());
		assertEquals(64, image1.getHeight());
	}

	@Test
	public void testMakeImageFromURL() throws MalformedURLException, IOException {
		URL url = new URL("http://upload.wikimedia.org/wikipedia/en/b/bc/Wiki.png");
		Image image1 = (Image)mediaFactory.make(url, temporaryResources);
		assertEquals(Image.Format.PNG, image1.getFormat());
		assertTrue(image1.getWidth() > 1);
		assertTrue(image1.getHeight() > 1);

		// manual check
		saveImage(image1, "mediaFactory-from-url");
	}

	@Test
	public void testMakeImageFromResourceInfo() throws IOException{
		File file = getFileByName("test.jpg");
		ResourceInfo resourceInfo = fileBaseResourceInfoFactory.make(file);
		Image image1 = (Image)mediaFactory.make(resourceInfo, temporaryResources);
		assertEquals(Image.Format.JPEG, image1.getFormat());
		assertEquals(480, image1.getWidth());
		assertEquals(360, image1.getHeight());
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

	/**
	 * Save image to file, for manual checking.
	 *
	 * @param image
	 * @param filename
	 * @throws IOException
	 */
	private void saveImage(Image image, String filename) throws IOException{
		String formatName = image.getFormat().toString().toLowerCase();
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		File file = new File(tempDir, filename + "." + formatName);
		
		
		ResourceInfo resourceInfo = image.getResourceInfo();
		//ResourceContent resourceContent = resourceInfo.getContentSnapshot();
		InputStream in = resourceInfo.getInputStream();
		
		FileOutputStream out = new FileOutputStream(file);
		IOUtils.copy(in, out);
		
		IOUtils.closeQuietly(out);
		IOUtils.closeQuietly(in);
	}
}
