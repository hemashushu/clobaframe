package org.archboy.clobaframe.io.http;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.archboy.clobaframe.io.ResourceInfo;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.utils.DateUtils;
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
public class CacheResourceSenderTest {

	private static final String DEFAULT_SAMPLE_FILE_DIRECTORY = "sample/data";
	private String sampleFileDirectory = DEFAULT_SAMPLE_FILE_DIRECTORY;
	
	private String sampleFileName1 = sampleFileDirectory + "/test.jpg";
	private String sampleFileUrl1 = "http://localhost:18080/resource?name=test.jpg";
	
	private Server server;
	
	@Inject
	private CacheResourceSender cacheResourceSender;

	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private MimeTypeDetector mimeTypeDetector;
	
	//@Inject
	private FileBaseResourceInfoFactory fileBaseResourceInfoFactory;

	@Before
	public void setUp() throws Exception {
		fileBaseResourceInfoFactory = new DefaultFileBaseResourceInfoFactory(mimeTypeDetector);
		
		// start jetty http server
		server = new Server(18080);
		
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);
		
		ServletHolder servletHolder = new ServletHolder(new ResourceSenderServlet());
		context.addServlet(servletHolder,"/resource");
		
		server.start();
	}

	@After
	public void tearDown() throws Exception {
		// stop http server
		server.stop();
	}

	@Test
	public void testCacheResourceSend() throws IOException, ParseException{

		CloseableHttpClient client = HttpClients.createDefault();
		
	
		// try send with cache 180 seconds.
		Date now = new Date();
		HttpGet method1 = new HttpGet(sampleFileUrl1 + "&cacheSeconds=180");
		CloseableHttpResponse response1 = client.execute(method1);
		
		assertEquals("public, max-age=180", response1.getFirstHeader("Cache-Control").getValue());
		String expires = response1.getFirstHeader("Expires").getValue();
		
		//Sun, 22 Mar 2015 20:06:16 GMT
		//SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzz");
		//dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		//Date date = dateFormat.parse(expires);
		
		Date date = DateUtils.parseDate(expires);
		
		long span = (date.getTime() - now.getTime()) /1000;
		assertTrue(span <= 180);
		EntityUtils.consume(response1.getEntity());
		response1.close();
		
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
	 * HttpServlet implementations for testing.
	 */
	public class ResourceSenderServlet extends HttpServlet {

		private static final long serialVersionUID = 1L;

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			
			int cacheSeconds = 0;
			
			String filename = request.getParameter("name");
			String seconds = request.getParameter("cacheSeconds");
			
			if (StringUtils.isNoneEmpty(seconds)){
				cacheSeconds = Integer.valueOf(seconds);
			}
			
			String sampleFile = sampleFileDirectory + "/" + filename;
			ResourceInfo resourceInfo = fileBaseResourceInfoFactory.make(getFileByName(sampleFile));
			cacheResourceSender.send(resourceInfo, CacheResourceSender.CACHE_CONTROL_PUBLIC, cacheSeconds, null, request, response);
		}
	}
}
