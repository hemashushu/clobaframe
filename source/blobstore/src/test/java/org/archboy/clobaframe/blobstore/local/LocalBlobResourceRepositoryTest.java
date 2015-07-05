package org.archboy.clobaframe.blobstore.local;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;
import org.archboy.clobaframe.blobstore.BlobResourceInfoFactory;
import org.archboy.clobaframe.blobstore.BlobResourceRepository;
import org.archboy.clobaframe.blobstore.Blobstore;
import org.archboy.clobaframe.blobstore.BlobstoreManager;
import org.archboy.clobaframe.blobstore.PartialCollection;
import org.archboy.clobaframe.blobstore.impl.DefaultBlobResourceInfoFactory;
import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class LocalBlobResourceRepositoryTest {

	@Inject
	private BlobstoreManager blobstoreManager;
	
	private BlobResourceInfoFactory blobResourceInfoFactory = new DefaultBlobResourceInfoFactory();

	private String testRepositoryName1 = "test-clobaframe-repository1";
	private String testRepositoryName2 = "test-clobaframe-repository2";

	@Before
	public void setUp() throws Exception {
		//
	}

	@After
	public void tearDown() throws Exception {
		//
	}

	public void testExistBucket() {
		//
	}

	public void testGetName() {
		//
	}

	@Test
	public void testPut() throws IOException {
		Blobstore blobstore = blobstoreManager.getDefault();
		
		// check bucket first
		if (!blobstore.exist(testRepositoryName1)){
			blobstore.create(testRepositoryName1);
		}

		BlobResourceRepository repository = blobstore.getRepository(testRepositoryName1);
		assertEquals(testRepositoryName1, repository.getName());
		
		String key1 = "b001";
		
		// clean first
		repository.delete(key1);

		// test put blob
		Calendar calendar = Calendar.getInstance();
		Date now1 = calendar.getTime();
		writeContent(repository, key1, "hello", "text/plain"); //, now);

		// test get blob by key
		BlobResourceInfo blobResourceInfo1 = repository.get(key1);
		assertEquals(testRepositoryName1, blobResourceInfo1.getRepositoryName());
		
		assertEquals(5, blobResourceInfo1.getContentLength());
		assertEquals(key1, blobResourceInfo1.getKey());
		assertDateEquals(now1, blobResourceInfo1.getLastModified());
		assertNull(blobResourceInfo1.getMetadata());
		assertEquals("text/plain", blobResourceInfo1.getMimeType());

		// test get blob content
		assertEquals("hello", readContent(blobResourceInfo1));

		// test get blob content partial
		assertEquals("ll", readContent(blobResourceInfo1,2,2));

		// test overwrite blob content and append meta data
//		calendar.add(Calendar.MINUTE, -10);
//		Date tenMinsAgo = calendar.getTime();
		Date now2 = calendar.getTime();
		
		calendar.add(Calendar.HOUR_OF_DAY, -1);
		Date oneHourAgo = calendar.getTime();
		
		Map<String, Object> meta1 = new HashMap<String, Object>();
		meta1.put("name", "foo");
		meta1.put("amount", 100);
		meta1.put("price", 99.1D);
		meta1.put("update", oneHourAgo);
		
		writeContent(repository, key1, "<html></html>", "text/html", meta1); //tenMinsAgo, meta1);
		
		// check update
		BlobResourceInfo blobResourceInfo2 = repository.get(key1);
		assertDateEquals(now2, blobResourceInfo2.getLastModified());
		assertEquals("text/html", blobResourceInfo2.getMimeType());
		assertEquals("<html></html>", readContent(blobResourceInfo2));

		// check meta data
		Map<String, Object> metaByBlob1 = blobResourceInfo2.getMetadata();
		assertEquals("foo", metaByBlob1.get("name"));
		assertEquals(100, metaByBlob1.get("amount"));
		assertEquals(99.1D, metaByBlob1.get("price"));
		assertDateEquals(oneHourAgo, new Date((Long)metaByBlob1.get("update")));
		
		// test get none-exists blob
		assertNull(repository.get("none-exists"));

		// test delete
		repository.delete(key1);
		assertNull(repository.get(key1));

		// test delete none-exist key
		repository.delete("none-exists");
	}

	public void testPutWithParams() {
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
		Blobstore blobstore = blobstoreManager.getDefault();
		
		// check bucket first
		if (!blobstore.exist(testRepositoryName2)){
			blobstore.create(testRepositoryName2);
		}
		
		// clean all
		BlobResourceRepository repository = blobstore.getRepository(testRepositoryName2);
		for(BlobResourceInfo info : repository.list()){
			repository.delete(info.getKey());
		}
		
		String key1 = "i001";
		String key2 = "i002";
		String key3 = "i003";
		
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		
		// test put blob
		writeContent(repository, key1, "body {}", "text/css"); //, now);
		writeContent(repository, key2, "div {}", "text/css"); //, now);
		writeContent(repository, key3, "p {}", "text/css"); //, now);

		// test list
		PartialCollection<BlobResourceInfo> blobs1 = repository.list();
		assertEquals(3, blobs1.size());
		assertFalse(blobs1.hasMore());

		assertContainsKey(blobs1, key1);
		assertContainsKey(blobs1, key2);
		assertContainsKey(blobs1, key3);
	}

	public void testListNext() {
		//
	}

	private void writeContent(
			BlobResourceRepository repository,
			String key,
			String content,
			String mimeType //,
			//Date lastModified
			) throws IOException{
		writeContent(repository, key, content, mimeType, null); // lastModified, null);
	}

	private void writeContent(
			BlobResourceRepository repository,
			String key,
			String content,
			String mimeType,
			//Date lastModified,
			Map<String, Object> metadata) throws IOException{
		
		byte[] data = content.getBytes();
		BlobResourceInfo blobResourceInfo = blobResourceInfoFactory.make(
				testRepositoryName1, key, data, mimeType, null, metadata);
		repository.put(blobResourceInfo);
	}

	private String readContent(
			BlobResourceInfo blob) throws IOException{
		InputStream in = blob.getContent();
		String content = IOUtils.toString(in);
		in.close();
		return content;
	}

	private String readContent(
			BlobResourceInfo blob, long start, long length) throws IOException{
		InputStream in = blob.getContent(start, length);
		String content = IOUtils.toString(in);
		in.close();
		return content;
	}

	
	private void assertContainsKey(PartialCollection<BlobResourceInfo> infos, String key) {
		boolean found = false;
		for (BlobResourceInfo info : infos) {
			if (info.getKey().equals(key)){
				found = true;
				break;
			}
		}

		assertTrue(found);
	}

	private static void assertDateEquals(Date expected, Date actual){
		if (expected == null && actual == null){
			//
		}else if(expected == null || actual == null){
			fail("date not equals");
		}else{
			assertTrue(String.format("expected %s, but actual is %s.", expected, actual),
					Math.abs(expected.getTime() - actual.getTime()) < 1000);
		}
	}
}
