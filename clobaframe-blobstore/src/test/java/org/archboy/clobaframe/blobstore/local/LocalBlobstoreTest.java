package org.archboy.clobaframe.blobstore.local;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;
import org.archboy.clobaframe.blobstore.BlobResourceInfoFactory;
import org.archboy.clobaframe.blobstore.BlobResourceInfoPartialCollection;
import org.archboy.clobaframe.blobstore.BlobKey;
import org.archboy.clobaframe.blobstore.Blobstore;
import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class LocalBlobstoreTest {

	@Inject
	private Blobstore blobstore;

//	@Inject
//	private BlobstoreBucket blobstoreBucket;

	@Inject
	private BlobResourceInfoFactory blobResourceInfoFactory;

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
			BlobResourceInfoPartialCollection collection = blobstore.list(new BlobKey(bucketName, null));
			for (BlobResourceInfo info : collection) {
				blobstore.delete(info.getBlobKey());
			}

			// delete bucket.
			blobstore.deleteBucket(bucketName);
			assertFalse(blobstore.existBucket(bucketName));
			return;
		}

		// test make
		blobstore.createBucket(bucketName);

		// test exist
		assertTrue(blobstore.existBucket(bucketName));

		// test exist
		assertFalse(blobstore.existBucket("noneExists"));

		// test make duplicate name bucket
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
		BlobResourceInfo blobByKey1 = blobstore.get(blobKey1);
		assertEquals(blobKey1, blobByKey1.getBlobKey());

		// NOTE:: local implements does not support the specify content type.
		// assertEquals("text/plain", blobInfoByKey1.getContentType());
		assertEquals(5, blobByKey1.getContentLength());
		assertNotNull(blobByKey1.getLastModified());

//		assertEquals(blobInfoByKey1.getMetadata().get("author"), "test");
//		assertEquals(blobInfoByKey1.getMetadata().get("price"), "99.0");

		// test get blob content
		assertEquals("hello", readContent(blobByKey1));

		// test get blob content partial
		assertEquals("ll", readContent(blobByKey1,2,2));

		// test overwrite blob content
		writeContent(blobstore, blobKey1, "woo");
		BlobResourceInfo overwriteBlobByKey2 = blobstore.get(blobKey1);
		assertEquals("woo", readContent(overwriteBlobByKey2));

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
//		BlobResourceInfo blobInfoByFactory1 = blobInfoFactory.make(blobKey2, 3, "text/plain", in1);
//
//		BlobKey blobKey3 = new BlobKey(bucketName, key3);
//		BlobResourceInfo blobInfoByFactory2 = blobInfoFactory.make(blobKey3, "text/plain", data1);
//
//		blobstore.put(blobInfoByFactory1, true, false);
//		blobstore.put(blobInfoByFactory2, false, true);
//
//		in1.close();
//
//		BlobResourceInfo blobInfoByKey2 = blobstore.get(blobKey2);
//		BlobResourceInfo blobInfoByKey3 = blobstore.get(blobKey3);
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
		BlobResourceInfoPartialCollection blobs = blobstore.list(prefixAll);
		if (blobs.size() > 0){
			for(BlobResourceInfo blob : blobs){
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
		BlobResourceInfoPartialCollection blobsByNoPrefix1 = blobstore.list(prefixAll);
		assertEquals(5, blobsByNoPrefix1.size());
		assertFalse(blobsByNoPrefix1.hasMore());

		assertContainsKey(blobsByNoPrefix1, "a");
		assertContainsKey(blobsByNoPrefix1, "r-j001");
		assertContainsKey(blobsByNoPrefix1, "r-j002");
		assertContainsKey(blobsByNoPrefix1, "r-c-c001");
		assertContainsKey(blobsByNoPrefix1, "r");

		BlobResourceInfoPartialCollection blobsByPrefix1 = blobstore.list(new BlobKey(bucketName, "r-"));
		assertEquals(3, blobsByPrefix1.size());
		assertContainsKey(blobsByPrefix1, "r-c-c001");
		assertContainsKey(blobsByPrefix1, "r-j001");
		assertContainsKey(blobsByPrefix1, "r-j002");

		BlobResourceInfoPartialCollection blobsByPrefix2 = blobstore.list(new BlobKey(bucketName, "r-c-"));
		assertEquals(1, blobsByPrefix2.size());
		assertContainsKey(blobsByPrefix2, "r-c-c001");

		// delete all blobs
		BlobResourceInfoPartialCollection blobsByNoPrefix2 = blobstore.list(prefixAll);
		for(BlobResourceInfo blob : blobsByNoPrefix2){
			blobstore.delete(blob.getBlobKey());
		}

		BlobResourceInfoPartialCollection blobsByRemove1 = blobstore.list(prefixAll);
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
		BlobResourceInfo blobResourceInfo = blobResourceInfoFactory.make(blobKey, "text/plain", in, data.length);

		if (metadata != null){
			for(String key : metadata.keySet()){
				blobResourceInfo.addMetadata(key, metadata.get(key));
			}
		}
		blobstore.put(blobResourceInfo);
		in.close();
	}

	private String readContent(
			BlobResourceInfo blob) throws IOException{
		//ResourceContent resourceContent = blob.getContentSnapshot();
		InputStream in = blob.getInputStream();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(in));
		String content = reader.readLine();
		reader.close();
		in.close();
		return content;
	}

	private String readContent(
			BlobResourceInfo blob, long start, long length) throws IOException{
		//ResourceContent resourceContent = blob.getContentSnapshot(start, length);
		InputStream in = blob.getInputStream(start, length);
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(in));
		String content = reader.readLine();
		reader.close();
		in.close();
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

	private void assertContainsKey(BlobResourceInfoPartialCollection collection, String key) {
		boolean found = false;
		for (BlobResourceInfo info : collection) {
			if (info.getBlobKey().getKey().equals(key)){
				found = true;
				break;
			}
		}

		assertTrue(found);
	}

}
