package org.archboy.clobaframe.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.WrapDynaBean;
import org.springframework.util.Assert;

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
	
	/**
	 * Wrap an object info ObjectMap.
	 * 
	 * @param object
	 * @return 
	 */
	public static ObjectMap Wrap(Object object){
		Assert.notNull(object);
		
		ObjectMap viewModel = new DefaultObjectMap();
		
		// check object type
		if (object instanceof Map){
			Map m = (Map)object;
			for(Object name : m.keySet()){
				viewModel.add(name.toString(), m.get(name));
			}
			return viewModel;
		}

		// copy all properties (except 'class') to map
		DynaBean dynaBean = new WrapDynaBean(object);
		DynaClass dynaClass = dynaBean.getDynaClass();
		for(DynaProperty property : dynaClass.getDynaProperties()){
			String key = property.getName();
			if (key.equals("class")){
				continue;
			}

			viewModel.add(key, dynaBean.get(key));
		}
		
		return viewModel;
	}
	
	public static ObjectMap Wrap(Object object, boolean deep){
		throw new UnsupportedOperationException("Does not supported.");
	}
	
	/**
	 * Wrap an object into ObjectMap with the specify properties.
	 * 
	 * @param object
	 * @param names
	 * @return 
	 */
	public static ObjectMap Wrap(Object object, Collection<String> names){
		Assert.notNull(object);
		Assert.notNull(names);
		
		ObjectMap viewModel = new DefaultObjectMap();
		
		// check object type
		if (object instanceof Map){
			Map m = (Map)object;
			for(Object name : m.keySet()){
				String s = name.toString();
				if (names.contains(s)){
					viewModel.add(s, m.get(name));
				}
			}
			return viewModel;
		}
		
		// copy all properties (except 'class') to map
		DynaBean dynaBean = new WrapDynaBean(object);
		DynaClass dynaClass = dynaBean.getDynaClass();
		for(DynaProperty property : dynaClass.getDynaProperties()){
			String key = property.getName();
			if (names.contains(key)){
				viewModel.add(key, dynaBean.get(key));
			}
		}
		
		return viewModel;
	}
}
