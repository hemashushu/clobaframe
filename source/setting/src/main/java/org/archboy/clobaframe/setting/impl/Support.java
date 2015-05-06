package org.archboy.clobaframe.setting.impl;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author yang
 */
public class Support {
	private static final String placeholderRegex = "\\$\\{([\\w\\.-]+)\\}";
	private static final Pattern placeholderPattern = Pattern.compile(placeholderRegex);
	
	public static Object resolvePlaceholder(Map<String, Object> setting, Object value){
		if (!(value instanceof String)){
			return value;
		}
		
		String val = (String)value;
		
		if (val.indexOf('$') < 0) {
			return val;
		}
		
		StringBuffer builder = new StringBuffer();
		
		Matcher matcher = placeholderPattern.matcher(val);
		while (matcher.find()) {
			String holderName = matcher.group(1);
			Object replaceValue = setting.get(holderName);
			
			if (replaceValue != null && replaceValue instanceof String){
				matcher.appendReplacement(builder, (String)replaceValue);
			}
		}
		
		matcher.appendTail(builder);
		return builder.toString();
		
	}
	
	/**
	 * Merge the new or none-null value into target map.
	 * @param target
	 * @param source
	 */
	public static void merge(Map<String, Object> target, Map<String, Object> source) {
		for(Map.Entry<String, Object> entry : source.entrySet()) {
			if (entry.getValue() == null) {
				continue;
			}
			
			Object value = resolvePlaceholder(target, entry.getValue());
			target.put(entry.getKey(), value);
		}
	}
	
	/**
	 * Merge the new or none-null value into target map.
	 * @param target
	 * @param key
	 * @param value 
	 */
	public static void merge(Map<String, Object> target, String key, Object value) {
		if (value == null) {
			return;
		}

		Object resolvedValue = resolvePlaceholder(target, value);
		target.put(key, resolvedValue);
	}
}
