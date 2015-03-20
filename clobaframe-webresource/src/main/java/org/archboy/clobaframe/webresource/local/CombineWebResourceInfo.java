package org.archboy.clobaframe.webresource.local;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.file.impl.PartialFileInputStream;
import org.archboy.clobaframe.webresource.WebResourceInfo;

/**
 *
 * @author yang
 */
public class CombineWebResourceInfo implements WebResourceInfo {

	private List<WebResourceInfo> resourceInfos;
	private String name;
	private String uniqueName;
	private String mimeType;

	public CombineWebResourceInfo(
			List<WebResourceInfo> resourceInfos, String name, String mimeType) {
		this.resourceInfos = resourceInfos;
		this.name = name;
		this.mimeType = mimeType;
	}

	@Override
	public String getHash() {
		try{
			byte[] data = getCombineData();
			return DigestUtils.sha256Hex(data);
		}catch(IOException e){
			return null;
		}
	}

	@Override
	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	@Override
	public long getContentLength() {
		// get the length just in time.
		long length = 0;
		for(WebResourceInfo resourceInfo : resourceInfos){
			length += resourceInfo.getContentLength();
		}
		
		return length + resourceInfos.size() - 1; // add the '\n' symbol between each resource.
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public InputStream getContent() throws IOException {
		byte[] data = getCombineData();
		return new ByteArrayInputStream(data);
	}

	@Override
	public InputStream getContent(long start, long length) throws IOException {
		byte[] data = getCombineData();
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
		// get the last modified time just in time.
		long lastModified = 0;
		
		for(WebResourceInfo resourceInfo : resourceInfos){
			Date date = resourceInfo.getLastModified();
			long time = date.getTime();
			if (time > lastModified) {
				lastModified = time;
			}
		}
		return new Date(lastModified);
	}

	private byte[] getCombineData() throws IOException {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		for(int idx=0; idx<resourceInfos.size(); idx++){
			WebResourceInfo resourceInfo = resourceInfos.get(idx);
			InputStream in = resourceInfo.getContent();
			IOUtils.copy(in, out);
			in.close();
			
			// append new line between two resource.
			if (idx < resourceInfos.size() -1){
				out.write('\n');
			}
		}
		
		byte[] data = out.toByteArray();
		out.close();
		
		return data;
	}
}
