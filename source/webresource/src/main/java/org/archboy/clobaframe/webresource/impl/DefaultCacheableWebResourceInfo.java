package org.archboy.clobaframe.webresource.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.webresource.AbstractWebResourceInfo;
import org.archboy.clobaframe.webresource.CacheableWebResourceInfo;
import org.archboy.clobaframe.webresource.CacheableWebResourceInfoUpdateListener;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.springframework.util.Assert;

public class DefaultCacheableWebResourceInfo extends AbstractWebResourceInfo implements CacheableWebResourceInfo, CacheableWebResourceInfoUpdateListener {

	private long cacheMilliSeconds;
	private long lastCheckingTime;
	private WebResourceInfo webResourceInfo;

	private String lastContentHash;
	private Date lastModified;
	private byte[] content;
	
	private boolean forceRefresh;
	
	// to prevent infinite loop
	private boolean rebuilding;
	
	private Set<CacheableWebResourceInfoUpdateListener> resourceUpdateListeners;

	/**
	 * 
	 * @param webResourceInfo
	 * @param cacheSeconds -1 = cache always, 0 = no cache, >0 cache seconds.
	 */
	public DefaultCacheableWebResourceInfo(WebResourceInfo webResourceInfo, int cacheSeconds) {
		Assert.notNull(webResourceInfo);
		
		this.webResourceInfo = webResourceInfo;
		this.cacheMilliSeconds = (cacheSeconds == -1 ? -1 : cacheSeconds * 1000);
		this.resourceUpdateListeners = new HashSet<CacheableWebResourceInfoUpdateListener>();
		
		addType(CacheableWebResourceInfo.class, webResourceInfo);
		
		// first time rebuild
		rebuild();
		this.lastCheckingTime = System.currentTimeMillis();
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
		recheck();
		return lastContentHash;
	}

	@Override
	public long getContentLength() {
		recheck();
		return content.length;
	}

	@Override
	public Date getLastModified() {
		recheck();
		return lastModified;
	}

	@Override
	public String getMimeType() {
		return webResourceInfo.getMimeType();
	}

	private void recheck() {
		if (cacheMilliSeconds == -1) {
			// cache always
		}else if (cacheMilliSeconds == 0) {
			// no cache
			rebuild();
		}else{
			long now = System.currentTimeMillis();
			long span = now - lastCheckingTime;
			if (span > cacheMilliSeconds){
				lastCheckingTime = now;
				rebuild();
			}
		}
	}

	private void rebuild() {

		if (rebuilding || 
				(!forceRefresh && webResourceInfo.getContentHash().equals(lastContentHash))){
			// resource not changed
			return;
		}

		this.forceRefresh = false;
		this.rebuilding = true;
		this.lastContentHash = webResourceInfo.getContentHash();
		this.lastModified = webResourceInfo.getLastModified();

		InputStream in = null;
		try {
			in = webResourceInfo.getContent();
			this.content = IOUtils.toByteArray(in);
		} catch (IOException ex) {
			// ignore
		} finally {
			IOUtils.closeQuietly(in);
		}
		
		// notify update listeners
		for(CacheableWebResourceInfoUpdateListener listener : resourceUpdateListeners){
			listener.onUpdate(webResourceInfo.getName());
		}
		
		this.rebuilding = false;
	}

	@Override
	public void refresh() {
		// force update content
		forceRefresh = true;
		rebuild();
	}

	@Override
	public void addUpdateListener(CacheableWebResourceInfoUpdateListener resourceUpdateListener) {
		resourceUpdateListeners.add(resourceUpdateListener);
	}

	@Override
	public void onUpdate(String childResourceName) {
		refresh();
	}

}
