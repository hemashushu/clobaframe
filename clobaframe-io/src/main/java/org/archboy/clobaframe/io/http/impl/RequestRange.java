package org.archboy.clobaframe.io.http.impl;

/**
 * Parse HTTP header 'Range: bytes=xx-yy' into starting and ending value.
 *
 * The startPosition and endPosition are include.
 * See http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
 * @author yang
 *
 */
public class RequestRange {
	private long startPosition;
	private long endPosition;
	private long length; // the request length

	public RequestRange(long startPosition, long endPosition) {
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.length = endPosition - startPosition + 1;
	}

	public RequestRange(String range, long contentLength) {
		// set the default position first.
		this.startPosition = 0;
		this.endPosition = contentLength - 1;

		if (range.indexOf(',') >= 0) {
			throw new UnsupportedOperationException(
					"Does not support multipart/byterange");
		}

		int pos1 = range.indexOf("bytes=");
		if (pos1 == 0) {
			int equalsPos = "bytes=".length();
			int pos2 = range.indexOf("-", equalsPos);
			if (pos2 > equalsPos) {
				// maybe "100-" or "100-200"
				this.startPosition = Long.parseLong(range.substring(equalsPos,
						pos2));
				if (pos2 < range.length() - 1) {
					// must be "100-200"
					this.endPosition = Long
							.parseLong(range.substring(pos2 + 1));
				}
			} else if (pos2 == equalsPos) {
				// must be "-100"
				long tailingLength = Long.parseLong(range.substring(pos2 + 1));
				if (tailingLength < contentLength) {
					this.startPosition = contentLength - tailingLength;
				}
			}
		}

		this.length = endPosition - startPosition + 1;
	}

	public long getStartPosition() {
		return startPosition;
	}

	public long getEndPosition() {
		return endPosition;
	}

	public long getLength() {
		//The request length.
		return length;
	}

}
