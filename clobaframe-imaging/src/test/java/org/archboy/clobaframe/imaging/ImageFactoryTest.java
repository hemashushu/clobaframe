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
package org.archboy.clobaframe.imaging;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.archboy.clobaframe.imaging.impl.AbstractImage;
import org.archboy.clobaframe.webio.ContentTypeAnalyzer;
import org.archboy.clobaframe.webio.ResourceInfo;
import org.archboy.clobaframe.webio.impl.FileResourceInfo;
import static org.junit.Assert.*;
import org.archboy.clobaframe.webio.ResourceInfoFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class ImageFactoryTest {

	private static final String DEFAULT_SAMPLE_IMAGE_FOLDER = "sample/image/";
	private String sampleImageFolder = DEFAULT_SAMPLE_IMAGE_FOLDER;

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private ImageFactory imageFactory;

	@Autowired
	private ResourceInfoFactory resourceInfoFactory;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMakeImageFromByteArray() throws IOException {
		byte[] data = getFileContent("test.png");
		Image image1 = imageFactory.makeImage(data);
		assertEquals(Image.Format.PNG, image1.getFormat());
		assertEquals(64, image1.getWidth());
		assertEquals(64, image1.getHeight());

		// test get image data from Image
		byte[] imageData = image1.getImageData();

		Image image2 = imageFactory.makeImage(imageData);
		assertEquals(Image.Format.PNG, image2.getFormat());
		assertEquals(64, image2.getWidth());
		assertEquals(64, image2.getHeight());
	}

	@Test
	public void testMakeImageFromFile() throws IOException {
		File file1 = getFileByName("test.jpg");
		Image image1 = imageFactory.makeImage(file1);
		assertEquals(Image.Format.JPEG, image1.getFormat());
		assertEquals(480, image1.getWidth());
		assertEquals(360, image1.getHeight());

		File file2 = getFileByName("test.bmp");
		Image image2 = imageFactory.makeImage(file2);
		assertEquals(Image.Format.BMP, image2.getFormat());
		assertEquals(48, image2.getWidth());
		assertEquals(48, image2.getHeight());

		File file3 = getFileByName("test.gif");
		Image image3 = imageFactory.makeImage(file3);
		assertEquals(Image.Format.GIF, image3.getFormat());
		assertEquals(16, image3.getWidth());
		assertEquals(16, image3.getHeight());

		File file4 = getFileByName("test.png");
		Image image4 = imageFactory.makeImage(file4);
		assertEquals(Image.Format.PNG, image4.getFormat());
		assertEquals(64, image4.getWidth());
		assertEquals(64, image4.getHeight());

		try{
			File file5 = new File("non-exists");
			imageFactory.makeImage(file5);
			fail();
		}catch(IOException e) {
			// pass
		}

	}

	@Test
	public void testMakeImageFromStream() throws IOException {
		InputStream in = getFileInputStream("test.png");
		Image image1 = imageFactory.makeImage(in);
		assertEquals(Image.Format.PNG, image1.getFormat());
		assertEquals(64, image1.getWidth());
		assertEquals(64, image1.getHeight());
	}

	@Test
	public void testMakeImageFromURL() throws MalformedURLException, IOException {
		String url = "http://upload.wikimedia.org/wikipedia/en/b/bc/Wiki.png";
		Image image1 = imageFactory.makeImage(url);
		assertEquals(Image.Format.PNG, image1.getFormat());
		assertTrue(image1.getWidth() > 1);
		assertTrue(image1.getHeight() > 1);

		// manual check
		saveImage(image1, "imagefactory-wiki");
	}

	@Test
	public void testMakeImageFromCanvas() throws IOException {
		Image image1 = imageFactory.makeImage(200, 300, Color.darkGray);
		assertEquals(200, image1.getWidth());
		assertEquals(300, image1.getHeight());
		assertEquals(Color.darkGray.getRGB(), getImageColor(image1, 0, 0));

		// manual check
		saveImage(image1, "imagefactory-darkGray-200x300");

		Color transparent = new Color(0, 0, 0, 0);
		Image image2 = imageFactory.makeImage(300, 100, transparent);
		assertEquals(300, image2.getWidth());
		assertEquals(100, image2.getHeight());
		assertEquals(transparent.getRGB(), getImageColor(image2, 0, 0));

		// manual check
		saveImage(image2, "imagefactory-transparent-300x100");
	}

	@Test
	public void testMakeImageFromResourceInfo() throws IOException{
		File file = getFileByName("test.jpg");
		ResourceInfo resourceInfo = resourceInfoFactory.make(file);
		Image image1 = imageFactory.makeImage(resourceInfo);
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

	private int getImageColor(Image image, int x, int y){
		BufferedImage bufferedImage = ((AbstractImage)image).getBufferedImage();
		return bufferedImage.getRGB(x, y);
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
		FileOutputStream out = new FileOutputStream(file);
		out.write(image.getImageData());
		out.close();
	}
}
