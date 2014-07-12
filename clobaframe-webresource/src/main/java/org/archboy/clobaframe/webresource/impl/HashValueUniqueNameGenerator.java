package org.archboy.clobaframe.webresource.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Named;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.webresource.UniqueNameGenerator;
import org.archboy.clobaframe.webresource.WebResourceInfo;

/**
 *
 * @author yang
 */
@Named
public class HashValueUniqueNameGenerator implements UniqueNameGenerator {

	@Override
	public String getName() {
		return "hash";
	}

	@Override
	public String getUniqueName(WebResourceInfo webResourceInfo) {
		String hash = null;
		InputStream in = null;
		try {
			in = webResourceInfo.getInputStream();
			hash = DigestUtils.sha256Hex(in);
		}catch(IOException e){
			//
		}finally{
			IOUtils.closeQuietly(in);
		}

		return DigestUtils.sha256Hex(webResourceInfo.getName() + "?hash=" + hash);
	}
	
}
