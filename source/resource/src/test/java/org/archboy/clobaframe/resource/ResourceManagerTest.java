package org.archboy.clobaframe.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.NamedResourceInfo;
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
public class ResourceManagerTest {

	@Inject
	private ResourceProviderSet resourceProviderSet;
	
	@Inject
	private ResourceManager resourceManager;
	
	@Inject
	private ResourceLoader resourceLoader;

	private final Logger logger = LoggerFactory.getLogger(ResourceManagerTest.class);

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testGetAllBaseResources() throws FileNotFoundException {
		// test get all base resources

		String[] names = new String[]{
			"test.css", "test.js", "test.png",
			"css/test2.css", "css/test3.css", "css/test4.css", "css/test5.css",
			"fonts/fontawesome-webfont.eot","fonts/fontawesome-webfont.svg","fonts/fontawesome-webfont.ttf","fonts/fontawesome-webfont.woff",
			"image/info-32.png", "image/success-16.png", "image/warn-16.png"
		};
		
		for (String name : names) {
			NamedResourceInfo webResourceInfo = resourceManager.getServedResource(name);
			assertTrue(webResourceInfo.getContentLength() > 0);
		}
		
		List<String> nameList1 = new ArrayList<String>();
		Collection<NamedResourceInfo> resourcesByManager1 = resourceManager.list();
		for(NamedResourceInfo resourceInfo : resourcesByManager1){
			nameList1.add(resourceInfo.getName());
		}
		
		for(String name : names) {
			assertTrue(nameList1.contains(name));
		}
		
		assertNull(resourceManager.get("test-none-exists.css"));
		assertNull(resourceManager.get("css/test-none-exists.css"));
		
		assertNull(resourceManager.getServedResource("test-none-exists.css"));
		assertNull(resourceManager.getServedResource("css/test-none-exists.css"));
	}

	@Test
	public void testGetAllOtherResources() throws FileNotFoundException {
		// test get other resources

		String[] names = new String[]{
			"root/apple-touch-icon-120x120.png", 
			"root/favicon-16x16.ico", 
			"root/favicon-16x16.png",
			"root/launcher-icon-192x192.png", 
			"root/robots.txt",
			"other/dark.css"
		};
		
		for (String name : names) {
			NamedResourceInfo webResourceInfo = resourceManager.getServedResource(name);
			assertTrue(webResourceInfo.getContentLength() > 0);
		}
		
		List<String> nameList1 = new ArrayList<String>();
		Collection<NamedResourceInfo> resourcesByManager1 = resourceManager.list();
		for(NamedResourceInfo resourceInfo : resourcesByManager1){
			nameList1.add(resourceInfo.getName());
		}
		
		for(String name : names) {
			assertTrue(nameList1.contains(name));
		}
		
		assertNull(resourceManager.get("root/root-none-exists.css"));
		assertNull(resourceManager.get("other/other-none-exists.css"));
		
		assertNull(resourceManager.getServedResource("root/test-none-exists.css"));
		assertNull(resourceManager.getServedResource("other/other-none-exists.css"));
	}
	
	@Test
	public void testGetServerResource() throws IOException {
		// test get a resource
		NamedResourceInfo webResource1 = resourceManager.getServedResource("test.css");
		NamedResourceInfo webResource2 = resourceManager.getServedResource("test.png");

		assertNotNull(((ContentHashResourceInfo)webResource1).getContentHash());
		assertTrue(webResource1.getContentLength() > 0);
		assertNotNull(webResource1.getLastModified());
		assertEquals("text/css", webResource1.getMimeType());
		assertEquals("test.css", webResource1.getName());
		
		// test get location
		String location1 = resourceManager.getLocation(webResource1);
		assertEquals("/resource/test.css", location1.substring(0, location1.indexOf('?')));
		assertEquals(location1, resourceManager.getLocation("test.css"));
	
		// test get by version name
		String versionName1 = location1.substring(location1.lastIndexOf('/') + 1);
		NamedResourceInfo webResourceByVersionName1 = resourceManager.getServedResourceByVersionName(versionName1);
		assertEquals(webResource1, webResourceByVersionName1);
		
		// test the content
		assertResourceContentEquals(webResource2, "webapp/resources/default/test.png");

		// test location transform
		InputStream in1 = webResource1.getContent();
		String text1 = IOUtils.toString(in1);
		in1.close();
		
		String[] linkNames1 = new String[]{"test.png",
		"image/info-32.png", "image/success-16.png","image/warn-16.png", 
		"fonts/fontawesome-webfont.eot", "fonts/fontawesome-webfont.woff", "fonts/fontawesome-webfont.ttf", "fonts/fontawesome-webfont.svg"};

		for(String name : linkNames1){
			assertTrue(text1.indexOf(resourceManager.getLocation(name)) > 0);
		}

		// test location transform, with relative path
		NamedResourceInfo webResource3 = resourceManager.getServedResource("css/test2.css");
		InputStream in2 = webResource3.getContent();
		String text2 = IOUtils.toString(in2);
		in2.close();
		
		String[] linkNames2 = new String[]{"css/test3.css","image/info-32.png"};
		
		for(String name : linkNames2){
			assertTrue(text2.indexOf(resourceManager.getLocation(name)) > 0);
		}

		// test get none-exists resource
		assertNull(resourceManager.getServedResource("none-exists"));
	}
	
	@Test
	public void testGetDynamicResource() throws IOException {
		
		assertNull(resourceManager.get("d3.css"));
		assertNull(resourceManager.get("d2.css"));
		assertNull(resourceManager.get("d1.css"));
		
		// add provider
		ResourceProvider webResourceProvider1 = new TestingDynamicWebResourceProvider();
		resourceProviderSet.addProvider(webResourceProvider1);
		
		NamedResourceInfo webResourceInfo1 = resourceManager.get("d2.css");
		assertTextResourceContentEquals(webResourceInfo1, "div {}");
		
		String[] names1 = new String[]{"d1.css", "d2.css", "d3.css"};

		List<String> nameList1 = new ArrayList<String>();
		Collection<NamedResourceInfo> resourcesByManager1 = resourceManager.list();
		for(NamedResourceInfo resourceInfo : resourcesByManager1){
			nameList1.add(resourceInfo.getName());
		}
		
		for(String name : names1) {
			assertTrue(nameList1.contains(name));
		}
		
		// remove provider
		resourceProviderSet.removeProvider(webResourceProvider1.getName());
		
		assertNull(resourceManager.get("d3.css"));
		assertNull(resourceManager.get("d2.css"));
		assertNull(resourceManager.get("d1.css"));
	}
	
	@Test
	public void testChainUpdate() throws IOException {
		
		String location1 = resourceManager.getLocation("l3.css");
		String location2 = resourceManager.getLocation("l2a.css");
		String location3 = resourceManager.getLocation("l2b.css");
		String location4 = resourceManager.getLocation("l1.css");
		
		// update l2a.css
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		Date date1 = calendar.getTime();
		
		TestingChainUpdateWebResourceProvider.info2a.updateContent("@import url('l1.css') \n h1,h3 {}", date1);
		
		assertEquals(location1, resourceManager.getLocation("l3.css"));
		assertEquals(location2, resourceManager.getLocation("l2a.css"));
		
		resourceManager.refresh("l2a.css");
		
		String location5 = resourceManager.getLocation("l3.css");
		String location6 = resourceManager.getLocation("l2a.css");
		
		assertFalse(location5.equals(location1));
		assertFalse(location6.equals(location2));
		assertEquals(location3, resourceManager.getLocation("l2b.css"));
		assertEquals(location4, resourceManager.getLocation("l1.css"));
		
		// update l1.css
		calendar.add(Calendar.HOUR_OF_DAY, 2);
		Date date2 = calendar.getTime();
		TestingChainUpdateWebResourceProvider.info1.updateContent("div {}", date2);
		
		resourceManager.refresh("l1.css");
		
		String location7 = resourceManager.getLocation("l3.css");
		String location8 = resourceManager.getLocation("l2a.css");
		String location9 = resourceManager.getLocation("l1.css");
		
		assertFalse(location7.equals(location5));
		assertFalse(location8.equals(location6));
		assertEquals(location3, resourceManager.getLocation("l2b.css"));
		assertFalse(location9.equals(location4));
		
	}
	
	@Test
	public void testGetConcatenateResource() throws IOException {
		NamedResourceInfo webResource1 = resourceManager.getServedResource("css/concat-34.css");
		NamedResourceInfo webResource2 = resourceManager.getServedResource("css/concat-345.css");

		String text1 = IOUtils.toString( resourceManager.getServedResource("css/test3.css").getContent());
		String text2 = IOUtils.toString( resourceManager.getServedResource("css/test4.css").getContent());
		String text3 = IOUtils.toString( resourceManager.getServedResource("css/test5.css").getContent());
		
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

	private void assertResourceContentEquals(NamedResourceInfo resourceInfo, String resourceName) throws IOException {
		byte[] data = getFileContent(resourceName);
		assertResourceContentEquals(resourceInfo, data);
	}

	private void assertTextResourceContentEquals(NamedResourceInfo resourceInfo, String text) throws IOException {
		byte[] data = text.getBytes(Charset.defaultCharset());
		assertResourceContentEquals(resourceInfo, data);
	}

	private void assertResourceContentEquals(NamedResourceInfo resourceInfo, byte[] data) throws IOException {
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

	public static class TestingDynamicWebResourceProvider implements ResourceProvider {

		private NamedResourceInfo d3 = new NamedTextResourceInfo("d3.css", "text/css", "p {}");
		private NamedResourceInfo d2 = new NamedTextResourceInfo("d2.css", "text/css", "div {}");
		private NamedResourceInfo d1 = new NamedTextResourceInfo("d1.css", "text/css", "body {}");
		
		@Override
		public String getName() {
			return "dynamic-test";
		}

		@Override
		public NamedResourceInfo getByName(String name) {
			if (name.equals("d3.css")){
				return d3;
			}else if (name.equals("d2.css")){
				return d2;
			}else if (name.equals("d1.css")){
				return d1;
			}else{
				return null;
			}
		}

		@Override
		public Collection<NamedResourceInfo> list() {
			return Arrays.asList(
					getByName("d3.css"),
					getByName("d2.css"),
					getByName("d1.css"));
		}

		@Override
		public int getOrder() {
			return PRIORITY_NORMAL;
		}

	};
	
	@Named
	public static class TestingChainUpdateWebResourceProvider implements ResourceProvider {

		// for chain update test.
		public static final NamedTextResourceInfo info1 = new NamedTextResourceInfo("l1.css", "text/css", "p {}");
		public static final NamedTextResourceInfo info2a = new NamedTextResourceInfo("l2a.css", "text/css", "@import url('l1.css') \n h1 {}");
		public static final NamedTextResourceInfo info2b = new NamedTextResourceInfo("l2b.css", "text/css", "h2 {}");
		public static final NamedTextResourceInfo info3 = new NamedTextResourceInfo("l3.css", "text/css", "@import url('l2a.css') \n body {}");

		@Override
		public String getName() {
			return "chain-update-test";
		}

		@Override
		public NamedResourceInfo getByName(String name) {
			if (name.equals("l3.css")){
				return info3;
			}else if (name.equals("l2a.css")){
				return info2a;
			}else if (name.equals("l2b.css")){
				return info2b;
			}else if (name.equals("l1.css")){
				return info1;
			}else{
				return null;
			}
		}

		@Override
		public Collection<NamedResourceInfo> list() {
			return Arrays.asList(
					getByName("l1.css"),
					getByName("l2a.css"),
					getByName("l2b.css"),
					getByName("l3.css"));
		}

		@Override
		public int getOrder() {
			return PRIORITY_NORMAL;
		}

	};
	
}
