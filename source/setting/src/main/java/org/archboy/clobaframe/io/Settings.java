package org.archboy.clobaframe.io;

import java.util.HashMap;
import java.util.Map;

/**
 * The settings (key-value collection).
 * The key format: "xxx.yyy.zzz",
 * the value data type can be String, Integer, Double, Boolean, Date and null.
 * 
 * @author yang
 */
public class Settings extends HashMap<String, Object>{
	
	public static enum Level {
		system,
		global,
		profile
	}
}
