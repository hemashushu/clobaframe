package org.archboy.clobaframe.media.video.impl;

import java.util.Date;
import org.archboy.clobaframe.io.ResourceInfo;
import org.archboy.clobaframe.media.MetaData;
import org.archboy.clobaframe.media.video.Video;

/**
 *
 * @author yang
 */
public class DefaultVideo implements Video {
	
	private ResourceInfo resourceInfo;
	private MetaData metaData;
	
	private Format format;
	private int width;
	private int height;
	private double duration;

	public DefaultVideo(ResourceInfo resourceInfo, Format format, int width, int height, double duration) {
		this.resourceInfo = resourceInfo;
		this.format = format;
		this.width = width;
		this.height = height;
		this.duration = duration;
	}

	@Override
	public ResourceInfo getResourceInfo() {
		return resourceInfo;
	}

	@Override
	public MetaData getMetaData() {
		return metaData;
	}

	@Override
	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	@Override
	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	@Override
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public Format getFormat() {
		return format;
	}

	@Override
	public ResourceInfo getResourceInfo(Date lastModified) {
		throw new UnsupportedOperationException("Does not supported.");
	}
}
