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
package org.archboy.clobaframe.webio;

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
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author young
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class MultipartFormResourceReceiverTest {

	private static final String DEFAULT_SAMPLE_FILE1 = "sample/data/test.jpg";
	private static final String DEFAULT_SAMPLE_FILE2 = "sample/data/test.png";
	private static final String DEFAULT_POST_URL = "http://localhost:18080/post";

	private String sampleFile1 = DEFAULT_SAMPLE_FILE1;
	private String sampleFile2 = DEFAULT_SAMPLE_FILE2;
	private String postUrl = DEFAULT_POST_URL;

	private Server server;

	@Autowired
	private MultipartFormResourceReceiver resourceReceiver;

	@Autowired
	private ResourceLoader resourceLoader;

	@Before
	public void setUp() throws Exception {
		// start http server
		server = new Server(18080);
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);

		Servlet receivePage = new MultipartPostReceiverServlet();
		context.addServlet(new ServletHolder(receivePage),"/post");

		server.start();
	}

	@After
	public void tearDown() throws Exception {
		// stop http server
		server.stop();
	}

	@Test
	public void testBaseFormDataReceive() throws UnsupportedEncodingException{

		HttpClient client = new DefaultHttpClient();
		HttpPost method = new HttpPost(postUrl);

		ContentBody part1 = new StringBody("001");
		ContentBody part2 = new StringBody("foo");

		MultipartEntity entity = new MultipartEntity();
		entity.addPart("id", part1);
		entity.addPart("name", part2);
		method.setEntity(entity);

		try {
			HttpResponse response = client.execute(method);
			int status = response.getStatusLine().getStatusCode();
			assertEquals(HttpStatus.SC_OK, status);

			InputStream in = response.getEntity().getContent();
			String content = IOUtils.toString(in);
			in.close();

			List<Map<String, String>> items = deserialize(content);

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
			client.getConnectionManager().shutdown();
		}
	}

	@Test
	public void testMultiPartFormDataReceive() throws IOException{

		HttpClient client = new DefaultHttpClient();
		HttpPost method = new HttpPost(postUrl);

		ContentBody part1 = new StringBody("001");
		ContentBody part2 = new FileBody(getFileByName(sampleFile1));
		ContentBody part3 = new FileBody(getFileByName(sampleFile2));

		MultipartEntity entity = new MultipartEntity();
		entity.addPart("id", part1);
		entity.addPart("file1", part2);
		entity.addPart("file2", part3);
		method.setEntity(entity);

		try {
			HttpResponse response = client.execute(method);
			int status = response.getStatusLine().getStatusCode();
			assertEquals(HttpStatus.SC_OK, status);

			InputStream in = response.getEntity().getContent();
			String content = IOUtils.toString(in);
			in.close();

			List<Map<String, String>> items = deserialize(content);

			Map<String, String> item1 = items.get(0); //"id");
			assertNotNull(item1);
			assertEquals("id", item1.get("name"));
			assertEquals("001", item1.get("value"));

			Map<String, String> item2 = items.get(1); //"file1");
			assertNotNull(item2);
			assertEquals("file1", item2.get("name"));
			assertEquals("test.jpg", item2.get("fileName"));
			assertEquals(DigestUtils.sha256Hex(getFileContent(sampleFile1)), item2.get("contentHash"));

			Map<String, String> item3 = items.get(2); //"file2");
			assertNotNull(item3);
			assertEquals("file2", item3.get("name"));
			assertEquals("test.png", item3.get("fileName"));
			assertEquals(DigestUtils.sha256Hex(getFileContent(sampleFile2)), item3.get("contentHash"));

		} catch (IOException e) {
			fail(e.getMessage());
		} finally {
			client.getConnectionManager().shutdown();
		}
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
		IOUtils.closeQuietly(in);
		return data;
	}

	public class MultipartPostReceiverServlet extends HttpServlet {

		private static final long serialVersionUID = 1L;

		@Override
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

			//Map<String, Object> items = new HashMap<String, Object>();
			List<Map<String, String>> items = new ArrayList<Map<String,String>>();

			List<MultipartFormResourceInfo> resourceInfos = resourceReceiver.receive(request);
			for(MultipartFormResourceInfo resourceInfo : resourceInfos){

				Map<String, String> itemInfo = new HashMap<String, String>();
				itemInfo.put("name", resourceInfo.getName());

				if (resourceInfo.isFile()){
					itemInfo.put("fileName", resourceInfo.getFileName());
					itemInfo.put("contentType", resourceInfo.getContentType());
					itemInfo.put("length", new Long(resourceInfo.getContentLength()).toString());

					ResourceContent resourceContent = resourceInfo.getContentSnapshot();
					String sha256hex = DigestUtils.sha256Hex(resourceContent.getInputStream());
					resourceContent.close();

					itemInfo.put("contentHash", sha256hex);
				}else{
					itemInfo.put("value", resourceInfo.getContentAsString());
				}

				items.add(itemInfo);
			}

			// set response
			response.addHeader("content-type", "application/json; charset=UTF-8");

			OutputStream out = response.getOutputStream();
			String content = serialize(items);
			IOUtils.write(content, out);
			out.close();
		}

	}

	private String serialize(List<Map<String, String>> items){
		StringBuilder builder = new StringBuilder();
		for(Map<String, String> item : items){
			for(String key : item.keySet()){
				builder.append(key);
				builder.append("=");
				builder.append(item.get(key));
				builder.append(",");
			}
			builder.append("\n");
		}
		return builder.toString();
	}

	private List<Map<String, String>> deserialize(String content){
		List<Map<String, String>> items = new ArrayList<Map<String,String>>();
		String[] lines = content.split("\n");

		for(int idx=0; idx<lines.length; idx++){
			String line = lines[idx];
			if (StringUtils.isEmpty(line)){
				continue;
			}

			Map<String, String> item = new HashMap<String, String>();
			String[] props = line.split(",");
			for(int subIdx=0; subIdx<props.length; subIdx++){
				String prop = props[subIdx];
				if (StringUtils.isEmpty(prop)){
					continue;
				}

				int equalsPos = prop.indexOf("=");
				item.put(prop.substring(0, equalsPos), prop.substring(equalsPos+1));
			}
			items.add(item);
		}

		return items;
	}
}
