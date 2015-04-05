package org.archboy.clobaframe.blobstore.local;

import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;
import org.archboy.clobaframe.blobstore.BlobResourceInfoFactory;
import org.archboy.clobaframe.blobstore.BlobResourceRepository;
import org.archboy.clobaframe.blobstore.Blobstore;
import org.archboy.clobaframe.blobstore.PartialCollection;
import org.archboy.clobaframe.blobstore.impl.DefaultBlobResourceInfoFactory;
import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class LocalBlobstoreTest {

	@Inject
	@Named("defaultBlobstore")
	private Blobstore blobstore;

	private BlobResourceInfoFactory blobResourceInfoFactory = new DefaultBlobResourceInfoFactory();

	private String testRepositoryName = "test-clobaframe-repository";

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
		if (blobstore.exist(testRepositoryName)){

			BlobResourceRepository repository = blobstore.getRepository(testRepositoryName);
			
			// clean all blobs.
			PartialCollection<BlobResourceInfo> infos = repository.list();
			
			for (BlobResourceInfo info : infos) {
				repository.delete(info.getKey());
			}

			// delete bucket.
			blobstore.delete(testRepositoryName);
			assertFalse(blobstore.exist(testRepositoryName));
		}

		// test make
		blobstore.create(testRepositoryName);

		// test exist
		assertTrue(blobstore.exist(testRepositoryName));
		assertNotNull(blobstore.getRepository(testRepositoryName));
		
		// test exist
		assertFalse(blobstore.exist("none-exists"));
		assertNull(blobstore.getRepository("none-exists"));
		
		// test make duplicate name bucket
		blobstore.create(testRepositoryName);

		// test delete
		blobstore.delete(testRepositoryName);
		
		assertFalse(blobstore.exist(testRepositoryName));
		assertNull(blobstore.getRepository(testRepositoryName));

		// test delete none exists
		blobstore.delete("none-exists");
	}

	public void testDeleteBucket() {
		//
	}

}
