package org.archboy.clobaframe.webresource.impl;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.IOUtils;
import org.archboy.clobaframe.webresource.AbstractWebResourceInfo;
import org.archboy.clobaframe.webresource.CompressableResource;
import org.archboy.clobaframe.webresource.WebResourceInfo;
import org.archboy.clobaframe.webresource.WebResourceManager;
import org.springframework.util.Assert;

/**
 * Minify resource.
 *
 * @author yang
 */
public class MinifyWebResourceInfo extends AbstractWebResourceInfo {

	private WebResourceInfo webResourceInfo;
	private String lastContentHash;
	private byte[] content;


	public MinifyWebResourceInfo(WebResourceInfo webResourceInfo) throws IOException{
		Assert.notNull(webResourceInfo);
		this.webResourceInfo = webResourceInfo;
		addUnderlayWebResource(webResourceInfo);
		rebuild();
	}

	@Override
	public String getContentHash() {
		return webResourceInfo.getContentHash();
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

	private void rebuild() throws IOException {

		if (webResourceInfo.getContentHash().equals(lastContentHash)){
			// resource does not changed
			return;
		}

		this.lastContentHash = webResourceInfo.getContentHash();

		InputStream in = null;
		ByteArrayOutputStream out = null;

		try{
			in = webResourceInfo.getContent();
			out = new ByteArrayOutputStream();
			Reader reader = new InputStreamReader(in, Charset.forName("UTF-8"));
			Writer writer = new OutputStreamWriter(out, Charset.forName("UTF-8"));
					
			if (webResourceInfo.getMimeType().equals(WebResourceManager.MIME_TYPE_STYLE_SHEET)){
				// style sheet
				CssCompressor compressor = new CssCompressor(reader);
				compressor.compress(writer, 1024);
			}else if(WebResourceManager.MIME_TYPE_JAVA_SCRIPT.contains(webResourceInfo.getMimeType())){
				// javascript
				JavaScriptCompressor compressor = new JavaScriptCompressor(reader, null);
				compressor.compress(writer, 1024, true, false, false, true);
				
			}else{
				throw new IllegalArgumentException(
						String.format(
								"Can not minify this mime type [%s]", 
								webResourceInfo.getMimeType()));
			}
			
			writer.flush();
			
		} finally{
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}

		this.content = out.toByteArray();
	}

}
