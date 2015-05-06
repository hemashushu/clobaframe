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
public abstract class AbstractJsonSettingRepository implements SettingRepository {

	private TypeReference<Map<String, Object>> typeReference = 
			new TypeReference<Map<String, Object>>() {};

	private ObjectMapper objectMapper;
	
	@Override
	public Map<String, Object> read(InputStream in) throws IOException {
		Map<String, Object> map = objectMapper.readValue(in, typeReference);
		Map<String, Object> setting = new HashMap<String, Object>();
		flat(setting, map, null, null);
		return setting;
	}

	@Override
	public void write(OutputStream outputStream, Map<String, Object> setting) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		cascade(setting, map);
		objectMapper.writeValue(outputStream, map);
	}
	
	/**
	 * 
	 * @param setting the result (flat).
	 * @param map the source.
	 * @param key The current map entry key.
	 * @param path Full path, e.g. "foo.bar.path.name".
	 */
	private void flat(Map<String, Object> setting, Map<String, Object> map, String key, String path) {
		if (key == null) {
			// it's the first time
			for(String name : map.keySet()){
				flat(setting, map, name, name);
			}
		}else{
			Object objValue = map.get(key);
			if (objValue instanceof Map) {
				// resolve child node.
				Map<String, Object> child = (Map<String, Object>)objValue;
				for(String name : child.keySet()) {
					flat(setting, child, name, path + "." + name);
				}
			}else{
				setting.put(path, objValue);
			}
		}
	}
	
	/**
	 * 
	 * @param setting the source (flat).
	 * @param map the result.
	 */
	private void cascade(Map<String, Object> setting, Map<String, Object> map) {
		
		// sort the keys
		Set<String> keys = setting.keySet();
		List<String> sortedKeys = new ArrayList<String>(keys);
		sortedKeys.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		
		// group the key name (the first part of key.
		// e.g. the name of "foo.bar.item" is "foo".
		String lastKeyName = null;
		Map<String, Object> child = null;
		for(int idx = 0; idx < sortedKeys.size(); idx++) {
			String key = sortedKeys.get(idx);
			int pos = key.indexOf('.');
			if (pos < 0) {
				// there has no child node.
				map.put(key, setting.get(key));
			}else{
				String keyName = key.substring(0, idx);
				if (!keyName.equals(lastKeyName) || idx == sortedKeys.size() - 1){
					// handle the pre child
					if (child != null) {
						cascade(child, map);
					}
					
					// create new child map
					child = new HashMap<String, Object>();
					child.put(key.substring(idx + 1), setting.get(key));
					map.put(keyName, child);
					
					// check whether current item is the last item
					if (idx == sortedKeys.size() -1){
						cascade(child, map);
					}
					
				}else{
					// copy the sub items.
					child.put(key.substring(idx + 1), setting.get(key));
				}
			}
		}
	}
	
}
