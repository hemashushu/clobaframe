package org.archboy.clobaframe.webresource.impl;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.archboy.clobaframe.io.NamedResourceInfo;
import org.archboy.clobaframe.webresource.AbstractWrapperResourceInfo;
import org.archboy.clobaframe.webresource.LocationTransformResourceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.archboy.clobaframe.webresource.ContentHashResourceInfo;
import org.archboy.clobaframe.webresource.ResourceManager;
import org.springframework.util.Assert;

/**
 * Replace "url('...')" in the style sheet with the
 * actually resource location.
 *
 * @author yang
 */
public class DefaultLocationTransformResourceInfo extends AbstractWrapperResourceInfo implements LocationTransformResourceInfo {

	private final Logger logger = LoggerFactory.getLogger(DefaultLocationTransformResourceInfo.class);

	private ResourceManager resourceManager;
	private NamedResourceInfo resourceInfo;
	
	//private String lastContentHash; // the underlay content hash
	
	private byte[] content;
	private String contentHash;

	private Collection<String> childResourceNames = new ArrayList<String>();
	
	private static final String resourceNameRegex = "([\\/\\w\\.-]+)([^'\"\\)]*)";

	private static final String urlRegex = "url\\(['|\"]?" + resourceNameRegex + "['|\"]?\\)";
	private static final Pattern urlPattern = Pattern.compile(urlRegex);

	public DefaultLocationTransformResourceInfo(
			ResourceManager resourceManager,
			NamedResourceInfo resourceInfo) {

		Assert.notNull(resourceManager);
		Assert.notNull(resourceInfo);
		
		this.resourceManager = resourceManager;
		this.resourceInfo = resourceInfo;
		
		appendType(getType(), resourceInfo);
		
		rebuild();
	}

	@Override
	public int getType() {
		return TYPE_LOCATION_TRANSFORM;
	}
	
	@Override
	public String getContentHash() {
		rebuild();
		return contentHash;
	}

	@Override
	public long getContentLength() {
		rebuild();
		return content.length;
	}

	@Override
	public String getMimeType() {
		return resourceInfo.getMimeType();
	}

	@Override
	public String getName() {
		return resourceInfo.getName();
	}

	@Override
	public Date getLastModified() {
		return resourceInfo.getLastModified();
	}

	@Override
	public InputStream getContent() throws IOException {
		rebuild();
		return new ByteArrayInputStream(content);
	}

	@Override
	public InputStream getContent(long start, long length) throws IOException {
		rebuild();
		return new ByteArrayInputStream(
				content, (int)start, (int)length);
	}

	@Override
	public boolean isSeekable() {
		return true;
	}

	private void rebuild() {

//		if (webResourceInfo.getContentHash().equals(lastContentHash)){
//			// resource does not changed
//			return;
//		}
//
//		lastContentHash = webResourceInfo.getContentHash();

		childResourceNames.clear();
		
		String text = null;
		//ResourceContent resourceContent = null;
		InputStream in = null;

		try{
			in = resourceInfo.getContent();

			InputStreamReader reader = new InputStreamReader(in, "utf-8");
			text = IOUtils.toString(reader);
			text = replaceLocation(text);
			
			// convert text into input stream
			content = text.getBytes(Charset.forName("utf-8"));
			contentHash = DigestUtils.sha256Hex(content);
		
		}catch(IOException e){
			// ignore
		} finally{
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * transform "url(...)" locations
	 * 
	 * @param text
	 * @return
	 */
	private String replaceLocation(String text) {
		StringBuffer builder = new StringBuffer();
		Matcher matcher = urlPattern.matcher(text);
		while(matcher.find()){
			String name = matcher.group(1);
			String result = null;
			
			try{
				String canonicalName = getCanonicalName(name);
				String location = resourceManager.getLocation(canonicalName);
				
				if (StringUtils.isNotEmpty(matcher.group(2))){
					location = appendQueryString(location, matcher.group(2));
				}
				
//				String group = matcher.group();
//				result= group.replace(name, location);
				
				result = String.format("url('%s')", location);
				childResourceNames.add(canonicalName);
			}catch(FileNotFoundException e){
				// ignore
				result = name;
			}
			
			matcher.appendReplacement(builder, result);
		}

		matcher.appendTail(builder);
		String replacedText = builder.toString();

		return replacedText;
	}

	private String appendQueryString(String location, String query) {
		
		/**
		 * The name maybe includes the query and url hash.
		 * E.g.
		 * 
		 * webfont.eot?v=4.2.0
		 * webfont.eot?#iefix
		 * webfont.eot?#iefix&v=4.2.0
		 * webfont.eot?v=4.2.0#iefix
		 * 
		 */
		
		if (query.charAt(0) == '?'){
			return location + "&" + query.substring(1);
		}else{
			return location + query;
		}
		
//		StringBuilder builder = new StringBuilder();
//	
//		int queryPos = name.lastIndexOf('?');
//		if (queryPos == -1) {
//			//name += "?" + shortHash;
//			builder.append(name);
//			builder.append(concatMark);
//			builder.append(shortContentHash);
//		}else{
//			if (queryPos == name.length() - 1){
//				builder.append(name.substring(0, queryPos));
//				builder.append(concatMark);
//				builder.append(shortContentHash);
//			}else{
//				if (name.charAt(queryPos + 1) == '#') { // for css font-family ie-fix, e.g. "webfont.eot?#iefix"
//					builder.append(name.substring(0, queryPos));
//					builder.append(concatMark);
//					builder.append(shortContentHash);
//					builder.append(name.substring(queryPos + 1));
//				}else{
//					builder.append(name.substring(0, queryPos));
//					builder.append(concatMark);
//					builder.append(shortContentHash);
//					builder.append("&");
//					builder.append(name.substring(queryPos + 1));
//				}
//			}
//		}
//		return builder.toString();
	}
	
	/**
	 * Remove the "/", "./" and "../"
	 * @param pathName the target path name
	 * @return
	 */
	private String getCanonicalName(String pathName) throws FileNotFoundException {
		
		// remove "/"
		if (pathName.startsWith("/")){
			pathName = pathName.substring(1);
		}

		// remove "./"
		if (pathName.startsWith("./")){
			pathName = pathName.substring(2);
		}

		String currentName = resourceInfo.getName();
		int pathIdx = currentName.lastIndexOf('/');
		String currentPath = pathIdx < 0 ? "":currentName.substring(0, pathIdx);

		// remove "../" and back-forward the current path.
		while(pathName.startsWith("../")){
			if (currentPath.equals("")){
				// has already reach the top of current path
				throw new FileNotFoundException(pathName);
			}

			pathName = pathName.substring(3);
			pathIdx = currentPath.lastIndexOf('/');
			currentPath = pathIdx < 0 ? "":currentPath.substring(0, pathIdx);
		}

		return currentPath.equals("") ? pathName : currentPath + "/" + pathName;

	}

	@Override
	public Collection<String> listChildResourceNames() {
		return childResourceNames;
	}

}