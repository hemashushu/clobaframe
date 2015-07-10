package org.archboy.clobaframe.setting.principal;

import java.util.Map;
import org.springframework.core.Ordered;

/**
 *
 * @author yang
 */
public interface PrincipalSettingProvider extends PrincipalSpecial, Ordered {

	/**
	 * The provider name.
	 * Optional.
	 * @return 
	 */
	String getName();
	
	/**
	 * 
	 * @param principal
	 * @param key
	 * @return 
	 */
	Object get(Principal principal, String key);
	
	/**
	 * 
	 * 
	 * @param principal 
	 * @return Never return null.
	 */
	Map<String, Object> list(Principal principal);

	
}
