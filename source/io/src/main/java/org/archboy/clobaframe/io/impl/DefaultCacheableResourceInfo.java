package org.archboy.clobaframe.io.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.CacheableResourceInfo;
import org.archboy.clobaframe.io.ResourceInfo;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
public class DefaultCacheableResourceInfo implements CacheableResourceInfo {

	/**
	 * Cache milliseconds.
	 * -1 = cache always.
	 * 0 = no cache (but will check the file last modified)
	 * >0 = cache milliseconds.
	 * 
	 */
	private long cacheMilliSeconds;
	
	private long lastCheckingTime;
	private ResourceInfo resourceInfo;

	private Date lastModified;
	private byte[] content;
	private boolean forceRefresh;
	
	// to prevent infinite loop
	private boolean rebuilding;

	public DefaultCacheableResourceInfo(ResourceInfo resourceInfo, int cacheSeconds) {
		Assert.notNull(resourceInfo);
		this.resourceInfo = resourceInfo;
		this.cacheMilliSeconds = (cacheSeconds == CACHE_ALWAYS ? CACHE_ALWAYS : cacheSeconds * 1000);
		
		// first time rebuild.
		rebuild();
		this.lastCheckingTime = System.currentTimeMillis();
	}
	
	private void recheck() {
		if (cacheMilliSeconds == CACHE_ALWAYS) {
			// cache always
		}else if (cacheMilliSeconds == NO_CACHE) {
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
				(!forceRefresh && resourceInfo.getLastModified().equals(lastModified))){
			// resource not changed
			return;
		}

		this.forceRefresh = false;
		this.rebuilding = true;
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
		
		this.rebuilding = false;
	}

	@Override
	public void refresh() {
		// force update content
		forceRefresh = true;
		rebuild();
	}
	
	@Override
	public long getContentLength() {
		recheck();
		return content.length;
	}

	@Override
	public String getMimeType() {
		return resourceInfo.getMimeType();
	}

	@Override
	public InputStream getContent() throws IOException {
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
	public Date getLastModified() {
		recheck();
		return lastModified;
	}

	
}
