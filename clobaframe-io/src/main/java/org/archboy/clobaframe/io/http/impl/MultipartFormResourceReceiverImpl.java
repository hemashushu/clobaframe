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
package org.archboy.clobaframe.io.http.impl;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import javax.inject.Named;
import org.archboy.clobaframe.io.TemporaryResources;
import org.springframework.util.Assert;
import org.archboy.clobaframe.io.http.MultipartFormResourceReceiver;
import org.archboy.clobaframe.io.http.MultipartFormResourceInfo;

/**
 *
 * @author young
 */
@Named
public class MultipartFormResourceReceiverImpl implements MultipartFormResourceReceiver {

	// default 10MByte
	private static final long DEFAULT_MAX_UPLOAD_SIZE = 10L * 1024 * 1024;

	private long maxUploadSize = DEFAULT_MAX_UPLOAD_SIZE;

//	@Inject
//	private TemporaryResourcesAutoCleaner temporaryFileCleanner;

	@Value("${io.maxUploadSize}")
	public void setMaxUploadSizeKB(int maxUploadSizeKB) {
		this.maxUploadSize = maxUploadSizeKB * 1024L;
	}

	@Override
	public List<MultipartFormResourceInfo> receive(HttpServletRequest request,
		TemporaryResources temporaryResources) throws IOException {
		return receive(request, temporaryResources, maxUploadSize);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MultipartFormResourceInfo> receive(HttpServletRequest request,
		TemporaryResources temporaryResources,
		long maxUploadSize) throws IOException {

		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		Assert.isTrue(isMultipart);

		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();
		
		/** the DiskFileItemFactory object hold the cleaning tracker object.
		 * 
		 * It's seems the temporary file cleaner doesn't works here,
		 * so use the TemporaryResource to delete temp file manually.
		 * 
		 */
		//factory.setFileCleaningTracker(temporaryFileCleanner.getFileCleaningTracker());
		
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(maxUploadSize);

		List<FileItem> items = null;
		try {
			items = (List<FileItem>)upload.parseRequest(request);
		} catch (FileUploadException ex) {
			throw new IOException(ex);
		}

		List<MultipartFormResourceInfo> resources = new ArrayList<MultipartFormResourceInfo>();
		for (final FileItem item : items){
			temporaryResources.addResource(new Closeable() {
				@Override
				public void close() throws IOException {
					item.delete();
				}
			});
			
			resources.add(new DefaultMultipartFormResourceInfo(item));
		}
		return resources;
	}

}