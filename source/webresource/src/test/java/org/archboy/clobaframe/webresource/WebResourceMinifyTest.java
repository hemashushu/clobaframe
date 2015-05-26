package org.archboy.clobaframe.webresource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author yang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-with-css-js-minify.xml"})
public class WebResourceMinifyTest {

	@Inject
	private WebResourceManager webResourceManager;
	
	@Inject
	private ResourceLoader resourceLoader;

	private final Logger logger = LoggerFactory.getLogger(WebResourceMinifyTest.class);

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testGetMinifyResources() throws IOException {
		
		// the minified css file size will be less than the original file.
		String[] names1 = new String[]{
			"test.css",
			"css/test2.css"
		};
		
		String[] files1 = new String[]{
			"sample/web/test.css",
			"sample/web/css/test2.css"
		};
		
		// check original resource
		for (int idx=0; idx<names1.length; idx++) {
			WebResourceInfo webResourceInfo = webResourceManager.getOriginalResource(names1[idx]);
			assertResourceContentEquals(webResourceInfo, files1[idx]);
		}
		
		// check minified content length
		for (int idx=0; idx<names1.length; idx++) {
			WebResourceInfo webResourceInfo = webResourceManager.getResource(names1[idx]);
			assertTrue(webResourceInfo.getContentLength() < getFileContent(files1[idx]).length);
		}
		
		// the files that only have comments and new-line symbols will minified to empty.
		String[] emptyResourceNames1 = new String[]{
			"test.js", 
			"css/test3.css", "css/test4.css", "css/test5.css"
		};
		
		for (String name : emptyResourceNames1) {
			WebResourceInfo webResourceInfo = webResourceManager.getResource(name);
			assertTrue(webResourceInfo.getContentLength() == 0);
		}
		
	}

	private void assertResourceContentEquals(WebResourceInfo resourceInfo, String resourceName) throws IOException {
		byte[] data = getFileContent(resourceName);
		assertResourceContentEquals(resourceInfo, data);
	}
	
	private void assertResourceContentEquals(WebResourceInfo resourceInfo, byte[] data) throws IOException {
		InputStream in = resourceInfo.getContent();
		byte[] content = IOUtils.toByteArray(in);
		in.close();

		assertArrayEquals(data, content);
	}
	
	/**
	 *
	 * @param name Relate to the 'src/test/resources' folder.
	 * @return
	 * @throws IOException
	 */
	private byte[] getFileContent(String name) throws IOException {
		File file = getFileByName(name);
		InputStream in = new FileInputStream(file);
		byte[] data = IOUtils.toByteArray(in);
		in.close();
		return data;
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
}
