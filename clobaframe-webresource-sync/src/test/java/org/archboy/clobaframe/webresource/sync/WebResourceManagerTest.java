package org.archboy.clobaframe.webresource.sync;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

//	private Server server;
	
	@Inject
	private WebResourceManager resourceManager;

	@Inject
	private ResourceLoader resourceLoader;
	
//	@Inject
//	private WebResourceSender resourceSender;

	@Before
	public void setUp() {
		// start http server
//		server = new Server(18080);
//		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
//		context.setContextPath("/");
//		server.setHandler(context);
//
//		ServletHolder servletHolder1 = new ServletHolder(
//				new WebResourceSenderServlet(resourceSender));
//		context.addServlet(servletHolder1,"/getByUniqueName");
//
//		server.start();
	}

	@After
	public void tearDown() {
		// stop http server
//		server.stop();
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
		WebResourceInfo webResource2 = resourceManager.getResource("folder/info-32.png");

		assertResourceContentEquals(webResource1, "sample/web/test.png");
		assertResourceContentEquals(webResource2, "sample/web/folder/info-32.png");

//		checkRemoteResource("test.css");
//		checkRemoteResource("test.png");
//		checkRemoteResource("folder/info-32.png");
	}

	@Test
	public void testGetResourceByUniqueName() throws FileNotFoundException {

	}

	public void testGetLocation(){
		//
	}

	public void testGetLocationWithObject(){
		//
	}

	private void assertResourceContentEquals(WebResourceInfo resourceInfo, String resourceName) throws IOException {
		byte[] data = getFileContent(resourceName);
		assertResourceContentEquals(resourceInfo, data);
	}
	
	private void assertResourceContentEquals(WebResourceInfo resourceInfo, byte[] data) throws IOException {
		InputStream in = resourceInfo.getInputStream(); // resourceContent.getInputStream();
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
	
//	private void checkRemoteResource(String resourceName) throws IOException {
//		WebResourceInfo webResource1 = resourceManager.getResource(resourceName);
//		
//		CloseableHttpClient client = HttpClients.createDefault();
//
//		HttpGet method1 = new HttpGet("http://localhost:18080" + resourceManager.getLocation(webResource1));
//
//		try {
//			assertResponseContentEquals(client, method1, webResource1);
//		} catch (IOException e) {
//			fail(e.getMessage());
//		}
//		
//		IOUtils.closeQuietly(client);
//	}
//	
//	private void assertResponseContentEquals(CloseableHttpClient client, HttpGet method, WebResourceInfo resourceInfo) throws IOException {
//		//ResourceContent resourceContent = resourceInfo.getContentSnapshot();
//		InputStream in = resourceInfo.getInputStream(); // resourceContent.getInputStream();
//		byte[] content = IOUtils.toByteArray(in);
//		in.close();
//
//		assertResponseContentEquals(client, method, content);
//	}
//	
//	
//	private void assertResponseContentEquals(CloseableHttpClient client, HttpGet method, byte[] data)
//			throws IllegalStateException, IOException {
//		CloseableHttpResponse response = client.execute(method);
//		//int statusCode = response.getStatusLine().getStatusCode();
//		//assertEquals(HttpStatus.SC_PARTIAL_CONTENT, statusCode);
//		//assertEquals(2, response.getEntity().getContentLength());
//		assertArrayEquals(data, EntityUtils.toByteArray(response.getEntity()));
//		
//		response.close();
//	}
//	
//	/**
//	 * HttpServlet implementations for testing.
//	 */
//	public class WebResourceSenderServlet extends HttpServlet {
//
//		private static final long serialVersionUID = 1L;
//
//		private WebResourceSender webResourceSender;
//
//		public WebResourceSenderServlet(WebResourceSender webResourceSender) {
//			this.webResourceSender = webResourceSender;
//		}
//
//		@Override
//		protected void doGet(HttpServletRequest request, HttpServletResponse response)
//				throws ServletException, IOException {
//			String name = request.getParameter("name");
//			webResourceSender.sendByUniqueName(name, request, response);
//		}
//	}
}
