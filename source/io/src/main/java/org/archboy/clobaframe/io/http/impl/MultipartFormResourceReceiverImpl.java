package org.archboy.clobaframe.io.http.impl;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.archboy.clobaframe.io.TemporaryResources;
import org.archboy.clobaframe.io.http.MultipartFormResourceInfo;
import org.archboy.clobaframe.io.http.MultipartFormResourceReceiver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
@Named
public class MultipartFormResourceReceiverImpl implements MultipartFormResourceReceiver {

	// default 12 MByte
	private static final long DEFAULT_MAX_UPLOAD_SIZE_BYTE = 12L * 1024 * 1024;

	@Value("${clobaframe.io.maxUploadSize:" + DEFAULT_MAX_UPLOAD_SIZE_BYTE + "}")
	private long maxUploadSizeByte;

	public void setMaxUploadSizeByte(long maxUploadSizeByte) {
		this.maxUploadSizeByte = maxUploadSizeByte;
	}
	
	@Override
	public List<MultipartFormResourceInfo> receive(HttpServletRequest request,
		TemporaryResources temporaryResources) throws IOException {
		return receive(request, temporaryResources, maxUploadSizeByte);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MultipartFormResourceInfo> receive(HttpServletRequest request,
		TemporaryResources temporaryResources,
		long maxUploadSizeByte) throws IOException {

		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		Assert.isTrue(isMultipart);

		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();
		//factory.setFileCleaningTracker(temporaryFileCleanner.getFileCleaningTracker());
		
		/*
		 * It's seems the TemporaryFileCleanner doesn't works here,
		 * so use the TemporaryResource to delete temp file manually.
		 */
		
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(maxUploadSizeByte);

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
