package org.archboy.clobaframe.setting.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author yang
 */
public abstract class JsonSettingStore extends AbstractSettingStore {

	private TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};

	private ObjectMapper objectMapper;
	
	@Override
	public Map<String, Object> read(InputStream in) throws IOException {
		Map<String, Object> map = objectMapper.readValue(in, typeReference);
		Map<String, Object> setting = new HashMap<String, Object>();
		flat(setting, map, null, null);
		return setting;
	}

	@Override
	public void write(InputStream in, OutputStream outputStream, Map<String, Object> setting) throws IOException {
		Map<String, Object> origin = read(in);
		Support.merge(origin, setting);
		Map<String, Object> map = new HashMap<String, Object>();
		cascade(origin, map);
		objectMapper.writeValue(outputStream, map);
	}
	
	private void flat(Map<String, Object> setting, Map<String, Object> map, String key, String path) {
		if (key == null) {
			for(String name : map.keySet()){
				flat(setting, map, name, name);
			}
		}else{
			Object objValue = map.get(key);
			if (objValue instanceof Map) {
				Map<String, Object> child = (Map<String, Object>)objValue;
				for(String name : child.keySet()) {
					flat(setting, map, name, path + "." + name);
				}
			}else{
				setting.put(path, objValue);
			}
		}
	}
	
	private void cascade(Map<String, Object> setting, Map<String, Object> map) {
		Set<String> keys = setting.keySet();
		List<String> sortedKeys = new ArrayList<String>(keys);
		sortedKeys.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		
		String lastKeyName = null;
		Map<String, Object> child = null; //new HashMap<String, Object>();
		for(int idx = 0; idx < sortedKeys.size(); idx++) {
			String key = sortedKeys.get(idx);
			int pos = key.indexOf('.');
			if (pos < 0) {
				map.put(key, setting.get(key));
			}else{
				String keyName = key.substring(0, idx);
				if (!keyName.equals(lastKeyName) || idx == sortedKeys.size() - 1){
					// handle pre map
					cascade(child, map);
					
					// create new child map
					child = new HashMap<String, Object>();
					child.put(key.substring(idx + 1), setting.get(key));
					map.put(keyName, child);
				}else{
					child.put(key.substring(idx + 1), setting.get(key));
				}
			}
		}
	}
	
}
