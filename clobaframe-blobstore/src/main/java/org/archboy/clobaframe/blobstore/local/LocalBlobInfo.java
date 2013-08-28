package org.archboy.clobaframe.blobstore.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import org.archboy.clobaframe.blobstore.BlobInfo;
import org.archboy.clobaframe.blobstore.BlobKey;
import org.archboy.clobaframe.io.ResourceContent;
import org.archboy.clobaframe.io.impl.DefaultResourceContent;
import org.archboy.clobaframe.io.impl.PartialFileInputStream;

/**
 *
 * @author arch
 */
public class LocalBlobInfo implements BlobInfo{

	private BlobKey blobKey;
	private File file;
	private String contentType;

	public LocalBlobInfo(BlobKey blobKey, File file, String contentType) {
		this.blobKey = blobKey;
		this.file = file;
		this.contentType = contentType;
	}

	@Override
	public BlobKey getBlobKey() {
		return blobKey;
	}

	@Override
	public long getContentLength() {
		return file.length();
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public Date getLastModified() {
		return new Date(file.lastModified());
	}

	@Override
	public ResourceContent getContentSnapshot() throws IOException{
		long length = file.length();
		InputStream in = new FileInputStream(file);
		return new DefaultResourceContent(in, length);
	}

	@Override
	public ResourceContent getContentSnapshot(long start, long length) throws IOException{
		InputStream in = new PartialFileInputStream(file, start, length);
		return new DefaultResourceContent(in, length);
//		long length = end - start + 1;
//		RandomAccessFile in = new RandomAccessFile(file, "r");
//
//		long remains = length;
//		int bufferSize = 256 * 1024;
//		byte[] buffer = new byte[bufferSize];
//
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		in.seek(start);
//		while(remains > 0){
//			int count = (int)(remains > bufferSize ? bufferSize : remains);
//			int read = in.read(buffer, 0, count);
//			if (read <= 0 ){
//				IOUtils.closeQuietly(in);
//				throw new IOException("Out of range.");
//			}
//			out.write(buffer, 0, read);
//			remains-=read;
//		}
//
//		byte[] data = out.toByteArray();
//		in.close();
//		out.close();
//
//		return new DefaultBlobContent(length, new ByteArrayInputStream(data));
	}

	@Override
	public Map<String, String> getMetadata() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void addMetadata(String key, String value) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isContentSeekable() {
		return true;
	}
}
