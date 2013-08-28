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
package org.archboy.clobaframe.blobstore.local;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.archboy.clobaframe.blobstore.BlobInfo;
import org.archboy.clobaframe.blobstore.BlobInfoFactory;
import org.archboy.clobaframe.blobstore.BlobInfoPartialCollection;
import org.archboy.clobaframe.blobstore.BlobKey;
import org.archboy.clobaframe.blobstore.Blobstore;
import org.archboy.clobaframe.io.ResourceContent;
import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class LocalBlobstoreTest {

	@Autowired
	private Blobstore blobstore;

//	@Autowired
//	private BlobstoreBucket blobstoreBucket;

	@Autowired
	private BlobInfoFactory blobInfoFactory;

	private static final String DEFAULT_BUCKET_NAME = "test-clobaframe-bucket";

	@Value("${test.blobstore.bucketName}")
	private String bucketName = DEFAULT_BUCKET_NAME;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExistBucket() {
		//
	}

	@Test
	public void testCreateBucket() throws IOException {

		// delete exist first
		if (blobstore.existBucket(bucketName)){

			// clean all blobs.
			BlobInfoPartialCollection collection = blobstore.list(new BlobKey(bucketName, null));
			for (BlobInfo info : collection) {
				blobstore.delete(info.getBlobKey());
			}

			// delete bucket.
			blobstore.deleteBucket(bucketName);
			assertFalse(blobstore.existBucket(bucketName));
			return;
		}

		// test create
		blobstore.createBucket(bucketName);

		// test exist
		assertTrue(blobstore.existBucket(bucketName));

		// test exist
		assertFalse(blobstore.existBucket("noneExists"));

		// test create duplicate name bucket
		blobstore.createBucket(bucketName);

		// test delete
		blobstore.deleteBucket(bucketName);
		assertFalse(blobstore.existBucket(bucketName));

		// test delete none exists
		blobstore.deleteBucket("noneExists");
	}

	public void testDeleteBucket() {
		//
	}

	@Test
	public void testPut() throws IOException {

		String key1 = "b001";
		String key2 = "b002";
		String key3 = "b003";

		// check bucket first
		if (!blobstore.existBucket(bucketName)){
			blobstore.createBucket(bucketName);
		}

		// clean first
		cleanByKey(key1);
		cleanByKey(key2);
		cleanByKey(key3);

		BlobKey blobKey1 = new BlobKey(bucketName, key1);

		// test put blob
		// NOTE:: the local implements does not support meta-data.

//		Map<String, String> metadata = new HashMap<String, String>();
//		metadata.put("author", "test");
//		metadata.put("price", "99.0");
//		writeContent(blobstore, blobKey1, "hello", metadata);

		writeContent(blobstore, blobKey1, "hello");

		// test get blob by key
		BlobInfo blobInfoByKey1 = blobstore.get(blobKey1);
		assertEquals(blobKey1, blobInfoByKey1.getBlobKey());

		// NOTE:: local implements does not support the specify content type.
		// assertEquals("text/plain", blobInfoByKey1.getContentType());
		assertEquals(5, blobInfoByKey1.getContentLength());
		assertNotNull(blobInfoByKey1.getLastModified());

//		assertEquals(blobInfoByKey1.getMetadata().get("author"), "test");
//		assertEquals(blobInfoByKey1.getMetadata().get("price"), "99.0");

		// test get blob content
		assertEquals("hello", readContent(blobInfoByKey1));

		// test get blob content partial
		assertEquals("ll", readContent(blobInfoByKey1,2,2));

		// test overwrite blob content
		writeContent(blobstore, blobKey1, "woo");
		BlobInfo blobByKey2 = blobstore.get(blobKey1);
		assertEquals("woo", readContent(blobByKey2));

		// test get none-exists blob
		BlobKey blobKeyNoneExists = new BlobKey(bucketName, "noneExists");
		try{
			blobstore.get(blobKeyNoneExists);
			fail();
		}catch(IOException e){
			// pass
		}

		// test delete
		blobstore.delete(blobKey1);
		try{
			blobstore.get(blobKey1);
			fail();
		}catch(IOException e){
			// pass
		}

		// test delete none-exist key
		blobstore.delete(new BlobKey(bucketName, "noneExists"));

		// put public read and reduced redundancy blob, currently can not verify automatically.
		// NOTE:: the local implements does not support this feature.

//		ByteArrayInputStream in1 = new ByteArrayInputStream("foo".getBytes());
//		byte[] data1 = "bar".getBytes();
//
//		BlobKey blobKey2 = new BlobKey(bucketName, key2);
//		BlobInfo blobInfoByFactory1 = blobInfoFactory.createBlobInfo(blobKey2, 3, "text/plain", in1);
//
//		BlobKey blobKey3 = new BlobKey(bucketName, key3);
//		BlobInfo blobInfoByFactory2 = blobInfoFactory.createBlobInfo(blobKey3, "text/plain", data1);
//
//		blobstore.put(blobInfoByFactory1, true, false);
//		blobstore.put(blobInfoByFactory2, false, true);
//
//		in1.close();
//
//		BlobInfo blobInfoByKey2 = blobstore.get(blobKey2);
//		BlobInfo blobInfoByKey3 = blobstore.get(blobKey3);
//
//		assertEquals("foo", readContent(blobInfoByKey2));
//		assertEquals("bar", readContent(blobInfoByKey3));

		// clean up
		cleanByKey(key2);
		cleanByKey(key3);
	}

	public void testPutWithPublic() {
		//
	}

	public void testGet() {
		//
	}

	public void testDelete() {
		//
	}

	@Test
	public void testList() throws IOException {
		// check bucket first
		if (!blobstore.existBucket(bucketName)){
			blobstore.createBucket(bucketName);
		}

		BlobKey prefixAll = new BlobKey(bucketName, null);

		// delete exist blobs
		BlobInfoPartialCollection blobs = blobstore.list(prefixAll);
		if (blobs.size() > 0){
			for(BlobInfo blob : blobs){
				blobstore.delete(blob.getBlobKey());
			}
		}

		// test put blob
		writeContent(blobstore, new BlobKey(bucketName, "a"), "a");
		writeContent(blobstore, new BlobKey(bucketName, "r-j001"), "j001");
		writeContent(blobstore, new BlobKey(bucketName, "r-j002"), "j002");
		writeContent(blobstore, new BlobKey(bucketName, "r-c-c001"), "c001");
		writeContent(blobstore, new BlobKey(bucketName, "r"), "r");

		// test list
		BlobInfoPartialCollection blobsByNoPrefix1 = blobstore.list(prefixAll);
		assertEquals(5, blobsByNoPrefix1.size());
		assertFalse(blobsByNoPrefix1.hasMore());

		assertContainsKey(blobsByNoPrefix1, "a");
		assertContainsKey(blobsByNoPrefix1, "r-j001");
		assertContainsKey(blobsByNoPrefix1, "r-j002");
		assertContainsKey(blobsByNoPrefix1, "r-c-c001");
		assertContainsKey(blobsByNoPrefix1, "r");

		BlobInfoPartialCollection blobsByPrefix1 = blobstore.list(new BlobKey(bucketName, "r-"));
		assertEquals(3, blobsByPrefix1.size());
		assertContainsKey(blobsByPrefix1, "r-c-c001");
		assertContainsKey(blobsByPrefix1, "r-j001");
		assertContainsKey(blobsByPrefix1, "r-j002");

		BlobInfoPartialCollection blobsByPrefix2 = blobstore.list(new BlobKey(bucketName, "r-c-"));
		assertEquals(1, blobsByPrefix2.size());
		assertContainsKey(blobsByPrefix2, "r-c-c001");

		// delete all blobs
		BlobInfoPartialCollection blobsByNoPrefix2 = blobstore.list(prefixAll);
		for(BlobInfo blob : blobsByNoPrefix2){
			blobstore.delete(blob.getBlobKey());
		}

		BlobInfoPartialCollection blobsByRemove1 = blobstore.list(prefixAll);
		assertEquals(0, blobsByRemove1.size());
	}

	public void testListNext() {
		//
	}

	private void writeContent(
			Blobstore blobstoreService,
			BlobKey blobKey,
			String content) throws IOException{
		writeContent(blobstoreService, blobKey, content, null);
	}

	private void writeContent(
			Blobstore blobstore,
			BlobKey blobKey,
			String content,
			Map<String, String> metadata) throws IOException{
		byte[] data = content.getBytes();
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		BlobInfo blobInfo = blobInfoFactory.createBlobInfo(blobKey, data.length, "text/plain", in);

		if (metadata != null){
			for(String key : metadata.keySet()){
				blobInfo.addMetadata(key, metadata.get(key));
			}
		}
		blobstore.put(blobInfo);
		IOUtils.closeQuietly(in);
	}

	private String readContent(
			BlobInfo blob) throws IOException{
		ResourceContent resourceContent = blob.getContentSnapshot();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(resourceContent.getInputStream()));
		String content = reader.readLine();
		reader.close();
		resourceContent.close();
		return content;
	}

	private String readContent(
			BlobInfo blob, long start, long length) throws IOException{
		ResourceContent resourceContent = blob.getContentSnapshot(start, length);
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(resourceContent.getInputStream()));
		String content = reader.readLine();
		reader.close();
		resourceContent.close();
		return content;
	}

	private void cleanByKey(String key){
		BlobKey blobKey = new BlobKey(bucketName, key);
		try{
			//BlobInfo blob1 = blobstore.get(blobKey);
			blobstore.delete(blobKey);
		}catch(IOException e){
			// ignore
		}
	}

	private void assertContainsKey(BlobInfoPartialCollection collection, String key) {
		boolean found = false;
		for (BlobInfo info : collection) {
			if (info.getBlobKey().getKey().equals(key)){
				found = true;
				break;
			}
		}

		assertTrue(found);
	}

}
