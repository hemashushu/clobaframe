package org.archboy.clobaframe.webresource;

import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author yang
 */
public abstract class AbstractWebResourceInfo implements WebResourceInfo {
	
	private Set<Class<?>> underlayWebResourceInfoTypes = new HashSet<Class<?>>();
	
	/**
	 * 
	 * @param webResourceInfo 
	 */
	protected void addUnderlayWebResourceType(WebResourceInfo webResourceInfo) {
		underlayWebResourceInfoTypes.add(webResourceInfo.getClass());
	}

	/**
	 * 
	 * @return 
	 */
	public Set<Class<?>> getUnderlayWebResourceInfoTypes() {
		return underlayWebResourceInfoTypes;
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

		WebResourceInfo other = (WebResourceInfo)obj;
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
