package org.archboy.clobaframe.blobstore;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The key of {@link BlobResourceInfo} object.
 *
 * @author yang
 *
 */
public class BlobKey {

	private String bucketName;
	private String key;

	public BlobKey() {
		//
	}

	public BlobKey(String bucketName, String key) {
		this.bucketName = bucketName;
		this.key = key;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(getBucketName())
				.append(getKey())
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof BlobKey)) {
			return false;
		}

		BlobKey other = (BlobKey) obj;
		return new EqualsBuilder()
				.append(getBucketName(), other.getBucketName())
				.append(getKey(), other.getKey())
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("bucketName", getBucketName())
				.append("key", getKey()).toString();
	}
}
