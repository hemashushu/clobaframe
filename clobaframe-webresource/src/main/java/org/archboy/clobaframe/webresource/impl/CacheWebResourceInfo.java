package org.archboy.clobaframe.webresource.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.commons.fileupload.UploadContext;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.webresource.AbstractWebResourceInfo;
import org.archboy.clobaframe.webresource.CacheableResource;
import org.archboy.clobaframe.webresource.CacheableResourceUpdateListener;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.springframework.util.Assert;

public class CacheWebResourceInfo extends AbstractWebResourceInfo implements CacheableResource {

	//private final Logger logger = LoggerFactory.getLogger(CacheWebResourceInfo.class);

	private long cacheMilliSeconds;
	private long lastCheckingTime;
	private WebResourceInfo webResourceInfo;

	private String lastContentHash;
	private Date lastModified;
	private byte[] content;
	
	private List<CacheableResourceUpdateListener> resourceUpdateListeners;
	private Collection<String> referenceResourceNames;


	public CacheWebResourceInfo(WebResourceInfo webResourceInfo, int cacheSeconds) throws IOException {
		Assert.notNull(webResourceInfo);
		this.webResourceInfo = webResourceInfo;
		this.cacheMilliSeconds = cacheSeconds * 1000;
		this.resourceUpdateListeners = new ArrayList<CacheableResourceUpdateListener>();
		
		addUnderlayWebResource(webResourceInfo);
		rebuild();
	}

	@Override
	public InputStream getContent() throws IOException{
		recheck();
		return new ByteArrayInputStream(content);
	}

	@Override
	public InputStream getContent(long start, long length) throws IOException {
		recheck();
		return new ByteArrayInputStream(
				content, (int)start, (int)length);
	}

	@Override
	public boolean isSeekable() {
		return true;
	}

	@Override
	public String getName() {
		return webResourceInfo.getName();
	}

	@Override
	public String getContentHash() {
		try{
			recheck();
		}catch(IOException e){
			// ignore
		}
		return lastContentHash;
	}

	@Override
	public long getContentLength() {
		try{
			recheck();
		}catch(IOException e){
			// ignore
		}
		return content.length;
	}

	@Override
	public Date getLastModified() {
		try{
			recheck();
		}catch(IOException e){
			// ignore
		}
		return lastModified;
	}

	@Override
	public String getMimeType() {
		return webResourceInfo.getMimeType();
	}

	private void recheck() throws IOException{
		long now = System.currentTimeMillis();
		long span = now - lastCheckingTime;
		if (span > cacheMilliSeconds){
			lastCheckingTime = now;
			rebuild();
		}
	}

	private void rebuild() throws IOException {

		if (webResourceInfo.getContentHash().equals(lastContentHash)){
			// resource not changed
			return;
		}

		this.lastContentHash = webResourceInfo.getContentHash();
		this.lastModified = webResourceInfo.getLastModified();

		//ResourceContent resourceContent = null;
		InputStream in = null;
		try {
//			resourceContent = webResourceInfo.getContentSnapshot();
//			InputStream in = resourceContent.getContent();
			in = webResourceInfo.getContent();
			
			this.content = IOUtils.toByteArray(in);
			//this.hash = DigestUtils.sha256Hex(this.content);
		} finally {
			IOUtils.closeQuietly(in);
		}
		
		// notify update listeners
		for(CacheableResourceUpdateListener listener : resourceUpdateListeners){
			listener.onUpdate(webResourceInfo.getName(), referenceResourceNames);
		}
	}

	@Override
	public void refresh() {
		lastCheckingTime = 0;
	}

	@Override
	public void addUpdateListener(CacheableResourceUpdateListener resourceUpdateListener) {
		this.resourceUpdateListeners.add(resourceUpdateListener);
	}

	@Override
	public void setReferenceResourceNames(Collection<String> names) {
		this.referenceResourceNames = names;
	}

}
