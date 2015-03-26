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
import org.archboy.clobaframe.webresource.AbstractWebResourceInfo;
import org.archboy.clobaframe.webresource.LocationTransformResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceManager;
import org.springframework.util.Assert;

/**
 * Replace "url('...')" in the style sheet with the
 * actually resource location.
 *
 * @author yang
 */
public class LocationTransformWebResourceInfo extends AbstractWebResourceInfo implements LocationTransformResource {

	private final Logger logger = LoggerFactory.getLogger(LocationTransformWebResourceInfo.class);

	private WebResourceManager webResourceManager;
	private WebResourceInfo webResourceInfo;
	//private Map<String, String> locations;
	
	private String lastContentHash; // the underlay content hash
	
	//private long lastModified;
	private byte[] content;
	private String contentHash;

	private Collection<String> referenceResourceNames;
	
	private static final String resourceNameRegex = "([\\/\\w\\.-]+)([^'\"\\)]*)";

	//private static final String placeHoldRegex = "\\[\\[" + resourceNameRegex + "\\]\\]";
	//private static final Pattern placeHoldPattern = Pattern.compile(placeHoldRegex);

	private static final String urlRegex = "url\\(['|\"]?" + resourceNameRegex + "['|\"]?\\)";
	private static final Pattern urlPattern = Pattern.compile(urlRegex);

	//private boolean autoConvertCssUrl;

	public LocationTransformWebResourceInfo(
			WebResourceManager webResourceManager,
			WebResourceInfo webResourceInfo) throws IOException{ //,
			//Map<String, String> locations, boolean autoConvertCssUrl) {

		Assert.notNull(webResourceManager);
		Assert.notNull(webResourceInfo);
		
		this.webResourceManager = webResourceManager;
		this.webResourceInfo = webResourceInfo;
//		this.locations = locations;
//		this.autoConvertCssUrl = autoConvertCssUrl;
		
		addUnderlayWebResource(webResourceInfo);
		
		rebuild();
	}

	@Override
	public String getContentHash() {
		try{
			rebuild();
		}catch(IOException e){
			// ignore
		}
		return contentHash;
	}

	@Override
	public long getContentLength() {
		try{
			rebuild();
		}catch(IOException e){
			// ignore
		}
		return content.length;
	}

//	@Override
//	public String getUniqueName() {
//		return webResourceInfo.getUniqueName();
//	}

	@Override
	public String getMimeType() {
		return webResourceInfo.getMimeType();
	}

	@Override
	public String getName() {
		return webResourceInfo.getName();
	}

	@Override
	public Date getLastModified() {
		return webResourceInfo.getLastModified();
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

//	private void refresh(){
//		buildSnapshot();
//	}

	private void rebuild() throws IOException {

		if (webResourceInfo.getContentHash().equals(lastContentHash)){
			// resource does not changed
			return;
		}

		lastContentHash = webResourceInfo.getContentHash();

		referenceResourceNames = new ArrayList<String>();
		
		String text = null;
		//ResourceContent resourceContent = null;
		InputStream in = null;

		try{
//			resourceContent = webResourceInfo.getContentSnapshot();
//			InputStream in = resourceContent.getContent();
			in = webResourceInfo.getContent();

			InputStreamReader reader = new InputStreamReader(in, "utf-8");
			text = IOUtils.toString(reader);

			// replace the css 'url' location.
//			if (autoConvertCssUrl) {
//				text = replaceLocation(urlPattern, text);
//			}

			// replace the location placehold.
			text = replaceLocation(text);
		} finally{
			IOUtils.closeQuietly(in);
		}

		// convert text into input stream
		content = text.getBytes(Charset.forName("utf-8"));
		contentHash = DigestUtils.sha256Hex(content);
	}

	/**
	 * transform "url(...)" locations
	 * @param text
	 * @return
	 */
	private String replaceLocation(String text) throws FileNotFoundException {
		StringBuffer builder = new StringBuffer();
		Matcher matcher = urlPattern.matcher(text);
		while(matcher.find()){
			String name = matcher.group(1);
			String canonicalName = getCanonicalName(name);

			referenceResourceNames.add(canonicalName);
			
			String location = webResourceManager.getLocation(canonicalName);
			//if (location != null){

//				if (pattern == urlPattern) {
					String group = matcher.group();
					String result= group.replace(name, location);

					matcher.appendReplacement(builder, result);
//				}else{
//					matcher.appendReplacement(builder, location);
//				}
//			}else{
//				// the specify resource can not be found.
//				matcher.appendReplacement(builder, matcher.group());
//			}
		}

		matcher.appendTail(builder);
		String replacedText = builder.toString();

		return replacedText;
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

		String currentName = webResourceInfo.getName();
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
	public Collection<String> getReferenceResourceNames() {
		return referenceResourceNames;
	}

}
