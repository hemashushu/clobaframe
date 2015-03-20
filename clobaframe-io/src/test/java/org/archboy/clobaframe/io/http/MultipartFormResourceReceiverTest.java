package org.archboy.clobaframe.io.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.archboy.clobaframe.io.MimeTypeDetector;
import org.archboy.clobaframe.io.TemporaryResources;
import org.archboy.clobaframe.io.impl.DefaultTemporaryResources;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

/**
 *
 * @author yang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class MultipartFormResourceReceiverTest {

	private String sampleFile1 = "sample/data/test.jpg";
	private String sampleFile2 = "sample/data/test.png";
	private String postUrl = "http://localhost:18080/post";

	private Server server;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Inject
	private MultipartFormResourceReceiver resourceReceiver;

	@Inject
	private MimeTypeDetector mimeTypeDetector;
	
	@Inject
	private ResourceLoader resourceLoader;

	@Before
	public void setUp() throws Exception {
		// start http server
		server = new Server(18080);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);

		Servlet receivePage = new MultipartPostReceiverServlet(resourceReceiver);
		context.addServlet(new ServletHolder(receivePage),"/post");

		server.start();
	}

	@After
	public void tearDown() throws Exception {
		// stop http server
		server.stop();
	}

	@Test
	public void testBaseFormDataReceive() throws UnsupportedEncodingException, IOException{

		//HttpClient client = new DefaultHttpClient();
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost method = new HttpPost(postUrl);

		ContentBody part1 = new StringBody("001", ContentType.TEXT_PLAIN);
		ContentBody part2 = new StringBody("foo", ContentType.TEXT_PLAIN);

		//MultipartEntity entity = new MultipartEntity();
		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		
		entityBuilder.addPart("id", part1);
		entityBuilder.addPart("name", part2);
		method.setEntity(entityBuilder.build());

		CloseableHttpResponse response = client.execute(method);
		
		try {
			int status = response.getStatusLine().getStatusCode();
			assertEquals(HttpStatus.SC_OK, status);

			InputStream in = response.getEntity().getContent();
//			String content = IOUtils.toString(in);
//			in.close();

			//List<Map<String, String>> items = deserialize(content);
			List<Map<String, String>> items = objectMapper.readValue(
					in, 
					new TypeReference<List<Map<String, String>>>() {});
			
			in.close();

			Map<String, String> item1 = items.get(0);
			assertNotNull(item1);
			assertEquals("id", item1.get("name"));
			assertEquals("001", item1.get("value"));

			Map<String, String> item2 = items.get(1);
			assertNotNull(item2);
			assertEquals("name", item2.get("name"));
			assertEquals("foo", item2.get("value"));

		} catch (IOException e) {
			fail(e.getMessage());
		}finally{
			response.close();
		}
		
		IOUtils.closeQuietly(client);
	}

	@Test
	public void testMultiPartFormDataReceive() throws IOException{

		//HttpClient client = new DefaultHttpClient();
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost method = new HttpPost(postUrl);

		File file1 = getFileByName(sampleFile1);
		String fileName1 = file1.getName();
		String mimeType1 = mimeTypeDetector.getByExtensionName(fileName1);
		
		File file2 = getFileByName(sampleFile2);
		String fileName2 = file2.getName();
		String mimeType2 = mimeTypeDetector.getByExtensionName(fileName2);
		
		ContentBody part1 = new StringBody("001", ContentType.TEXT_PLAIN);
		ContentBody part2 = new FileBody(file1, ContentType.create(mimeType1), fileName1);
		ContentBody part3 = new FileBody(file2, ContentType.create(mimeType2), fileName2);

		//MultipartEntity entity = new MultipartEntity();
		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		entityBuilder.addPart("id", part1);
		entityBuilder.addPart("file1", part2);
		entityBuilder.addPart("file2", part3);
		method.setEntity(entityBuilder.build());

		CloseableHttpResponse response = client.execute(method);
		
		try {
			//HttpResponse response = client.execute(method);
			int status = response.getStatusLine().getStatusCode();
			assertEquals(HttpStatus.SC_OK, status);

			InputStream in = response.getEntity().getContent();
			//String content = IOUtils.toString(in);
			//in.close();

			//List<Map<String, String>> items = deserialize(content);
			List<Map<String, String>> items = objectMapper.readValue(
					in, 
					new TypeReference<List<Map<String, String>>>() {});
			
			in.close();

			Map<String, String> item1 = items.get(0); //"id");
			assertEquals("id", item1.get("name"));
			assertEquals("001", item1.get("value"));

			Map<String, String> item2 = items.get(1); //"file1");
			assertEquals("file1", item2.get("name"));
			assertEquals(fileName1, item2.get("fileName"));
			assertEquals(mimeType1, item2.get("mimeType"));
			assertEquals(DigestUtils.sha256Hex(getFileContent(sampleFile1)), item2.get("contentHash"));

			Map<String, String> item3 = items.get(2); //"file2");
			assertEquals("file2", item3.get("name"));
			assertEquals(fileName2, item3.get("fileName"));
			assertEquals(mimeType2, item3.get("mimeType"));
			assertEquals(DigestUtils.sha256Hex(getFileContent(sampleFile2)), item3.get("contentHash"));

		} catch (IOException e) {
			fail(e.getMessage());
		} finally {
			response.close();
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

	public class MultipartPostReceiverServlet extends HttpServlet {

		private static final long serialVersionUID = 1L;

		private MultipartFormResourceReceiver receiver;

		public MultipartPostReceiverServlet(MultipartFormResourceReceiver receiver) {
			this.receiver = receiver;
		}
		
		@Override
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

			List<Map<String, String>> items = new ArrayList<Map<String,String>>();
			TemporaryResources temporaryResources = new DefaultTemporaryResources();
			
			List<MultipartFormResourceInfo> resourceInfos = receiver.receive(request, temporaryResources);
			for(MultipartFormResourceInfo resourceInfo : resourceInfos){

				Map<String, String> itemInfo = new HashMap<String, String>();
				itemInfo.put("name", resourceInfo.getFieldName());

				if (resourceInfo.isFormField()){
					itemInfo.put("value", resourceInfo.getContentAsString());
				}else{
					itemInfo.put("fileName", resourceInfo.getFileName());
					itemInfo.put("mimeType", resourceInfo.getMimeType());
					itemInfo.put("length", new Long(resourceInfo.getContentLength()).toString());

					InputStream in = resourceInfo.getContent();
					String sha256hex = DigestUtils.sha256Hex(in);
					in.close();

					itemInfo.put("contentHash", sha256hex);
				}

				items.add(itemInfo);
			}

			// set response
			response.addHeader("content-type", "application/json; charset=UTF-8");

			OutputStream out = response.getOutputStream();
			objectMapper.writeValue(out, items);
			//String content = serialize(items);
			//IOUtils.write(content, out);
			out.close();
			
			// clean temp resources
			temporaryResources.close();
		}
		
//		private String serialize(List<Map<String, String>> items){
//			StringBuilder builder = new StringBuilder();
//			for(Map<String, String> item : items){
//				for(String key : item.keySet()){
//					builder.append(key);
//					builder.append("=");
//					builder.append(item.get(key));
//					builder.append(",");
//				}
//				builder.append("\n");
//			}
//			return builder.toString();
//		}

	}

//	private List<Map<String, String>> deserialize(String content){
//		List<Map<String, String>> items = new ArrayList<Map<String,String>>();
//		String[] lines = content.split("\n");
//
//		for(int idx=0; idx<lines.length; idx++){
//			String line = lines[idx];
//			if (StringUtils.isEmpty(line)){
//				continue;
//			}
//
//			Map<String, String> item = new HashMap<String, String>();
//			String[] props = line.split(",");
//			for(int subIdx=0; subIdx<props.length; subIdx++){
//				String prop = props[subIdx];
//				if (StringUtils.isEmpty(prop)){
//					continue;
//				}
//
//				int equalsPos = prop.indexOf("=");
//				item.put(prop.substring(0, equalsPos), prop.substring(equalsPos+1));
//			}
//			items.add(item);
//		}
//
//		return items;
//	}
}
