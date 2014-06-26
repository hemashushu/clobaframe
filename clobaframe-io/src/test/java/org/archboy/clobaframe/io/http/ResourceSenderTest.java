/*
 * Copyright 2011 Spark Young (sparkyoungs@gmail.com). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.archboy.clobaframe.io.ResourceInfo;
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
import org.archboy.clobaframe.io.file.FileBaseResourceInfoFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

/**
 *
 * @author young
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class ResourceSenderTest {

	private static final String DEFAULT_SAMPLE_FILE = "sample/data/test.txt";
	private static final String DEFAULT_SAMPLE_FILE_URL = "http://localhost:18080/test.txt";

	private String sampleFile = DEFAULT_SAMPLE_FILE;
	private String sampleFileUrl = DEFAULT_SAMPLE_FILE_URL;

	private Server server;

	@Inject
	private ResourceSender resourceSender;

	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private FileBaseResourceInfoFactory fileBaseResourceInfoFactory;

	@Before
	public void setUp() throws Exception {
		// start http server
		server = new Server(18080);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);

		ResourceInfo resourceInfo = fileBaseResourceInfoFactory.make(getFileByName(sampleFile));
		ServletHolder servletHolder = new ServletHolder(
				new ResourceSenderServlet(resourceSender, resourceInfo));
		
		context.addServlet(servletHolder,"/test.txt");

		server.start();
	}

	@After
	public void tearDown() throws Exception {
		// stop http server
		server.stop();
	}

	@Test
	public void testBaseSend() throws IOException{
		byte[] sampleData = getFileContent(sampleFile);

		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet method = new HttpGet(sampleFileUrl);
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
	public void testSendWithLastModified() throws IOException {
		TimeZone timeZone = TimeZone.getTimeZone("GMT");
		SimpleDateFormat format = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		format.setTimeZone(timeZone);

		Date lastModified = null;

		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet method1 = new HttpGet(sampleFileUrl);

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

		HttpGet method2 = new HttpGet(sampleFileUrl);
		method2.setHeader("If-Modified-Since",
				format.format(oneSecondBefore));

		HttpGet method3 = new HttpGet(sampleFileUrl);
		method3.setHeader("If-Modified-Since",
				format.format(lastModified));

		HttpGet method4 = new HttpGet(sampleFileUrl);
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

	@Test
	public void testSendPartialContent() throws IOException {
		byte[] sampleData = getFileContent(sampleFile);

		int middlePosition = sampleData.length / 2;
		byte[] subSampleData1 = Arrays.copyOfRange(sampleData,
				2, sampleData.length);
		byte[] subSampleData2 = Arrays.copyOfRange(sampleData, middlePosition,
				middlePosition + 2);
		byte[] subSampleData3 = Arrays.copyOfRange(sampleData,
				sampleData.length - 2, sampleData.length);

		CloseableHttpClient client = HttpClients.createDefault();

		// get from last 2 bytes
		HttpGet method1 = new HttpGet(sampleFileUrl);
		method1.setHeader("Range", "bytes=2-");

		// get middle 2 bytes
		HttpGet method2 = new HttpGet(sampleFileUrl);
		method2.setHeader("Range", "bytes=" + middlePosition + "-"
				+ (middlePosition + 1));

		// get last 2 bytes
		HttpGet method3 = new HttpGet(sampleFileUrl);
		method3.setHeader("Range", "bytes=-2");

		try {
			checkResponseContent(client, method1, HttpStatus.SC_PARTIAL_CONTENT, subSampleData1);
			checkResponseContent(client, method2, HttpStatus.SC_PARTIAL_CONTENT, subSampleData2);
			checkResponseContent(client, method3, HttpStatus.SC_PARTIAL_CONTENT, subSampleData3);
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

	private void checkResponseContent(CloseableHttpClient client, HttpGet method, int status, byte[] data)
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

		private ResourceSender resourceSender;
		private ResourceInfo resourceInfo;

		public ResourceSenderServlet(ResourceSender resourceSender, ResourceInfo resourceInfo) {
			this.resourceSender = resourceSender;
			this.resourceInfo = resourceInfo;
		}

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			resourceSender.send(resourceInfo, request, response);
		}
	}
}
