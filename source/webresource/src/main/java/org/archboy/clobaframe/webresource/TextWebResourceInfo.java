package org.archboy.clobaframe.webresource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;
import org.apache.commons.codec.digest.DigestUtils;
import org.archboy.clobaframe.io.TextResourceInfo;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
public class TextWebResourceInfo extends AbstractWebResourceInfo implements TextResourceInfo {
	
	private String name;
	private String mimeType;
	private Date lastModified;
	private Charset charset;
	private byte[] content;

	public TextWebResourceInfo(String name, String text, Charset charset, String mimeType, Date lastModified) {
		Assert.hasText(name);
		Assert.hasText(text);
		Assert.notNull(charset);
		Assert.hasText(mimeType);
		Assert.notNull(lastModified);
		
		this.name = name;
		this.mimeType = mimeType;
		this.charset = charset;
		
		updateContent(text, lastModified);
	}
	
	public TextWebResourceInfo(String name, String mimeType, String text){
		this(name, text, Charset.forName("UTF-8"), mimeType, new Date());
	}

	@Override
	public long getContentLength() {
		return content.length;
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public InputStream getContent() throws IOException {
		return new ByteArrayInputStream(content);
	}

	@Override
	public InputStream getContent(long start, long length) throws IOException {
		return new ByteArrayInputStream(
				content, (int)start, (int)length);
	}

	@Override
	public boolean isSeekable() {
		return true;
	}

	@Override
	public Date getLastModified() {
		return lastModified;
	}
	
	@Override
	public void updateContent(String text, Date lastModified){
		this.content = text.getBytes(charset);
		this.lastModified = lastModified;
	}
	
	@Override
	public String getContentHash() {
		return DigestUtils.sha256Hex(content);
	}

	@Override
	public String getName() {
		return name;
	}
	
}
