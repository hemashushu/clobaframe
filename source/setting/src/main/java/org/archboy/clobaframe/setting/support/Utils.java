package org.archboy.clobaframe.setting.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 */
public class Utils {
	private static final String placeholderRegex = "\\$\\{([\\w\\.-]+)\\}";
	private static final Pattern placeholderPattern = Pattern.compile(placeholderRegex);
	
	private static final TypeReference<Map<String, Object>> typeReference = 
			new TypeReference<Map<String, Object>>() {};

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static Map<String, Object> readJson(InputStream in) throws IOException {
		Map<String, Object> map = objectMapper.readValue(in, typeReference);
		return Utils.flat(map);
	}

	public static void writeJson(OutputStream outputStream, Map<String, Object> setting) throws IOException {
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		Map<String, Object> map = Utils.cascade(setting);
		objectMapper.writeValue(outputStream, map);
	}
	
	public static Map<String, Object> readProperties(InputStream in) throws IOException{
		Properties properties = new OrderedProperties();
		properties.load(in);
		
		Map<String, Object> setting = new LinkedHashMap<String, Object>();
		for(Map.Entry<Object, Object> entry : properties.entrySet()){
			setting.put((String)entry.getKey(), entry.getValue());
		}
		
		return setting;
	}
	
	public static void writeProperties(
			OutputStream outputStream, 
			Map<String, Object> setting)
			throws IOException {
		Properties properties = new OrderedProperties();
		properties.putAll(setting);
		properties.store(outputStream, null);
	}
	
	public static Map<String, Object> resolvePlaceholder(Map<String, Object> setting){
		Map<String, Object> resolvedMap = new LinkedHashMap<String, Object>();
		for(Map.Entry<String, Object> entry : setting.entrySet()) {
			resolvedMap.put(entry.getKey(), resolvePlaceholder(setting, entry.getValue()));
		}
		return resolvedMap;
	}
	
	public static Object resolvePlaceholder(Map<String, Object> setting, Object value){
		if (value == null) {
			return null;
		}
		
		if (!(value instanceof String)){
			return value;
		}
		
		String sv = (String)value;
		if (sv.contains("${")) {
			return resolvePlaceholder(setting, sv, 0);
		}else{
			return sv;
		}
	}
	
	/**
	 * 
	 * About depth:
	 * item1=${item2}, item2=value2  > iterate: no depth, depth=0
	 * item1=${item2}, item2=${item3}, item3=value  > iterate: no depth, depth=0, depth=1
	 * item1=${item2}, item2=${item3}, item3=${item4}, item4=value4  > iterate: no depth, depth=0, depth=1, depth=2
	 * 
	 * @param setting
	 * @param value
	 * @param depth 
	 * @return 
	 */
	private static String resolvePlaceholder(Map<String, Object> setting, String value, int depth){ 
		StringBuffer builder = new StringBuffer();
		Matcher matcher = placeholderPattern.matcher(value);
		while (matcher.find()) {
			String holderName = matcher.group(1);
			Object replaceValue = setting.get(holderName);
			
			if (replaceValue == null || !(replaceValue instanceof String)) {
				// no match placeholder found.
				matcher.appendReplacement(builder, "\\${" + holderName + "}");
				continue;
			}
			
			String sv = (String)replaceValue;
			if (sv.contains("${")){
				if (depth < 16) { // max depth
					replaceValue = resolvePlaceholder(setting, sv, depth +1);
				}
			}
			
			String v = (String)replaceValue;
			v = v.replace("\\", "\\\\"); // the windows file path seperator conflict with the regular express escape character.
			v = v.replace("$", "\\$"); // the regular replace character.
			matcher.appendReplacement(builder, v);
		}
		
		matcher.appendTail(builder);
		return builder.toString();
	}
	
	/**
	 * Merge the new setting into target map.
	 * @param source
	 * @param append
	 * @return 
	 */
	public static Map<String, Object> merge(
			Map<String, Object> source, Map<String, Object> append) {

		Map<String, Object> result = new LinkedHashMap<String, Object>();
		result.putAll(source);
		
		for(Map.Entry<String, Object> entry : append.entrySet()) {
			if (entry.getValue() == null) {
				continue;
			}
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	public static Map<String, Object> merge(
			Map<String, Object> source, Properties properties) {
		Map<String, Object> append = new LinkedHashMap<String, Object>();
		
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			append.put((String)entry.getKey(), entry.getValue());
		}
		
		return merge(source, append);
	}
	
	/**
	 * Merge the new value into target map.
	 * @param source
	 * @param key
	 * @param value 
	 * @return  
	 */
	public static Map<String, Object> merge(
			Map<String, Object> source, String key, Object value) {
		Assert.notNull(value);
		
		Map<String, Object> append = new LinkedHashMap<String, Object>();
		append.put(key, value);
		
		return merge(source, append);
	}
	
	/**
	 * 
	 * @param source
	 * @return 
	 */
	public static Map<String, Object> flat(Map<String, Object> source){
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		for(String name : source.keySet()){
			flat(result, source, name, name);
		}
		return result;
	} 
	
	/**
	 * 
	 * @param setting the result (will be flat).
	 * @param map the source (cascade).
	 * @param key The current map entry key.
	 * @param path Full path, e.g. "foo.bar.path.name".
	 */
	private static void flat(Map<String, Object> setting, Map<String, Object> map, String key, String path) {
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
	
	/**
	 * 
	 * @param source
	 * @return 
	 */
	public static Map<String, Object> cascade(Map<String, Object> source) {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		cascade(source, result);
		return result;
	}
	
	/*
	private static void cascade(Map<String, Object> setting, Map<String, Object> map) {
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
		Map<String, Object> subList = null;
		
		for(int idx = 0; idx < sortedKeys.size(); idx++) {
			String key = sortedKeys.get(idx);
			
			int pos = key.indexOf('.');
			if (pos < 0) {
				// there is no child node.
				map.put(key, setting.get(key));
			}else{
				String keyName = key.substring(0, pos);
				
				if (!keyName.equals(lastKeyName)){ // || 
					// handle the pre child
					if (subList != null) {
						Map<String, Object> child = new LinkedHashMap<String, Object>();
						map.put(lastKeyName, child);
						cascade(subList, child);
					}
					
					lastKeyName = keyName;
					
					// create new child map
					subList = new LinkedHashMap<String, Object>();
					subList.put(key.substring(pos + 1), setting.get(key));
				}else{
					// copy the sub items.
					subList.put(key.substring(pos + 1), setting.get(key));
				}
				
				// check whether current item is the last item
				if (idx == sortedKeys.size() -1){
					Map<String, Object> child = new LinkedHashMap<String, Object>();
					map.put(lastKeyName, child);
					cascade(subList, child);
				}
			}
		}
	}
	*/
	
	private static void cascade(Map<String, Object> setting, Map<String, Object> map) {
		
		// group keys
		Map<String, Map<String, Object>> groups = new LinkedHashMap<String, Map<String, Object>>();
		for(String key : setting.keySet()){
			int dotPos = key.indexOf('.');
			if (dotPos < 0) {
				// this is a value node.
				map.put(key, setting.get(key));
			}else{
				// this is child node.
				String keyName = key.substring(0, dotPos);
				String subKeyName = key.substring(dotPos + 1);
				
				Map<String, Object> childList = groups.get(keyName);
				if (childList == null){
					childList = new LinkedHashMap<String, Object>();
					groups.put(keyName, childList);
				}
				
				childList.put(subKeyName, setting.get(key));
			}
		}
		
		// resolve child nodes
		for(String key : groups.keySet()){
			Map<String, Object> child = new LinkedHashMap<String, Object>();
			map.put(key, child);

			Map<String, Object> childList = groups.get(key);
			cascade(childList, child);
		}
	}
}
