package org.archboy.clobaframe.webresource.blobstore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceManager;
import static org.junit.Assert.*;


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

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testGetAllResources() {
		// test get all resources

		Collection<WebResourceInfo> webResources = resourceManager.getAllResources();
		//assertEquals(5, webResources.size()); // should assume the items.

		List<String> names = new ArrayList<String>();
		for(WebResourceInfo webResource : webResources){
			names.add(webResource.getName());
		}

		assertTrue(names.contains("test.css"));
		assertTrue(names.contains("test.js"));
		assertTrue(names.contains("test.png"));
		assertTrue(names.contains("test.txt"));
		assertTrue(names.contains("folder/info-32.png"));
	}

	@Test
	public void testGetResource() throws IOException {
		// test get a resource

		WebResourceInfo webResource1 = resourceManager.getResource("test.png");
		WebResourceInfo webResource2 = resourceManager.getResource("test.css");
		WebResourceInfo webResource3 = resourceManager.getResource("folder/info-32.png");

//		assertNotNull(webResource1);
//		assertNotNull(webResource2);
//		assertNotNull(webResource3);

		assertNotNull(webResource1.getUniqueName());
		assertNotNull(resourceManager.getLocation(webResource1));
		assertEquals("image/png", webResource1.getContentType());

		// test the content
//		ResourceContent content1 = webResource1.getContentSnapshot();
//		assertTrue(content1.getLength() > 0);
//		IOUtils.closeQuietly(content1);

		assertRemoteContentEquals(webResource1, "sample/web/test.png");
		//assertContentEquals(webResource2, "sample/web/test.css");
		assertRemoteContentEquals(webResource3, "sample/web/folder/info-32.png");

//		// test get a content-replacing resource, this is optional
//		//ResourceContent content2 = webResource2.getContentSnapshot();
//		InputStream in2 = webResource2.getInputStream();
//		String text2 = IOUtils.toString(in2);
//		assertTrue(text2.indexOf(resourceService.getLocation(webResource1)) > 0);
//		assertTrue(text2.indexOf(resourceService.getLocation(webResource3)) > 0);
//		in2.close();
//
//		// test get none exists
//		try{
//			resourceService.getResource("noneExists.png");
//			fail();
//		}catch(FileNotFoundException e){
//			// pass
//		}
	}

	@Test
	public void testGetResourceByUniqueName() throws FileNotFoundException {
//		// test get by unique name
//		WebResourceInfo webResource1 = resourceService.getResource("test.png");
//		String uniqueName1 = webResource1.getUniqueName();
//
//		WebResourceInfo webResource2 = resourceService.getResourceByUniqueName(uniqueName1);
//
//		assertEquals(webResource1.getName(), webResource2.getName());
//		assertEquals(uniqueName1, webResource2.getUniqueName());
//
//		// test get none exists
//		try{
//			resourceService.getResourceByUniqueName("noneExists");
//			fail();
//		}catch(FileNotFoundException e){
//			// pass
//		}
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

	private void checkRemoteResourceContent(WebResourceInfo resourceInfo, byte[] data) throws IOException {
		//ResourceContent resourceContent = resourceInfo.getContentSnapshot();
//		InputStream in = resourceInfo.getInputStream();// resourceContent.getInputStream();
//		byte[] content = IOUtils.toByteArray(in);
//		in.close();

		String location = resourceManager.getLocation(resourceInfo);
		
		File file = new File(location);
		InputStream in = new FileInputStream(file);
		byte[] content = IOUtils.toByteArray(in);
		in.close();
		
		assertArrayEquals(data, content);
	}

	private void assertRemoteContentEquals(WebResourceInfo resourceInfo, String resourceName) throws IOException {
		byte[] data = getFileContent(resourceName);
		checkRemoteResourceContent(resourceInfo, data);
	}
}
