package org.archboy.clobaframe.webresource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author yang
 */
public abstract class AbstractWrapperResourceInfo extends AbstractNamedResourceInfo implements WrapperResourceInfo, ContentHashResourceInfo {
	
	protected Set<Integer> types = new HashSet<Integer>();
	
	protected void appendType(int type) {
		types.add(type);
	}

	protected void appendType(int type, ResourceInfo resourceInfo) {
		if (resourceInfo instanceof WrapperResourceInfo){
			types.addAll(((WrapperResourceInfo)resourceInfo).listTypes());
		}
		types.add(type);
	}
	
	@Override
	public Collection<Integer> listTypes() {
		return types;
	}

}
