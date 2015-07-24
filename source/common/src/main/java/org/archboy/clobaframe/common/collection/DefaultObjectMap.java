package org.archboy.clobaframe.common.collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;

/**
 *
 * @author yang
 */
public class DefaultObjectMap extends HashMap<String, Object>
	implements ObjectMap {

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private ObjectMap parent;

	@JsonIgnore
	private ObjectMap top;
	
	public DefaultObjectMap() {
		this.parent = null;
		this.top = null;
	}

	private DefaultObjectMap(ObjectMap top, ObjectMap parent) {
		this.top = top;
		this.parent = parent;
	}
	
	@Override
	public ObjectMap add(String key, Object value) {
		put(key, value);
		return this;
	}

	@Override
	public ObjectMap addChild(String name) {
		ObjectMap childViewModel = new DefaultObjectMap(top(), this);
		put(name, childViewModel);
		return childViewModel;
	}

	@Override
	public ObjectMap parent() {
		return parent;
	}

	@Override
	public ObjectMap top() {
		return (top == null ? this : top);
	}
}
