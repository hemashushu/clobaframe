package org.archboy.clobaframe.webresource.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceManager;
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
@ContextConfiguration(locations = { "/applicationContext.xml"})
public class WebResourceManagerTest {

	@Inject
	private WebResourceManager webResourceManager;
	
	@Inject
	private ResourceLoader resourceLoader;

	private final Logger logger = LoggerFactory.getLogger(WebResourceManagerTest.class);

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testGetAllResources() throws FileNotFoundException {
		// test get all resources

		String[] names = new String[]{
			"test.css", "test.js", "test.png",
			"css/test2.css", "css/test3.css", "css/test4.css", "css/test5.css",
			"fonts/fontawesome-webfont.eot","fonts/fontawesome-webfont.svg","fonts/fontawesome-webfont.ttf","fonts/fontawesome-webfont.woff",
			"image/info-32.png", "image/success-16.png", "image/warn-16.png",
			"css/concat-34.css", "css/concat-345.css"
		};
		
		List<WebResourceInfo> infos = new ArrayList<WebResourceInfo>();
		for (String name : names) {
			WebResourceInfo webResourceInfo = webResourceManager.getResource(name);
			assertTrue(webResourceInfo.getContentLength() > 0);
		}
		
	}

	@Test
	public void testGetResource() throws IOException {
		// test get a resource
		WebResourceInfo webResource1 = webResourceManager.getResource("test.css");
		WebResourceInfo webResource2 = webResourceManager.getResource("test.png");

		assertNotNull(webResource1.getContentHash());
		assertTrue(webResource1.getContentLength() > 0);
		assertNotNull(webResource1.getLastModified());
		assertEquals("text/css", webResource1.getMimeType());
		assertEquals("test.css", webResource1.getName());
		
		// test get location
		String location1 = webResourceManager.getLocation(webResource1);
		assertEquals("/resource/test.css", location1.substring(0, location1.indexOf('?')));
		assertEquals(location1, webResourceManager.getLocation("test.css"));
	
		// test get by version name
		String versionName1 =location1.substring(location1.lastIndexOf('/') + 1);
		WebResourceInfo webResourceByVersionName1 = webResourceManager.getResourceByVersionName(versionName1);
		assertEquals(webResource1, webResourceByVersionName1);
		
		// test the content
		assertResourceContentEquals(webResource2, "sample/web/test.png");

		// test location transform
		InputStream in1 = webResource1.getContent();
		String text1 = IOUtils.toString(in1);
		in1.close();
		
		String[] linkNames1 = new String[]{"test.png",
		"image/info-32.png", "image/success-16.png","image/warn-16.png", 
		"fonts/fontawesome-webfont.eot", "fonts/fontawesome-webfont.woff", "fonts/fontawesome-webfont.ttf", "fonts/fontawesome-webfont.svg"};

		for(String name : linkNames1){
			assertTrue(text1.indexOf(webResourceManager.getLocation(name)) > 0);
		}

		// test location transform, with relative path
		WebResourceInfo webResource3 = webResourceManager.getResource("css/test2.css");
		InputStream in2 = webResource3.getContent();
		String text2 = IOUtils.toString(in2);
		in2.close();
		
		String[] linkNames2 = new String[]{"css/test3.css","image/info-32.png"};
		
		for(String name : linkNames2){
			assertTrue(text2.indexOf(webResourceManager.getLocation(name)) > 0);
		}

		// test get none-exists resource
		try{
			webResourceManager.getResource("none-exists");
			fail();
		}catch(FileNotFoundException e){
			// pass
		}
	}

	@Test
	public void testGetConcatenateResource() throws IOException {
		WebResourceInfo webResource1 = webResourceManager.getResource("css/concat-34.css");
		WebResourceInfo webResource2 = webResourceManager.getResource("css/concat-345.css");

		String text1 = IOUtils.toString( webResourceManager.getResource("css/test3.css").getContent());
		String text2 = IOUtils.toString( webResourceManager.getResource("css/test4.css").getContent());
		String text3 = IOUtils.toString( webResourceManager.getResource("css/test5.css").getContent());
		
		// test the content
		assertTextResourceContentEquals(webResource1, text1 + "\n" + text2);
		assertTextResourceContentEquals(webResource2, text1 + "\n" + text2 + "\n" + text3);
	}

	public void testGetLocation(){
		//
	}

	public void testGetLocationWithWebResourceInfoObject(){
		//
	}

	private void assertResourceContentEquals(WebResourceInfo resourceInfo, String resourceName) throws IOException {
		byte[] data = getFileContent(resourceName);
		assertResourceContentEquals(resourceInfo, data);
	}

	private void assertTextResourceContentEquals(WebResourceInfo resourceInfo, String text) throws IOException {
		byte[] data = text.getBytes(Charset.defaultCharset());
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
