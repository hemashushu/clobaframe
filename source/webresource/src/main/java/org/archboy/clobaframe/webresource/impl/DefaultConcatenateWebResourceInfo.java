package org.archboy.clobaframe.webresource.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.webresource.AbstractWebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceInfo;

/**
 *
 * @author yang
 */
public class DefaultConcatenateWebResourceInfo extends AbstractWebResourceInfo {

	private List<WebResourceInfo> webResourceInfos;
	private String name;
	private String mimeType;

	public DefaultConcatenateWebResourceInfo(
			List<WebResourceInfo> webResourceInfos, String name) {
		this.webResourceInfos = webResourceInfos;
		this.name = name;
		
		// get the first resource mime type as the composite resource mime type.
		this.mimeType = webResourceInfos.get(0).getMimeType();
		
		for(WebResourceInfo info : webResourceInfos) {
			addType(DefaultConcatenateWebResourceInfo.class, info);
		}
	}

	@Override
	public String getContentHash() {
		// only hash all source hash values, not hash the source content itself, to increase compute speed.
		StringBuilder builder = new StringBuilder(webResourceInfos.size());
		for (WebResourceInfo webResourceInfo : webResourceInfos) {
			builder.append(webResourceInfo.getContentHash());
		}

		return DigestUtils.sha256Hex(builder.toString());
	}

	@Override
	public long getContentLength() {
		long length = 0;
		for(WebResourceInfo resourceInfo : webResourceInfos){
			length += resourceInfo.getContentLength();
		}
		
		// because of adding the '\n' symbol between each resource, so add the (size - 1).
		return length + webResourceInfos.size() - 1; 
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public InputStream getContent() throws IOException {
		byte[] data = getConcatenateData();
		return new ByteArrayInputStream(data);
	}

	@Override
	public InputStream getContent(long start, long length) throws IOException {
		byte[] data = getConcatenateData();
		return new ByteArrayInputStream(data, (int)start, (int)length);
	}

	@Override
	public boolean isSeekable() {
		return true;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Date getLastModified() {
		// get the last modified time of all resources.
		long lastModified = 0;
		
		for(WebResourceInfo resourceInfo : webResourceInfos){
			Date date = resourceInfo.getLastModified();
			long time = date.getTime();
			if (time > lastModified) {
				lastModified = time;
			}
		}
		return new Date(lastModified);
	}

	private byte[] getConcatenateData() throws IOException {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		for(int idx=0; idx<webResourceInfos.size(); idx++){
			WebResourceInfo resourceInfo = webResourceInfos.get(idx);
			InputStream in = resourceInfo.getContent();
			IOUtils.copy(in, out);
			in.close();
			
			// append new line between resources.
			if (idx < webResourceInfos.size() -1){
				out.write('\n');
			}
		}
		
		byte[] data = out.toByteArray();
		out.close();
		
		return data;
	}
}
