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
package org.archboy.clobaframe.webresource.blobstore;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import javax.inject.Named;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;
import org.archboy.clobaframe.blobstore.BlobResourceInfoFactory;
import org.archboy.clobaframe.blobstore.BlobKey;
import org.archboy.clobaframe.blobstore.Blobstore;
import org.archboy.clobaframe.blobstore.PartialCollection;
import org.archboy.clobaframe.webresource.WebResourceInfo;

/**
 *
 * @author young
 */
@Named
public class BlobstoreWebResourceSynchronizer{

	private final Logger logger = LoggerFactory.getLogger(BlobstoreWebResourceSynchronizer.class);

	@Inject
	private Blobstore blobstore;

//	@Inject
//	private BlobstoreBucket blobstoreBucket;

	@Inject
	private BlobResourceInfoFactory blobResourceInfoFactory;

	@Value("${webresource.blobstore.bucketName}")
	private String bucketName;

	@Value("${webresource.blobstore.keyNamePrefix}")
	private String keyNamePrefix;

	@Value("${webresource.blobstore.autoCreateBucket}")
	private boolean autoCreateBucket;

	@Value("${webresource.blobstore.sync}")
	private boolean sync;

	@Value("${webresource.blobstore.deleteNoneExists}")
	private boolean deleteNoneExists;

	private static final int DEFAULT_REMOTE_RESOURCE_MAX_ITEMS = 1000;
	private int remoteResourceMaxItems = DEFAULT_REMOTE_RESOURCE_MAX_ITEMS;

	/**
	 * Update the remote server web resources with local web resources.
	 *
	 * The remote resource named as: key name prefix + unique name.
	 * Because by default the unique name is the SHA256/MD5 hash value + file name part of the resource,
	 * so in the update process, only upload the local resource that unique name
	 * does not exists in the remote repository, and (optional) delete the
	 * remote resource that the unique name does exists in the local repository.
	 *
	 * @param webResourceInfos
	 */
	public void update(Collection<WebResourceInfo> webResourceInfos) throws IOException{

		if (!sync){
			return;
		}

		// put bucket first
		if (autoCreateBucket){
			if (!blobstore.existBucket(bucketName)) {
				blobstore.createBucket(bucketName);

				logger.info("Create bucket [{}] for storing web resources.",
					bucketName);
			}
		}

		// find all web resources that exists in the blobstore
		Map<String, BlobResourceInfo> remoteResources = new HashMap<String, BlobResourceInfo>();

		// build a map for remote resource, use the unique name as map key
		int prefixLength = 0;
		if (StringUtils.isNotEmpty(keyNamePrefix)){
			prefixLength = keyNamePrefix.length();
		}

		for (BlobResourceInfo blobResourceInfo : getRemoteBlobResourceInfos()){
			String uniqueName = blobResourceInfo.getBlobKey().getKey().substring(prefixLength);
			remoteResources.put(uniqueName, blobResourceInfo);
		}

		// build a map for local resource, use the unique name as map key
		Map<String, WebResourceInfo> localResources = new HashMap<String, WebResourceInfo>();
		for (WebResourceInfo webResourceInfo : webResourceInfos) {
			localResources.put(
				webResourceInfo.getUniqueName(),
				webResourceInfo);
		}

		// find the already update-to-date resources
		List<String> unmodifiedKeys = new ArrayList<String>();
		for (String key : localResources.keySet()) {
			if (remoteResources.containsKey(key)) {
				// compare the resources with hash

				//String remoteHash = remoteResources.get(key).getMetadata().get("sha256");
				//String localHash = localResources.get(key).getHash();
				//if (localHash.equals(remoteHash)){
					unmodifiedKeys.add(key);
				//}
			}
		}

		if (!unmodifiedKeys.isEmpty()){
			logger.info("Found {} unmodified remote web resources in bucket [{}].",
					unmodifiedKeys.size(),
					bucketName);
		}

		// delete the keys of unmodified resources, i.e. they doesn't need to be updated.
		for (String key : unmodifiedKeys) {
			localResources.remove(key);
			remoteResources.remove(key);
		}

		// delete none exists resource
		if (deleteNoneExists) {
			for (BlobResourceInfo blobResourceInfo : remoteResources.values()){
				remove(blobResourceInfo.getBlobKey());
			}
		}

		// upload resources
		for(WebResourceInfo webResourceInfo : localResources.values()){
			upload(webResourceInfo);
		}
	}

	private List<BlobResourceInfo> getRemoteBlobResourceInfos() {
		List<BlobResourceInfo> blobResourceInfos = new ArrayList<BlobResourceInfo>();

		BlobKey prefix = new BlobKey(bucketName, keyNamePrefix);
		PartialCollection<BlobResourceInfo> partialBlobResourceInfos = null;
		do {
			partialBlobResourceInfos = blobstore.list(prefix);

			for (BlobResourceInfo blobResourceInfo : partialBlobResourceInfos) {
				blobResourceInfos.add(blobResourceInfo);
			}

			if (blobResourceInfos.size() > remoteResourceMaxItems) {
				break;
			}

		} while (partialBlobResourceInfos.hasMore());

		return blobResourceInfos;
	}

	private void remove(BlobKey blobKey) throws IOException {
		logger.info("Delete remote resource [{}, {}].",
				blobKey.getBucketName(), blobKey.getKey());
		blobstore.delete(blobKey);
	}

	private void upload(WebResourceInfo webResourceInfo) throws IOException {

		BlobKey blobKey = new BlobKey(
				bucketName,
				keyNamePrefix + webResourceInfo.getUniqueName());

		logger.info("Upload web resource [{}] to [{}, {}].", new Object[]{
				webResourceInfo.getName(),
				blobKey.getBucketName(),
				blobKey.getKey()});

		//ResourceContent resourceContent = webResourceInfo.getContentSnapshot();
		InputStream in = webResourceInfo.getInputStream();
		BlobResourceInfo blobResourceInfo = blobResourceInfoFactory.make(
				blobKey,
				webResourceInfo.getContentType(),
				in,
				webResourceInfo.getContentLength());

		// blobInfo.addMetadata("sha256", webResourceInfo.getHash());

		try{
			blobstore.put(blobResourceInfo, true, false);
		}finally{
			IOUtils.closeQuietly(in);
		}
	}
}
