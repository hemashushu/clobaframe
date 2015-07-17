package org.archboy.clobaframe.ioc.impl;

import java.lang.reflect.Method;
import java.util.Collection;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author yang
 */
public class Bean {
	private String id;
	private Class<?> clazz;
	private Object object;
	private Class<?>[] interfaces;
	private Method initMethod;
	private Method closeMethod;
	private boolean inited;
	
	public Bean(String id, Class<?> clazz, Object object, Class<?>[] interfaces, Method initMethod, Method closeMethod, boolean inited) {
		this.id = id;
		this.clazz = clazz;
		this.object = object;
		this.interfaces = interfaces;
		this.initMethod = initMethod;
		this.closeMethod = closeMethod;
		this.inited = inited;
	}

	public String getId() {
		return id;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public Object getObject() {
		return object;
	}

	public Class<?>[] getInterfaces() {
		return interfaces;
	}

	public Method getInitMethod() {
		return initMethod;
	}

	public Method getCloseMethod() {
		return closeMethod;
	}

	public boolean isInited() {
		return inited;
	}

	public void finishInited() {
		this.inited = true;
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

		Bean other = (Bean)obj;
		return new EqualsBuilder()
				.append(getId(), other.getId())
				.append(getClazz(), other.getClazz())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(getId())
				.append(getClazz())
				.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("id", getId())
				.append("class", getClazz())
				.toString();
	}
}
