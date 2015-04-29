package org.archboy.clobaframe.blobstore.impl;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.archboy.clobaframe.blobstore.BlobResourceInfo;

/**
 *
 * @author yang
 */
public abstract class AbstractBlobResourceInfo implements BlobResourceInfo {
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(getRepositoryName())
				.append(getKey())
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof BlobResourceInfo)) {
			return false;
		}

		BlobResourceInfo other = (BlobResourceInfo) obj;
		return new EqualsBuilder()
				.append(getRepositoryName(), other.getRepositoryName())
				.append(getKey(), other.getKey())
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("repositoryName", getRepositoryName())
				.append("key", getKey()).toString();
	}
	
}
