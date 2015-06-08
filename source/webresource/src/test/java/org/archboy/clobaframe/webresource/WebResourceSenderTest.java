package org.archboy.clobaframe.webresource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;


/**
 *
 * @author yang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml"})
public class WebResourceSenderTest {

	private Server server;

	@Inject
	private WebResourceManager resourceManager;

	@Inject
	private WebResourceSender resourceSender;

	@Before
	public void setUp() throws Exception {
		// start http server
		server = new Server(18080);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);

		ServletHolder servletHolder1 = new ServletHolder(
				new TestingWebResourceSenderServlet(resourceSender));
		context.addServlet(servletHolder1,"/resource/*");

		server.start();
	}

	@After
	public void tearDown() throws Exception {
		// stop http server
		server.stop();
	}

	@Test
	public void testSend() throws FileNotFoundException {

		WebResourceInfo webResource1 = resourceManager.getServerResource("test.png");

		CloseableHttpClient client = HttpClients.createDefault();

		// test get by resource name
		HttpGet method1 = new HttpGet("http://localhost:18080/resource/test.png");

		try {
			assertResponseContentEquals(client, method1, webResource1);
		} catch (IOException e) {
			fail(e.getMessage());
		}

		// test none exists resource
		HttpGet method3 = new HttpGet("http://localhost:18080/resource/none-exists");
		try {
			assertStatusCodeEquals(client, method3, HttpStatus.SC_NOT_FOUND);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		// test get by resource name with version number
		String location1 = resourceManager.getLocation(webResource1);
		HttpGet method4 = new HttpGet("http://localhost:18080" + location1);
		try {
			assertResponseContentEquals(client, method4, webResource1);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		IOUtils.closeQuietly(client);
	}

	public void testSendByVersionName() {
		//
	}

	private void assertResponseContentEquals(CloseableHttpClient client, HttpGet method, byte[] data)
			throws IllegalStateException, IOException {
		CloseableHttpResponse response = client.execute(method);
		assertArrayEquals(data, EntityUtils.toByteArray(response.getEntity()));
		response.close();
	}

	private void assertResponseContentEquals(CloseableHttpClient client, HttpGet method, WebResourceInfo resourceInfo) throws IOException {
		InputStream in = resourceInfo.getContent();
		byte[] content = IOUtils.toByteArray(in);
		in.close();

		assertResponseContentEquals(client, method, content);
	}

	private void assertStatusCodeEquals(CloseableHttpClient client, HttpGet method, int status)
			throws IOException {
		CloseableHttpResponse response = client.execute(method);
		int statusCode = response.getStatusLine().getStatusCode();
		EntityUtils.consume(response.getEntity());
		assertEquals(status, statusCode);
		
		response.close();
	}

	/**
	 * HttpServlet implementations for testing.
	 */
	public static class TestingWebResourceSenderServlet extends HttpServlet {

		private static final long serialVersionUID = 1L;

		private WebResourceSender webResourceSender;

		public TestingWebResourceSenderServlet(WebResourceSender webResourceSender) {
			this.webResourceSender = webResourceSender;
		}

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			String name = request.getPathInfo();
			String version = request.getQueryString();
			String versionName = (version == null ? name : name + "?" + version);
			
			webResourceSender.sendByVersionName(versionName, request, response);
		}
	}
}
