package org.archboy.clobaframe.webresource.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.webresource.AbstractWebResourceInfo;
import org.archboy.clobaframe.webresource.CompressableResource;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.springframework.util.Assert;

/**
 * Compressed resource.
 *
 * @author yang
 */
public class CompressWebResourceInfo extends AbstractWebResourceInfo implements CompressableResource {

	private WebResourceInfo webResourceInfo;
	private String lastContentHash;
	private byte[] content;


	public CompressWebResourceInfo(WebResourceInfo webResourceInfo) throws IOException{
		Assert.notNull(webResourceInfo);
		this.webResourceInfo = webResourceInfo;
		addUnderlayWebResource(webResourceInfo);
		rebuild();
	}

	@Override
	public String getContentHash() {
		return webResourceInfo.getContentHash();
	}

	@Override
	public long getContentLength() {
		try{
			rebuild();
		}catch(IOException e){
			// ignore
		}
		return content.length;
	}

	@Override
	public String getMimeType() {
		return webResourceInfo.getMimeType();
	}

	@Override
	public String getName() {
		return webResourceInfo.getName();
	}

	@Override
	public Date getLastModified() {
		return webResourceInfo.getLastModified();
	}

	@Override
	public InputStream getContent() throws IOException {
		rebuild();
		return new ByteArrayInputStream(content);
	}

	@Override
	public InputStream getContent(long start, long length) throws IOException {
		rebuild();
		return new ByteArrayInputStream(
				content, (int)start, (int)length);
	}

	@Override
	public boolean isSeekable() {
		return true;
	}

	private void rebuild() throws IOException {

		if (webResourceInfo.getContentHash().equals(lastContentHash)){
			// resource does not changed
			return;
		}

		this.lastContentHash = webResourceInfo.getContentHash();

		InputStream in = null;
		ByteArrayOutputStream out = null;

		try{
			in = webResourceInfo.getContent();
			out = new ByteArrayOutputStream();
			GZIPOutputStream gzipOut = new GZIPOutputStream(out);
			IOUtils.copy(in, out);
			gzipOut.close();
		} finally{
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}

		this.content = out.toByteArray();
	}

	@Override
	public String getCompressAlgorithm() {
		return "gzip";
	}

}
