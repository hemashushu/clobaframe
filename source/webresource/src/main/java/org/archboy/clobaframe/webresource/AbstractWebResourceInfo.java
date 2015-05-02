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
	
	// the underlay web resource types
	private Set<Class<? extends WebResourceInfo>> types = 
			new HashSet<Class<? extends WebResourceInfo>>();
	
	protected void addType(Class<? extends WebResourceInfo> clazz){
		types.add(clazz);
	}
	
	protected void addType(Class<? extends WebResourceInfo> clazz, WebResourceInfo underlay) {
		if (underlay instanceof AbstractWebResourceInfo) {
			types.addAll(((AbstractWebResourceInfo)underlay).getTypes());
		}
		
		types.add(clazz);
	}

	public Set<Class<? extends WebResourceInfo>> getTypes() {
		return types;
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
