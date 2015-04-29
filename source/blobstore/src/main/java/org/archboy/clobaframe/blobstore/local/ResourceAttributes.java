package org.archboy.clobaframe.blobstore.local;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 * @author yang
 */
public class ResourceAttributes implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String mimeType;
	private Date lastModified;
	private String metas;

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getMetas() {
		return metas;
	}

	public void setMetas(String metas) {
		this.metas = metas;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(getMimeType())
				.append(getLastModified())
				.append(getMetas())
				.toHashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null){
			return false;
		}

		if (o == this){
			return true;
		}

		if(o.getClass() != getClass()){
			return false;
		}

		ResourceAttributes other = (ResourceAttributes)o;
		return new EqualsBuilder()
				.append(getMimeType(), other.getMimeType())
				.append(getLastModified(), other.getLastModified())
				.append(getMetas(), other.getMetas())
				.isEquals();
	}
}
