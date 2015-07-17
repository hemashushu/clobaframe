package org.archboy.clobaframe.resource.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.NamedResourceInfo;
import org.archboy.clobaframe.resource.AbstractWrapperResourceInfo;
import org.archboy.clobaframe.resource.NotificationCacheableResourceInfo;
import org.archboy.clobaframe.resource.ResourceUpdateListener;
import org.archboy.clobaframe.resource.ContentHashResourceInfo;
import org.springframework.util.Assert;

public class DefaultCacheableResourceInfo extends AbstractWrapperResourceInfo implements NotificationCacheableResourceInfo, ResourceUpdateListener {

	private long cacheMilliSeconds;
	private long lastCheckingTime;
	private NamedResourceInfo resourceInfo;

	private String lastContentHash;
	private Date lastModified;
	private byte[] content;
	
	private boolean forceRefresh;
	
	// to prevent infinite loop
	private boolean rebuilding;
	
	private Set<ResourceUpdateListener> resourceUpdateListeners;

	/**
	 * 
	 * @param resourceInfo
	 * @param cacheSeconds -1 = cache always, 0 = no cache, >0 cache seconds.
	 */
	public DefaultCacheableResourceInfo(NamedResourceInfo resourceInfo, int cacheSeconds) {
		Assert.notNull(resourceInfo);
		Assert.isInstanceOf(ContentHashResourceInfo.class, resourceInfo);
		
		this.resourceInfo = resourceInfo;
		this.cacheMilliSeconds = (cacheSeconds == -1 ? -1 : cacheSeconds * 1000);
		this.resourceUpdateListeners = new HashSet<ResourceUpdateListener>();
		
		appendType(getType(), resourceInfo);
		
		// first time rebuild
		rebuild();
		this.lastCheckingTime = System.currentTimeMillis();
	}

	@Override
	public int getType() {
		return TYPE_CACHE;
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
		return resourceInfo.getName();
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
		return resourceInfo.getMimeType();
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
		
		// get the upstream content hash
		String currentContentHash = ((ContentHashResourceInfo)resourceInfo).getContentHash();
		
		if (rebuilding || 
				(!forceRefresh && currentContentHash.equals(lastContentHash))){
			// resource not changed
			return;
		}

		this.forceRefresh = false;
		this.rebuilding = true;
		this.lastContentHash = currentContentHash;
		this.lastModified = resourceInfo.getLastModified();

		InputStream in = null;
		try {
			in = resourceInfo.getContent();
			this.content = IOUtils.toByteArray(in);
		} catch (IOException ex) {
			// ignore
		} finally {
			IOUtils.closeQuietly(in);
		}
		
		// notify the listeners
		for(ResourceUpdateListener listener : resourceUpdateListeners){
			listener.onUpdate(resourceInfo.getName());
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
	public void addUpdateListener(ResourceUpdateListener resourceUpdateListener) {
		resourceUpdateListeners.add(resourceUpdateListener);
	}

	@Override
	public void onUpdate(String childResourceName) {
		refresh();
	}

}
