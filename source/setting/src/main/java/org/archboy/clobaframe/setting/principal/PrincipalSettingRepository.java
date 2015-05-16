package org.archboy.clobaframe.setting.principal;

import java.util.Map;

/**
 *
 * @author yang
 */
public interface PrincipalSettingRepository extends PrincipalSpecial {
	
	void set(Principal profile, Map<String, Object> item);
	
	void set(Principal profile, String key, Object value);
}
