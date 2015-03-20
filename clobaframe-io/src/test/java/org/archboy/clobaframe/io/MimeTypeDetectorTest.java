package org.archboy.clobaframe.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

/**
 *
 * @author yang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class MimeTypeDetectorTest {

	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private MimeTypeDetector mimeTypeDetector;

	private String[] sampleFiles = new String[]{
		"sample/data/test.jpg", 
		"sample/data/test.png", 
		"sample/data/test.txt", 
		"sample/data/test.xml", 
		"sample/data/test.data", // it's a png image file.
		"sample/data/test.css", 
		"sample/data/test.js", 
		"sample/data/test.m4a", // some mime-types list does not identify this type.
		"sample/data/test.mov",
		"sample/data/test.mp3",
		"sample/data/test.mp4",
		"sample/data/test-no-extension", // it's a xml file
		"sample/data/FontAwesome.otf",
		"sample/data/fontawesome-webfont.eot",
		"sample/data/fontawesome-webfont.svg",
		"sample/data/fontawesome-webfont.ttf",
		"sample/data/fontawesome-webfont.woff"};
	
	@Before
	public void setUp() throws Exception {
		//
	}

	@After
	public void tearDown() throws Exception {
		//
	}

	@Test
	public void testGetByExtensionName(){
		String[] types = new String[]{
			"image/jpeg",
			"image/png",
			"text/plain",
			"application/xml",
			"application/octet-stream", // data
			"text/css",
			"application/javascript",
			"audio/mpeg", // m4a
			"video/quicktime",
			"audio/mpeg",
			"video/mp4",
			"application/octet-stream", // no extension
			"application/x-font-otf",
			"application/vnd.ms-fontobject", // eot
			"image/svg+xml",
			"application/x-font-ttf",
			"application/x-font-woff"};

		for(int idx=0; idx<sampleFiles.length; idx++){
			String name = sampleFiles[idx];
			String exceptedType = types[idx];
			if (StringUtils.isNotEmpty(exceptedType)){
				String type = mimeTypeDetector.getByExtensionName(name);
				assertEquals(exceptedType, type);
			}
		}
	}

	/**
	 * Get the test resources by file name.
	 *
	 * @param name Relate to the 'src/test/resources' folder.
	 * @return
	 * @throws IOException
	 */
	private File getFileByName(String name) throws IOException{
		Resource resource = resourceLoader.getResource(name); //"file:target/test-classes/" +
		return resource.getFile();
	}

	/**
	 * Remember to close the input stream.
	 *
	 * @param name
	 * @return
	 * @throws IOException
	 */
	private InputStream getFileInputStream(String name) throws IOException {
		File file = getFileByName(name);
		InputStream in = new FileInputStream(file);
		return in;
	}
}
