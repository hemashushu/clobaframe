package org.archboy.clobaframe.webresource;

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
	private WebResourceProviderSet webResourceProviderSet;
	
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
	public void testGetAllBaseResources() throws FileNotFoundException {
		// test get all base resources

		String[] names = new String[]{
			"test.css", "test.js", "test.png",
			"css/test2.css", "css/test3.css", "css/test4.css", "css/test5.css",
			"fonts/fontawesome-webfont.eot","fonts/fontawesome-webfont.svg","fonts/fontawesome-webfont.ttf","fonts/fontawesome-webfont.woff",
			"image/info-32.png", "image/success-16.png", "image/warn-16.png"
		};
		
		for (String name : names) {
			WebResourceInfo webResourceInfo = webResourceManager.getServerResource(name);
			assertTrue(webResourceInfo.getContentLength() > 0);
		}
		
		List<String> nameList1 = new ArrayList<String>();
		Collection<WebResourceInfo> resourcesByManager1 = webResourceManager.list();
		for(WebResourceInfo resourceInfo : resourcesByManager1){
			nameList1.add(resourceInfo.getName());
		}
		
		for(String name : names) {
			assertTrue(nameList1.contains(name));
		}
		
		assertNull(webResourceManager.getResource("test-none-exists.css"));
		assertNull(webResourceManager.getResource("css/test-none-exists.css"));
		
		assertNull(webResourceManager.getServerResource("test-none-exists.css"));
		assertNull(webResourceManager.getServerResource("css/test-none-exists.css"));
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
			WebResourceInfo webResourceInfo = webResourceManager.getServerResource(name);
			assertTrue(webResourceInfo.getContentLength() > 0);
		}
		
		List<String> nameList1 = new ArrayList<String>();
		Collection<WebResourceInfo> resourcesByManager1 = webResourceManager.list();
		for(WebResourceInfo resourceInfo : resourcesByManager1){
			nameList1.add(resourceInfo.getName());
		}
		
		for(String name : names) {
			assertTrue(nameList1.contains(name));
		}
		
		assertNull(webResourceManager.getResource("root/root-none-exists.css"));
		assertNull(webResourceManager.getResource("other/other-none-exists.css"));
		
		assertNull(webResourceManager.getServerResource("root/test-none-exists.css"));
		assertNull(webResourceManager.getServerResource("other/other-none-exists.css"));
	}
	
	@Test
	public void testGetServerResource() throws IOException {
		// test get a resource
		WebResourceInfo webResource1 = webResourceManager.getServerResource("test.css");
		WebResourceInfo webResource2 = webResourceManager.getServerResource("test.png");

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
		String versionName1 = location1.substring(location1.lastIndexOf('/') + 1);
		WebResourceInfo webResourceByVersionName1 = webResourceManager.getServerResourceByVersionName(versionName1);
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
			assertTrue(text1.indexOf(webResourceManager.getLocation(name)) > 0);
		}

		// test location transform, with relative path
		WebResourceInfo webResource3 = webResourceManager.getServerResource("css/test2.css");
		InputStream in2 = webResource3.getContent();
		String text2 = IOUtils.toString(in2);
		in2.close();
		
		String[] linkNames2 = new String[]{"css/test3.css","image/info-32.png"};
		
		for(String name : linkNames2){
			assertTrue(text2.indexOf(webResourceManager.getLocation(name)) > 0);
		}

		// test get none-exists resource
		assertNull(webResourceManager.getServerResource("none-exists"));
	}
	
	@Test
	public void testGetDynamicResource() throws IOException {
		
		assertNull(webResourceManager.getResource("d3.css"));
		assertNull(webResourceManager.getResource("d2.css"));
		assertNull(webResourceManager.getResource("d1.css"));
		
		// add provider
		WebResourceProvider webResourceProvider1 = new TestingDynamicWebResourceProvider();
		webResourceProviderSet.addProvider(webResourceProvider1);
		
		WebResourceInfo webResourceInfo1 = webResourceManager.getResource("d2.css");
		assertTextResourceContentEquals(webResourceInfo1, "div {}");
		
		String[] names1 = new String[]{"d1.css", "d2.css", "d3.css"};

		List<String> nameList1 = new ArrayList<String>();
		Collection<WebResourceInfo> resourcesByManager1 = webResourceManager.list();
		for(WebResourceInfo resourceInfo : resourcesByManager1){
			nameList1.add(resourceInfo.getName());
		}
		
		for(String name : names1) {
			assertTrue(nameList1.contains(name));
		}
		
		// remove provider
		webResourceProviderSet.removeProvider(webResourceProvider1.getName());
		
		assertNull(webResourceManager.getResource("d3.css"));
		assertNull(webResourceManager.getResource("d2.css"));
		assertNull(webResourceManager.getResource("d1.css"));
	}
	
	@Test
	public void testChainUpdate() throws IOException {
		
		String location1 = webResourceManager.getLocation("l3.css");
		String location2 = webResourceManager.getLocation("l2a.css");
		String location3 = webResourceManager.getLocation("l2b.css");
		String location4 = webResourceManager.getLocation("l1.css");
		
		// update l2a.css
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		Date date1 = calendar.getTime();
		
		TestingChainUpdateWebResourceProvider.info2a.updateContent("@import url('l1.css') \n h1,h3 {}", date1);
		
		assertEquals(location1, webResourceManager.getLocation("l3.css"));
		assertEquals(location2, webResourceManager.getLocation("l2a.css"));
		
		webResourceManager.refresh("l2a.css");
		
		String location5 = webResourceManager.getLocation("l3.css");
		String location6 = webResourceManager.getLocation("l2a.css");
		
		assertFalse(location5.equals(location1));
		assertFalse(location6.equals(location2));
		assertEquals(location3, webResourceManager.getLocation("l2b.css"));
		assertEquals(location4, webResourceManager.getLocation("l1.css"));
		
		// update l1.css
		calendar.add(Calendar.HOUR_OF_DAY, 2);
		Date date2 = calendar.getTime();
		TestingChainUpdateWebResourceProvider.info1.updateContent("div {}", date2);
		
		webResourceManager.refresh("l1.css");
		
		String location7 = webResourceManager.getLocation("l3.css");
		String location8 = webResourceManager.getLocation("l2a.css");
		String location9 = webResourceManager.getLocation("l1.css");
		
		assertFalse(location7.equals(location5));
		assertFalse(location8.equals(location6));
		assertEquals(location3, webResourceManager.getLocation("l2b.css"));
		assertFalse(location9.equals(location4));
		
	}
	
	@Test
	public void testGetConcatenateResource() throws IOException {
		WebResourceInfo webResource1 = webResourceManager.getServerResource("css/concat-34.css");
		WebResourceInfo webResource2 = webResourceManager.getServerResource("css/concat-345.css");

		String text1 = IOUtils.toString( webResourceManager.getServerResource("css/test3.css").getContent());
		String text2 = IOUtils.toString( webResourceManager.getServerResource("css/test4.css").getContent());
		String text3 = IOUtils.toString( webResourceManager.getServerResource("css/test5.css").getContent());
		
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

	public static class TestingDynamicWebResourceProvider implements WebResourceProvider {

		private WebResourceInfo d3 = new TextWebResourceInfo("d3.css", "text/css", "p {}");
		private WebResourceInfo d2 = new TextWebResourceInfo("d2.css", "text/css", "div {}");
		private WebResourceInfo d1 = new TextWebResourceInfo("d1.css", "text/css", "body {}");
		
		@Override
		public String getName() {
			return "dynamic-test";
		}

		@Override
		public WebResourceInfo getByName(String name) {
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
		public Collection<WebResourceInfo> list() {
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
	public static class TestingChainUpdateWebResourceProvider implements WebResourceProvider {

		// for chain update test.
		public static final TextWebResourceInfo info1 = new TextWebResourceInfo("l1.css", "text/css", "p {}");
		public static final TextWebResourceInfo info2a = new TextWebResourceInfo("l2a.css", "text/css", "@import url('l1.css') \n h1 {}");
		public static final TextWebResourceInfo info2b = new TextWebResourceInfo("l2b.css", "text/css", "h2 {}");
		public static final TextWebResourceInfo info3 = new TextWebResourceInfo("l3.css", "text/css", "@import url('l2a.css') \n body {}");

		@Override
		public String getName() {
			return "chain-update-test";
		}

		@Override
		public WebResourceInfo getByName(String name) {
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
		public Collection<WebResourceInfo> list() {
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
