package org.archboy.clobaframe.imaging;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.apache.commons.io.IOUtils;
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
import org.archboy.clobaframe.webio.ResourceContent;

/**
 *
 * @author arch
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class ImageResourceInfoTest {

	private static final String DEFAULT_SAMPLE_IMAGE_FOLDER = "sample/image/";
	private String sampleImageFolder = DEFAULT_SAMPLE_IMAGE_FOLDER;

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private ImageFactory imageFactory;


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testImageResourceInfo() throws IOException {

		File file = getFileByName("test.jpg");
		byte[] data = getFileContent("test.jpg");

		Image image1 = imageFactory.makeImage(file);

		ImageResourceInfo info1 = new ImageResourceInfo(image1, new Date(file.lastModified()));
		assertEquals(file.length(), info1.getContentLength());
		assertEquals("image/jpeg", info1.getContentType());
		assertEquals(file.lastModified(), info1.getLastModified().getTime());

		ResourceContent content1 = info1.getContentSnapshot();
		byte[] contentData1 = IOUtils.toByteArray(content1.getInputStream());
		content1.close();

		assertArrayEquals(data, contentData1);
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
		return IOUtils.toByteArray(in);
	}
}
