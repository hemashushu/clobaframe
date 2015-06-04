package org.archboy.clobaframe.webresource.local;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.file.impl.DefaultFileBaseResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceInfo;

public class LocalWebResourceInfo extends DefaultFileBaseResourceInfo implements WebResourceInfo {

	private String name;

	public LocalWebResourceInfo(
			File file, String mimeType, String name) {
		super(file, mimeType);
		this.name = name;
	}

	@Override
	public String getContentHash() {
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
	public String getName() {
		return name;
	}
}
