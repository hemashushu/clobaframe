package org.archboy.clobaframe.webresource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author yang
 */
public abstract class AbstractServerWebResourceInfo extends AbstractWebResourceInfo implements ServerWebResourceInfo {
	
	protected Set<Integer> types = new HashSet<Integer>();
	
	protected void appendType(int type) {
		types.add(type);
	}

	protected void appendType(int type, WebResourceInfo inheritInfo) {
		if (inheritInfo instanceof ServerWebResourceInfo) {
			types.addAll(((ServerWebResourceInfo)inheritInfo).getInheritTypes());
		}
		
		types.add(type);
	}
	
	@Override
	public Collection<Integer> getInheritTypes() {
		return types;
	}

}
