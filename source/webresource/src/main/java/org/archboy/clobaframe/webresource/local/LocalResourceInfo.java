package org.archboy.clobaframe.webresource.local;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.archboy.clobaframe.io.NamedResourceInfo;
import org.archboy.clobaframe.io.file.impl.DefaultFileBaseResourceInfo;
import org.archboy.clobaframe.webresource.ContentHashResourceInfo;

public class LocalResourceInfo extends DefaultFileBaseResourceInfo implements NamedResourceInfo, ContentHashResourceInfo {

	private String name;

	public LocalResourceInfo(
			File file, String mimeType, String name) {
		super(file, mimeType);
		this.name = name;
	}

	@Override
	public String getContentHash() {
		String hash = null;
		InputStream in = null;
		try {
			in = getContent();
			hash = DigestUtils.sha256Hex(in);
		}catch(IOException e){
			// ignore
		}finally{
			IOUtils.closeQuietly(in);
		}
		return hash;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null){
			return false;
		}

		if (obj == this){
			return true;
		}

		if(obj.getClass() != getClass()){
			return false;
		}

		NamedResourceInfo other = (NamedResourceInfo)obj;
		return new EqualsBuilder()
				.append(getName(), other.getName())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(getName())
				.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("name", getName())
				.toString();
	}
}
