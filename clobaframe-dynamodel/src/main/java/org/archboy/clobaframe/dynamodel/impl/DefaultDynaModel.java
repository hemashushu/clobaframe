package org.archboy.clobaframe.dynamodel.impl;

import java.util.HashMap;
import org.archboy.clobaframe.dynamodel.DynaModel;

/**
 *
 * @author yang
 */
public class DefaultDynaModel extends HashMap<String, Object>
	implements DynaModel {

	private static final long serialVersionUID = 1L;

	@Override
	public DynaModel add(String key, Object value) {
		put(key, value);
		return this;
	}

//	@Override
//	public DynaModel addModel(String name, DynaModel model) {
//		put(name, model);
//		return this;
//	}

}
