package org.archboy.clobaframe.webresource.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.file.FileBaseResourceInfo;
import org.archboy.clobaframe.io.file.impl.PartialFileInputStream;
import org.archboy.clobaframe.webresource.WebResourceInfo;

public class LocalWebResourceInfo implements WebResourceInfo, FileBaseResourceInfo {

	private File file;
	private String name;
	private String mimeType;
	
	//private long lastModified;
	//private String contentHash;

	public LocalWebResourceInfo(
			File file, String name, String mimeType) {
		this.file = file;
		this.name = name;
		this.mimeType = mimeType;
	}

	@Override
	public String getContentHash() {
		// only re-compute content hash when the File.lastModified changed.
		//if (contentHash == null || lastModified != file.lastModified()) {
		//	return computeContentHash();
		//}else{
		//	return contentHash;
		//}
	//}
	//
	//private String computeContentHash(){
		
		String hash = null;
		InputStream in = null;
		try {
			in = getContent();
			hash = DigestUtils.sha256Hex(in);
		}catch(IOException e){
			// ignore
		}finally{
			IOUtils.closeQuietly(in);
		}
		return hash;
	}
	
	@Override
	public long getContentLength() {
		// get the length just in time.
		return file.length();
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public InputStream getContent() throws IOException {
		return new FileInputStream(file);
	}

	@Override
	public InputStream getContent(long start, long length) throws IOException {
		return new PartialFileInputStream(file, start, length);
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
		return new Date(file.lastModified());
	}

	@Override
	public File getFile() {
		return file;
	}
	
}