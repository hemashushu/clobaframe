package org.archboy.clobaframe.media.image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 *
 * @author yang
 */
public class Utils {
	
	/**
	 * Save image to file, for manual checking.
	 *
	 * @param image
	 * @param filename
	 * @throws IOException
	 */
	public static void saveImage(Image image, String filename) throws IOException{
		String formatName = image.getFormat().toString().toLowerCase();
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		File workDir = new File(tempDir, "clobaframe/media/image");
		if (!workDir.exists()) {
			workDir.mkdirs();
		}
		File file = new File(workDir, filename + "." + formatName);
		
		
		ResourceInfo resourceInfo = image.getResourceInfo();
		//ResourceContent resourceContent = resourceInfo.getContentSnapshot();
		InputStream in = resourceInfo.getInputStream();
		
		FileOutputStream out = new FileOutputStream(file);
		IOUtils.copy(in, out);
		
		out.close();
		in.close();
	}
}
