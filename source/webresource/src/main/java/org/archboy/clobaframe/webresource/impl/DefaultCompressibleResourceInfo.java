package org.archboy.clobaframe.webresource.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.NamedResourceInfo;
import org.archboy.clobaframe.webresource.AbstractWrapperResourceInfo;
import org.archboy.clobaframe.webresource.CompressibleResourceInfo;
import org.archboy.clobaframe.webresource.ContentHashResourceInfo;
import org.springframework.util.Assert;

/**
 * Compressed resource.
 *
 * @author yang
 */
public class DefaultCompressibleResourceInfo extends AbstractWrapperResourceInfo implements CompressibleResourceInfo {

	private NamedResourceInfo resourceInfo;
	//private String lastContentHash;
	private byte[] content;

	public DefaultCompressibleResourceInfo(NamedResourceInfo resourceInfo) {
		Assert.notNull(resourceInfo);
		Assert.isInstanceOf(ContentHashResourceInfo.class, resourceInfo);
		
		this.resourceInfo = resourceInfo;
		
		appendType(getType(), resourceInfo);
		rebuild();
	}

	@Override
	public int getType() {
		return TYPE_COMPRESS;
	}

	@Override
	public String getContentHash() {
		// return the upstream content hash, because the actually content does not changed.
		return ((ContentHashResourceInfo)resourceInfo).getContentHash();
	}

	@Override
	public long getContentLength() {
		rebuild();
		return content.length;
	}

	@Override
	public String getMimeType() {
		return resourceInfo.getMimeType();
	}

	@Override
	public String getName() {
		return resourceInfo.getName();
	}

	@Override
	public Date getLastModified() {
		return resourceInfo.getLastModified();
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

	private void rebuild() {
//		if (webResourceInfo.getContentHash().equals(lastContentHash)){
//			// resource does not changed
//			return;
//		}
//
//		this.lastContentHash = webResourceInfo.getContentHash();

		InputStream in = null;
		ByteArrayOutputStream out = null;

		try{
			in = resourceInfo.getContent();
			out = new ByteArrayOutputStream();
			GZIPOutputStream gzipOut = new GZIPOutputStream(out);
			IOUtils.copy(in, gzipOut);
			gzipOut.close();
		} catch (IOException ex) {
			// ignore
		} finally{
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}

		this.content = out.toByteArray();
	}

	@Override
	public String getAlgorithm() {
		return "gzip";
	}

}
