package org.archboy.clobaframe.dynamodel.impl;

import java.util.HashMap;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.WrapDynaBean;
import org.archboy.clobaframe.dynamodel.DynaModel;
import org.springframework.util.Assert;

/**
 * Wrap a POJO object into DynaModel, all properties value will be copied.
 * This implementation only wrap 1 level property.
 *
 * @author yang
 */
public class WrapDynaModel extends HashMap<String, Object>
	implements DynaModel{

	private static final long serialVersionUID = 1L;

	public WrapDynaModel(Object object){
		Assert.notNull(object);
		
		DynaBean dynaBean = new WrapDynaBean(object);

		// add all properties (except 'class') to map
		DynaClass dynaClass = dynaBean.getDynaClass();
		for(DynaProperty property : dynaClass.getDynaProperties()){
			String key = property.getName();
			if (key.equals("class")){
				continue;
			}

			put(key, dynaBean.get(key));
		}
	}

	@Override
	public DynaModel add(String key, Object value) {
		put(key, value);
		return this;
	}

//	@Override
//	public DynaModel addModel(String name) {
//		DynaModel model = new DefaultDynaModel();
//		put(name, model);
//		return model;
//	}

	@Override
	public DynaModel addModel(String name, DynaModel model) {
		put(name, model);
		return this;
	}
}
