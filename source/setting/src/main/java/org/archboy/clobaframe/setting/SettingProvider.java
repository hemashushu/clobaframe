package org.archboy.clobaframe.setting;

import java.util.Map;
import org.springframework.core.Ordered;

/**
 * The setting items provider.
 * One setting manager can contains several providers.
 * 
 * @author yang
 */
public interface SettingProvider extends Ordered {
	
	/**
	 * 
	 * @return Never return null.
	 */
	Map<String, Object> getAll();
	
}
