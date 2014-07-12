package org.archboy.clobaframe.webresource;

import java.io.FileNotFoundException;
import java.util.Collection;


/**
 * Web resources service.
 *
 * <p>
 *     It provides the following functions:
 * </p>
 * <ul>
 *     <li>Get the specify web resource object.</li>
 *     <li>Get the full location (url) of the specify web resource.</li>
 *     <li>Some implements may synchronize web resources with the content
 *         delivery network.</li>
 *     <li>Replace the resource location in the css/javascript with the
 *         actually URL automatically in runtime.</li>
 * </ul>
 *
 * <p>
 *     About the location replacement:
 * </p>
 * <p>
 *     In the css/javascript file can referent other web resource by name using
 *     the "[[RESOURCE_NAME]]" symbol. for example, there are "common.css"
 *     and the "logo.png", if you intend to referent "logo.png",
 *     in the "common.css" you can write "[[logo.png]]", appear as:
 *     <pre>
 *     #button {
 *         background: url("[[test.png]]") no-repeat 0px 0px;
 *     }
 *     </pre>
     Then the WebResourceManager will replace the "[[test.png]]" with
     the actually URL in runtime.
 </p>
 *
 * @author yang
 *
 */
public interface WebResourceManager {

	/**
	 *
	 * @return
	 */
	Collection<WebResourceInfo> getAllResources();

	/**
	 *
	 * @param name
	 * @return
	 * @throws java.io.FileNotFoundException
	 */
	WebResourceInfo getResource(String name) throws FileNotFoundException;

	/**
	 *
	 * @param uniqueName
	 * @return
	 * @throws java.io.FileNotFoundException
	 */
	WebResourceInfo getResourceByUniqueName(String uniqueName) throws FileNotFoundException;

	/**
	 * Get the location (URL) of the specify resource.
	 * <p>
	 *     The location (return value) can print on the web page directly.
	 * </p>
	 *
	 * @param name
	 * @return
	 * @throws java.io.FileNotFoundException
	 */
	String getLocation(String name) throws FileNotFoundException;

	/**
	 * Get the location (URL) of the specify resource.
	 *
	 * @param webResourceInfo
	 * @return
	 */
	String getLocation(WebResourceInfo webResourceInfo);
}
