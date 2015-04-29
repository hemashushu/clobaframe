package org.archboy.clobaframe.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * A resource object.
 * 
 * A resource means a set of binary or text data with
 * the fixed length and mime type defined,
 * optional with the name and the last modified time properties.
 *
 * @author yang
 *
 */
public interface ResourceInfo {

	/**
	 * The content length.
	 *
	 * @return
	 */
	long getContentLength();

	/**
	 * The mime type.
	 *
	 * @return The mime type name.
	 */
	String getMimeType();

	/**
	 * Get the content snapshot.
	 * New instance will be created while each invoked.
	 *
	 * @return
	 * @throws IOException
	 */
	InputStream getContent() throws IOException;

	/**
	 * Get the content snapshot with the specify range.
	 * New instance will be created while each invoked.
	 *
	 * @param start
	 * @param length
	 * @return
	 * @throws IOException If the resource content is not seek-able.
	 */
	InputStream getContent(long start, long length) throws IOException;


	/**
	 * Optional property.
	 * 
	 * Indicates the method {@link ResourceInfo#getContent(long, long) } can be invoked.
	 * 
	 * @return
	 */
	boolean isSeekable();

	/**
	 * Optional property.
	 *
	 * @return NULL if no date specified.
	 */
	Date getLastModified();

}
