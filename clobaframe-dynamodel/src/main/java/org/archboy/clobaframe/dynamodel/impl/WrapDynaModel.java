/*
 * Copyright 2011 Spark Young (sparkyoungs@gmail.com). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.archboy.clobaframe.dynamodel.impl;

import java.util.HashMap;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.WrapDynaBean;
import org.archboy.clobaframe.dynamodel.DynaModel;

/**
 * Wrap a POJO object into DynaModel, all properties value will be copied.
 * This implementation only wrap 1 level property.
 *
 * @author young
 */
public class WrapDynaModel extends HashMap<String, Object>
	implements DynaModel{

	private static final long serialVersionUID = 1L;

	public WrapDynaModel(Object object){

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
