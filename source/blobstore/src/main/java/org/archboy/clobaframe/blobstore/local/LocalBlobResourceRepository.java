package org.archboy.clobaframe.blobstore.local;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;
import org.archboy.clobaframe.blobstore.PartialCollection;
import org.archboy.clobaframe.blobstore.impl.AbstractBlobResourceRepository;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
public class LocalBlobResourceRepository extends AbstractBlobResourceRepository implements Closeable{

	private String name; // repository name
	private File repoDir; // repository root dir
	
	private DB db;
	private BTreeMap<String, ResourceAttributes> table;
	
	private static final String tableName = "meta";
	
	// serializer
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private final TypeReference<Map<String, Object>> metaDataTypeReference = 
			new TypeReference<Map<String, Object>>() {};
	
	public LocalBlobResourceRepository(String name, File indexFile,  File repoDir) {
		this.name = name;
		this.repoDir = repoDir;
		
		//Serializer<ResourceAttributes> serializer = new ResourceAttributesSerializer();
		
		db = DBMaker.newFileDB(indexFile)
           //.closeOnJvmShutdown()
           .make();
		
//		table = db.createTreeMap(indexName)
//				.valueSerializer(serializer)
//				.makeOrGet();
		
		table = db.getTreeMap(tableName);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void close() throws IOException {
		// close meta db
		db.close();
	}
	
	@Override
	public void put(BlobResourceInfo blobResourceInfo, boolean publicReadable, int priority) throws IOException {
		Assert.notNull(blobResourceInfo);

		// NOTE:: the local blob resource repository does not supports the 
		// read permission and priority
		
		String key = blobResourceInfo.getKey();
		File file = new File(repoDir, key);

		// copy content
		InputStream in = blobResourceInfo.getContent();
		FileOutputStream out = new FileOutputStream(file);
		IOUtils.copy(in, out);

		IOUtils.closeQuietly(out);
		IOUtils.closeQuietly(in);
		
		Date now = new Date();
		
		// write meta data
		ResourceAttributes attributes = new ResourceAttributes();
		attributes.setLastModified(now); //blobResourceInfo.getLastModified());
		attributes.setMimeType(blobResourceInfo.getMimeType());
		
		Map<String, Object> metaData = blobResourceInfo.getMetadata();
		if (metaData != null && !metaData.isEmpty()){
			String metas = objectMapper.writeValueAsString(metaData);
			attributes.setMetas(metas);
		}
		
		table.put(key, attributes);
		db.commit();
	}

	@Override
	public BlobResourceInfo get(String key) {
		Assert.notNull(key);

		ResourceAttributes attributes = table.get(key);
		return getBlobResourceInfo(key, attributes);
	}

	@Override
	public void delete(String key) throws IOException {
		Assert.notNull(key);
		
		File file = new File(repoDir, key);

		if (!file.exists()){
			return;
		}

		boolean result = file.delete();
		if (!result) {
			throw new IOException("Delete blob failed.");
		}
	}

	@Override
	public PartialCollection<BlobResourceInfo> list() {
		List<BlobResourceInfo> infos = new ArrayList<BlobResourceInfo>();
		
		NavigableSet<String> keys = table.keySet();
		for(String key : keys){
			ResourceAttributes attributes = table.get(key);
			BlobResourceInfo info = getBlobResourceInfo(key, attributes);
			if (info != null) {
				infos.add(info);
			}
		}
		
		PartialArrayList<BlobResourceInfo> pinfos = new PartialArrayList<BlobResourceInfo>(infos , false);
		return pinfos;
	}

	@Override
	public PartialCollection<BlobResourceInfo> listNext(PartialCollection<BlobResourceInfo> prevCollection) {
		if (!prevCollection.hasMore()) {
			throw new IllegalArgumentException("There is no more items.");
		}
		
		// the local blob resource repository does not support the partial list.
		
		return new PartialArrayList<BlobResourceInfo>(
				new ArrayList<BlobResourceInfo>(), false);
	}
	
	private BlobResourceInfo getBlobResourceInfo(String key, ResourceAttributes attributes) {
		File file = new File(repoDir, key);
		if (!file.exists() || file.isDirectory()) {
			return null;
		}
		
		Map<String, Object> metaData = null;
		if (attributes.getMetas() != null && StringUtils.isNotEmpty(attributes.getMetas())){
			try{
				metaData = objectMapper.readValue(attributes.getMetas(), metaDataTypeReference);
			}catch(IOException e){
				// ignore
			}
		}
		
		return new LocalBlobResourceInfo(name, key, file,
				attributes.getMimeType(),
				attributes.getLastModified(), 
				metaData);
	}
}
