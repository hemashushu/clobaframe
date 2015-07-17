package org.archboy.clobaframe.resource.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.NamedResourceInfo;
import org.archboy.clobaframe.resource.AbstractWrapperResourceInfo;
import org.archboy.clobaframe.resource.ContentHashResourceInfo;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
public class DefaultConcatenateResourceInfo extends AbstractWrapperResourceInfo {

	private List<NamedResourceInfo> resourceInfos;
	private String name;
	private String mimeType;

	public DefaultConcatenateResourceInfo(
			List<NamedResourceInfo> resourceInfos, String name) {
		Assert.notNull(resourceInfos);
		Assert.hasText(name);
		for(NamedResourceInfo resourceInfo : resourceInfos){
			Assert.isInstanceOf(ContentHashResourceInfo.class, resourceInfo);
		}
		
		this.resourceInfos = resourceInfos;
		this.name = name;
		
		// get the first resource mime type as the composite resource mime type.
		this.mimeType = resourceInfos.get(0).getMimeType();
		
		for(NamedResourceInfo info : resourceInfos) {
			appendType(getType(), info);
		}
	}

	@Override
	public int getType() {
		return TYPE_CONCATENATE;
	}
	
	@Override
	public String getContentHash() {
		// only hash all source hash values, not hash the source content itself, to increase the compute performance.
		StringBuilder builder = new StringBuilder(resourceInfos.size());
		for (NamedResourceInfo resourceInfo : resourceInfos) {
			builder.append(((ContentHashResourceInfo)resourceInfo).getContentHash());
		}

		return DigestUtils.sha256Hex(builder.toString());
	}

	@Override
	public long getContentLength() {
		long length = 0;
		for(NamedResourceInfo resourceInfo : resourceInfos){
			length += resourceInfo.getContentLength();
		}
		
		// because of adding the '\n' symbol between each resource, so add the (size - 1).
		return length + resourceInfos.size() - 1; 
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
		
		for(NamedResourceInfo resourceInfo : resourceInfos){
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
		
		for(int idx=0; idx<resourceInfos.size(); idx++){
			NamedResourceInfo resourceInfo = resourceInfos.get(idx);
			InputStream in = resourceInfo.getContent();
			IOUtils.copy(in, out);
			in.close();
			
			// append new line between resources.
			if (idx < resourceInfos.size() -1){
				out.write('\n');
			}
		}
		
		byte[] data = out.toByteArray();
		out.close();
		
		return data;
	}
}
