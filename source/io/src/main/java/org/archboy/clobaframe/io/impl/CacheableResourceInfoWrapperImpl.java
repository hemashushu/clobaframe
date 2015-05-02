/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.archboy.clobaframe.io.impl;

import javax.inject.Named;
import org.archboy.clobaframe.io.CacheableResourceInfo;
import org.archboy.clobaframe.io.CacheableResourceInfoWrapper;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author yang
 */
@Named
public class CacheableResourceInfoWrapperImpl implements CacheableResourceInfoWrapper {
	
	@Override
	public CacheableResourceInfo wrap(ResourceInfo resourceInfo, int cacheSeconds) {
		return new DefaultCacheableResourceInfo(resourceInfo, cacheSeconds);
	}
}
