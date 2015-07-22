package org.archboy.clobaframe.ioc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
public class BeanDefinition {
	private String id;
	private Class<?> clazz;
	private Object object;
	private Class<?>[] interfaces;
	private Annotation[] annotations;
	private Method[] methods; // declared methods
	private String initMethodName;
	private String disposeMethodName;
	private boolean initialized;
	
	public BeanDefinition(String id, Class<?> clazz, Object object, 
			Class<?>[] interfaces, Annotation[] annotations, 
			Method[] methods, 
			String initMethodName, String disposeMethodName, 
			boolean initialized) {
		this.id = id;
		this.clazz = clazz;
		this.object = object;
		this.interfaces = interfaces;
		this.annotations = annotations;
		this.methods = methods;
		this.initMethodName = initMethodName;
		this.disposeMethodName = disposeMethodName;
		this.initialized = initialized;
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

	public Annotation[] getAnnotations() {
		return annotations;
	}

	public Method[] getMethods() {
		return methods;
	}

	public String getInitMethodName() {
		return initMethodName;
	}

	public String getDisposeMethodName() {
		return disposeMethodName;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		Assert.isTrue(initialized);
		this.initialized = initialized;
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

		BeanDefinition other = (BeanDefinition)obj;
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
