package org.archboy.clobaframe.webresource.local;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceSender;
import org.archboy.clobaframe.webresource.WebResourceManager;
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
	private WebResourceManager resourceService;

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
				new WebResourceSenderServlet(resourceSender, false));
		context.addServlet(servletHolder1,"/get");

		ServletHolder servletHolder2 = new ServletHolder(
				new WebResourceSenderServlet(resourceSender, true));
		context.addServlet(servletHolder2,"/getByUniqueName");

		server.start();
	}

	@After
	public void tearDown() throws Exception {
		// stop http server
		server.stop();
	}

	@Test
	public void testSend() throws FileNotFoundException {

		WebResourceInfo webResource1 = resourceService.getResource("test.png");
		WebResourceInfo webResource2 = resourceService.getResource("test.css");

		CloseableHttpClient client = HttpClients.createDefault();

		HttpGet method1 = new HttpGet("http://localhost:18080/get?name=" + webResource1.getName());
		HttpGet method2 = new HttpGet("http://localhost:18080/getByUniqueName?name=" + webResource2.getUniqueName());

		try {
			checkResponseContent(client, method1, webResource1);
			checkResponseContent(client, method2, webResource2);
		} catch (IOException e) {
			fail(e.getMessage());
		}

		// test none exists resource
		HttpGet method3 = new HttpGet("http://localhost:18080/get?name=noneExists");
		try {
			checkStatusCode(client, method3, HttpStatus.SC_NOT_FOUND);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		IOUtils.closeQuietly(client);
	}

	public void testSendByUniqueName() {
		//
	}

	private void checkResponseContent(CloseableHttpClient client, HttpGet method, byte[] data)
			throws IllegalStateException, IOException {
		CloseableHttpResponse response = client.execute(method);
		//int statusCode = response.getStatusLine().getStatusCode();
		//assertEquals(HttpStatus.SC_PARTIAL_CONTENT, statusCode);
		//assertEquals(2, response.getEntity().getContentLength());
		assertArrayEquals(data, EntityUtils.toByteArray(response.getEntity()));
		
		response.close();
	}

	private void checkResponseContent(CloseableHttpClient client, HttpGet method, WebResourceInfo resourceInfo) throws IOException {
		//ResourceContent resourceContent = resourceInfo.getContentSnapshot();
		InputStream in = resourceInfo.getInputStream(); // resourceContent.getInputStream();
		byte[] content = IOUtils.toByteArray(in);
		in.close();

		checkResponseContent(client, method, content);
	}

	private void checkStatusCode(CloseableHttpClient client, HttpGet method, int status)
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
	public class WebResourceSenderServlet extends HttpServlet {

		private static final long serialVersionUID = 1L;

		private WebResourceSender webResourceSender;
		private boolean byUniqueName;

		public WebResourceSenderServlet(WebResourceSender webResourceSender, boolean byUniqueName) {
			this.webResourceSender = webResourceSender;
			this.byUniqueName = byUniqueName;
		}

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			String name = request.getParameter("name");
			if (byUniqueName) {
				webResourceSender.sendByUniqueName(name, request, response);
			}else{
				webResourceSender.send(name, request, response);
			}
		}
	}
}
