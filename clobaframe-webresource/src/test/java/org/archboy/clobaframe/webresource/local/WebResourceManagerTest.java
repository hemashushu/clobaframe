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
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceManager;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;


/**
 *
 * @author yang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml"})
public class WebResourceManagerTest {

	@Inject
	private WebResourceManager resourceManager;

	@Inject
	private ResourceLoader resourceLoader;

	private final Logger logger = LoggerFactory.getLogger(WebResourceManagerTest.class);

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testGetAllResources() {
		// test get all resources

		// note: can not assume/assert the collection size because
		// it may be exists web resource files before test.
		Collection<WebResourceInfo> webResources = resourceManager.getAllResources();

		List<String> names = new ArrayList<String>();
		for(WebResourceInfo webResource : webResources){
			names.add(webResource.getName());
		}

		assertTrue(names.contains("test.css"));
		assertTrue(names.contains("test.js"));
		assertTrue(names.contains("test.png"));
		assertTrue(names.contains("test.txt"));
		assertTrue(names.contains("image/info-32.png"));

		// check combine web resource
		assertTrue(names.contains("css/cob-t3-t4.css"));
		assertTrue(names.contains("css/cob-t3-t4-t5.css"));
	}

	@Test
	public void testGetResource() throws IOException {
		// test get a resource

		WebResourceInfo webResource1 = resourceManager.getResource("test.css");
		WebResourceInfo webResource2 = resourceManager.getResource("test.png");
		WebResourceInfo webResource3 = resourceManager.getResource("image/info-32.png");
		WebResourceInfo webResource4 = resourceManager.getResource("image/success-16.png");
		WebResourceInfo webResource5 = resourceManager.getResource("fonts/glyphicons-halflings-regular.ttf");
		WebResourceInfo webResource6 = resourceManager.getResource("fonts/glyphicons-halflings-regular.svg");
		WebResourceInfo webResource7 = resourceManager.getResource("fonts/glyphicons-halflings-regular.eot");

		assertNotNull(webResource1.getUniqueName());
		assertNotNull(resourceManager.getLocation(webResource1));
		assertEquals("text/css", webResource1.getContentType());

		// test the content
		assertFileContentEquals(webResource2, "sample/web/test.png");
		assertFileContentEquals(webResource3, "sample/web/image/info-32.png");

		// test get a content-replacing resource, this is optional
		InputStream in1 = webResource1.getInputStream();
		String text1 = IOUtils.toString(in1);

		logger.info(text1);

		assertTrue(text1.indexOf(resourceManager.getLocation(webResource2)) > 0);
		assertTrue(text1.indexOf(resourceManager.getLocation(webResource3)) > 0);
		assertTrue(text1.indexOf(resourceManager.getLocation(webResource4)) > 0);
		assertTrue(text1.indexOf(resourceManager.getLocation(webResource5)) > 0);
		assertTrue(text1.indexOf(resourceManager.getLocation(webResource6)) > 0);
		assertTrue(text1.indexOf(resourceManager.getLocation(webResource7)) > 0);
		in1.close();

		// test get a content-replacing resource with relate folder url.
		WebResourceInfo webResource11 = resourceManager.getResource("css/test2.css");
		WebResourceInfo webResource12 = resourceManager.getResource("css/test3.css");

		// test get a content-replacing resource, this is optional
		InputStream in11 = webResource11.getInputStream();
		String text11 = IOUtils.toString(in11);
		assertTrue(text11.indexOf(resourceManager.getLocation(webResource12)) > 0);
		assertTrue(text11.indexOf(resourceManager.getLocation(webResource3)) > 0);
		in11.close();

		// test get none exists
		try{
			resourceManager.getResource("noneExists.png");
			fail();
		}catch(FileNotFoundException e){
			// pass
		}
	}

	@Test
	public void testGetLocationReplaceResource() throws IOException {
		WebResourceInfo webResource1 = resourceManager.getResource("css/cob-t3-t4.css");
		WebResourceInfo webResource2 = resourceManager.getResource("css/cob-t3-t4-t5.css");

		// test the content
		assertTextContentEquals(webResource1, "/* test3.css */\n/* test4.css */");
		assertTextContentEquals(webResource2, "/* test3.css */\n/* test4.css */\n/* test5.css */");

	}

	@Test
	public void testGetCombineResource() throws IOException {

	}

	@Test
	public void testGetResourceByUniqueName() throws FileNotFoundException {
		// test get by unique name
		WebResourceInfo webResource1 = resourceManager.getResource("test.png");
		String uniqueName1 = webResource1.getUniqueName();

		WebResourceInfo webResource2 = resourceManager.getResourceByUniqueName(uniqueName1);

		assertEquals(webResource1.getName(), webResource2.getName());
		assertEquals(uniqueName1, webResource2.getUniqueName());

		// test get none exists
		try{
			resourceManager.getResourceByUniqueName("noneExists");
			fail();
		}catch(FileNotFoundException e){
			// pass
		}
	}

	public void testGetLocation(){
		//
	}

	public void testGetLocationWithObject(){
		//
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

	private void checkResourceContent(WebResourceInfo resourceInfo, byte[] data) throws IOException {
		//ResourceContent resourceContent = resourceInfo.getContentSnapshot();
		InputStream in = resourceInfo.getInputStream(); // resourceContent.getInputStream();
		byte[] content = IOUtils.toByteArray(in);
		in.close();

		assertArrayEquals(data, content);
	}

	private void assertFileContentEquals(WebResourceInfo resourceInfo, String resourceName) throws IOException {
		byte[] data = getFileContent(resourceName);
		checkResourceContent(resourceInfo, data);
	}

	private void assertTextContentEquals(WebResourceInfo resourceInfo, String text) throws IOException {
		byte[] data = text.getBytes(Charset.defaultCharset());
		checkResourceContent(resourceInfo, data);
	}
}

