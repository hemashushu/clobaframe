package org.archboy.clobaframe.io.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.archboy.clobaframe.io.ResourceInfo;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.archboy.clobaframe.io.MimeTypeDetector;
import org.archboy.clobaframe.io.file.FileBaseResourceInfoFactory;
import org.archboy.clobaframe.io.file.impl.DefaultFileBaseResourceInfoFactory;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

/**
 *
 * @author yang
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class ResourceSenderTest {

	private static final String DEFAULT_SAMPLE_FILE_DIRECTORY = "sample/data";
	private String sampleFileDirectory = DEFAULT_SAMPLE_FILE_DIRECTORY;
	
	private String sampleFileName1 = sampleFileDirectory + "/test.jpg";
	private String sampleFileUrl1 = "http://localhost:18080/resource?name=test.jpg";
	
	private String sampleFileName2 = sampleFileDirectory + "/test.css";
	private String sampleFileUrl2 = "http://localhost:18080/resource?name=test.css";

	private Server server;
	//private Tomcat tomcat;
	
	@Inject
	private ResourceSender resourceSender;

	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private MimeTypeDetector mimeTypeDetector;

	@Before
	public void setUp() throws Exception {
		// start jetty http server
		server = new Server(18080);
		
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);
		
		ServletHolder servletHolder = new ServletHolder(new ResourceSenderServlet());
		context.addServlet(servletHolder,"/resource");
		
		server.start();
		
		// start tomcat http server
//		tomcat = new Tomcat();
//		tomcat.setPort(18080);
//		
//		File base = new File(System.getProperty("java.io.tmpdir"));
//		Context context = tomcat.addContext("", base.getAbsolutePath());
//		
//		Tomcat.addServlet(context, "resourceServlet", new ResourceSenderServlet());
//		context.addServletMapping("/resource", "resourceServlet");
//		
//		tomcat.start();
//		tomcat.getServer().await();
	}

	@After
	public void tearDown() throws Exception {
		// stop http server
		server.stop();
		//tomcat.stop();
	}

	@Test
	public void testBaseSend() throws IOException{
		byte[] sampleData = getFileContent(sampleFileName1);

		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet method = new HttpGet(sampleFileUrl1);
		CloseableHttpResponse response = client.execute(method);
		
		try {
			
			int statusCode = response.getStatusLine().getStatusCode();
			assertEquals(HttpStatus.SC_OK, statusCode);
			InputStream in = response.getEntity().getContent();
			byte[] data = IOUtils.toByteArray(in);
			in.close();
			assertArrayEquals(sampleData, data);
		}catch(IOException e){
			fail(e.getMessage());
		} finally {
			response.close();
		}
		
		IOUtils.closeQuietly(client);
	}

	@Test
	public void testSendWithGZip() throws IOException{
		
		CloseableHttpClient client = HttpClients.createDefault();
		
		// image/jpeg will not be compressed
		HttpGet method1 = new HttpGet(sampleFileUrl1);
		CloseableHttpResponse response1 = client.execute(method1);
		
		EntityUtils.consume(response1.getEntity());
		response1.close();
		assertNull(response1.getFirstHeader("Content-Encoding"));
		
		// image/jpeg will not be compressed even specify accept-encoding
		HttpGet method2 = new HttpGet(sampleFileUrl1);
		method2.setHeader("Accept-Encoding", "gzip");
		CloseableHttpResponse response2 = client.execute(method2);
		EntityUtils.consume(response2.getEntity());
		response2.close();
		assertNull(response2.getFirstHeader("Content-Encoding"));
		
		// test gzip
		byte[] sampleData = getFileContent(sampleFileName2);
		
		// text/css will not be compressed if no accept-encoding specify
		HttpGet method3 = new HttpGet(sampleFileUrl2);
		method3.removeHeaders("Accept-Encoding");
		method3.setHeader("Accept-Encoding", "no-this-method");
		CloseableHttpResponse response3 = client.execute(method3);
		EntityUtils.consume(response3.getEntity());
		response3.close();
		
		assertNull(response3.getFirstHeader("Content-Encoding"));
		
		long contentLength3 = Long.parseLong(response3.getFirstHeader("Content-Length").getValue());
		assertEquals(sampleData.length, contentLength3);

		// test gzip
		HttpGet method4 = new HttpGet(sampleFileUrl2);
		method4.setHeader("Accept-Encoding", "gzip");
		CloseableHttpResponse response4 = client.execute(method4);
		
		try {
			// NOTE:: the http client hide the content-encoding and content-length header while 
			// receiving gzip data.
			//
			//Header header = response4.getFirstHeader("Content-Encoding");
			//assertEquals("gzip", header.getValue());
			//
			//long contentLength4 = Long.parseLong(response4.getFirstHeader("Content-Length").getValue());
			//assertTrue(sampleData.length > contentLength4);
			
			HttpEntity httpEntity4 = response4.getEntity();
			InputStream in = httpEntity4.getContent();
			byte[] data = IOUtils.toByteArray(in);
			in.close();
			assertArrayEquals(sampleData, data);
		}catch(IOException e){
			fail(e.getMessage());
		} finally {
			response4.close();
		}
		
		IOUtils.closeQuietly(client);
	}
	
	@Test
	public void testSendWithPartialContent() throws IOException {
		byte[] sampleData = getFileContent(sampleFileName1);

		int middlePosition = sampleData.length / 2;
		byte[] subSampleData1 = Arrays.copyOfRange(sampleData,
				2, sampleData.length);
		byte[] subSampleData2 = Arrays.copyOfRange(sampleData, middlePosition,
				middlePosition + 2);
		byte[] subSampleData3 = Arrays.copyOfRange(sampleData,
				sampleData.length - 2, sampleData.length);

		CloseableHttpClient client = HttpClients.createDefault();

		// get from last 2 bytes
		HttpGet method1 = new HttpGet(sampleFileUrl1);
		method1.setHeader("Range", "bytes=2-");

		// get middle 2 bytes
		HttpGet method2 = new HttpGet(sampleFileUrl1);
		method2.setHeader("Range", "bytes=" + middlePosition + "-"
				+ (middlePosition + 1));

		// get last 2 bytes
		HttpGet method3 = new HttpGet(sampleFileUrl1);
		method3.setHeader("Range", "bytes=-2");

		try {
			checkStatusCodeAndContent(client, method1, HttpStatus.SC_PARTIAL_CONTENT, subSampleData1);
			checkStatusCodeAndContent(client, method2, HttpStatus.SC_PARTIAL_CONTENT, subSampleData2);
			checkStatusCodeAndContent(client, method3, HttpStatus.SC_PARTIAL_CONTENT, subSampleData3);
		} catch (IOException e) {
			fail(e.getMessage());
		} finally {

		}
		
		IOUtils.closeQuietly(client);
	}

	@Test
	public void testSendWithLastModifiedChecking() throws IOException {
		TimeZone timeZone = TimeZone.getTimeZone("GMT");
		SimpleDateFormat format = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		format.setTimeZone(timeZone);

		Date lastModified = null;

		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet method1 = new HttpGet(sampleFileUrl1);

		CloseableHttpResponse response = client.execute(method1);
		
		try {
			int statusCode = response.getStatusLine().getStatusCode();
			assertEquals(HttpStatus.SC_OK, statusCode);

			Header lastModifiedHeader = response.getFirstHeader("Last-Modified");
			assertNotNull(lastModifiedHeader);
			lastModified = format.parse(lastModifiedHeader.getValue());

			EntityUtils.consume(response.getEntity());

		} catch (IOException e) {
			fail(e.getMessage());
		} catch (ParseException e) {
			fail(e.getMessage());
		} finally {
			response.close();
		}

		// test if-modified-since
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(lastModified);
		calendar.add(Calendar.SECOND, -1);
		Date oneSecondBefore = calendar.getTime();

		calendar.add(Calendar.SECOND, 2);
		Date oneSecondAfter = calendar.getTime();

		HttpGet method2 = new HttpGet(sampleFileUrl1);
		method2.setHeader("If-Modified-Since",
				format.format(oneSecondBefore));

		HttpGet method3 = new HttpGet(sampleFileUrl1);
		method3.setHeader("If-Modified-Since",
				format.format(lastModified));

		HttpGet method4 = new HttpGet(sampleFileUrl1);
		method4.setHeader("If-Modified-Since",
				format.format(oneSecondAfter));

		try {
			checkStatusCode(client, method2, HttpStatus.SC_OK);
			checkStatusCode(client, method3, HttpStatus.SC_NOT_MODIFIED);
			checkStatusCode(client, method4, HttpStatus.SC_NOT_MODIFIED);
		} catch (IOException e) {
			fail(e.getMessage());
		} finally {

		}
		
		IOUtils.closeQuietly(client);
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

	private void checkStatusCodeAndContent(CloseableHttpClient client, HttpGet method, int status, byte[] data)
			throws IllegalStateException, IOException {
		CloseableHttpResponse response = client.execute(method);
		int statusCode = response.getStatusLine().getStatusCode();
		assertEquals(status, statusCode);
		assertArrayEquals(data, EntityUtils.toByteArray(response.getEntity()));
		response.close();
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
	public class ResourceSenderServlet extends HttpServlet {

		private static final long serialVersionUID = 1L;

		private FileBaseResourceInfoFactory fileBaseResourceInfoFactory = new DefaultFileBaseResourceInfoFactory(mimeTypeDetector);
		
		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			
			String filename = request.getParameter("name");
			String sampleFile = sampleFileDirectory + "/" + filename;
			ResourceInfo resourceInfo = fileBaseResourceInfoFactory.make(getFileByName(sampleFile));
			resourceSender.send(resourceInfo, null, request, response);
		}
	}
}
