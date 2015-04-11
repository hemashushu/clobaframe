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
public class DefaultViewModel extends HashMap<String, Object>
	implements ViewModel {

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private ViewModel parent;

	public DefaultViewModel() {
		this.parent = null;
	}

	private DefaultViewModel(ViewModel parent) {
		this.parent = parent;
	}
	
	@Override
	public ViewModel add(String key, Object value) {
		put(key, value);
		return this;
	}

	@Override
	public ViewModel addChild(String name) {
		ViewModel childViewModel = new DefaultViewModel(this);
		put(name, childViewModel);
		return childViewModel;
	}

	@Override
	public ViewModel parent() {
		return parent;
	}
	
	/**
	 * Wrap an object info ViewModel.
	 * 
	 * @param object
	 * @return 
	 */
	public static ViewModel Wrap(Object object){
		Assert.notNull(object);
		
		ViewModel viewModel = new DefaultViewModel();
		
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
	
	public static ViewModel Wrap(Object object, boolean deep){
		throw new UnsupportedOperationException("Does not supported.");
	}
	
	/**
	 * Wrap an object into ViewModel with the specify properties.
	 * 
	 * @param object
	 * @param names
	 * @return 
	 */
	public static ViewModel Wrap(Object object, Collection<String> names){
		Assert.notNull(object);
		Assert.notNull(names);
		
		ViewModel viewModel = new DefaultViewModel();
		
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
