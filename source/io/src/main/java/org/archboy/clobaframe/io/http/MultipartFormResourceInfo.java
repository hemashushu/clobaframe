package org.archboy.clobaframe.io.http;

import java.nio.charset.Charset;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 * The multi-part POST FORM data.
 *
 * @author yang
 */
public interface MultipartFormResourceInfo extends ResourceInfo{

	/**
	 * The field name.
	 *
	 * @return
	 */
	String getFieldName();

	/**
	 * Indicates the current resource is a file or a form field.
	 *
	 * @return
	 */
	boolean isFormField();

	/**
	 * The not(isFormField()).
	 * @return 
	 */
	boolean isFileField();
	
	/**
	 * Get the file name.
	 * 
	 * Some browser may send the file name with full path,
	 * the invoker must handle this by itself.
	 *
	 * @return
	 */
	String getFileName();

	/**
	 * Get the content as string with the (session) default {@link Charset} encoding.
	 *
	 * @return
	 */
	String getContentAsString();
}
