package org.archboy.clobaframe.io.http;

import java.nio.charset.Charset;
import org.archboy.clobaframe.io.ResourceInfo;

/**
 * Commonly means the multi-part POST form data.
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
	 * Get the file name.
	 * <p>
	 *     Some browser may send the file name with full path,
	 *     the caller must handle it by itself.
	 * </p>
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
