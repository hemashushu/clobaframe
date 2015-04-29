package org.archboy.clobaframe.media.image;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author yang
 *
 */
public class OutputSettings {

	// default encoding is PNG
	private OutputEncoding outputEncoding = OutputEncoding.PNG;

	// default not set quality
	private int quality = -1;

	public OutputSettings() {
		//
	}

	public OutputSettings(OutputEncoding outputEncoding){
		this.outputEncoding = outputEncoding;
	}

	public OutputSettings(OutputEncoding outputEncoding, int quality) {
		if(quality <1 || quality >100){
			throw new IllegalArgumentException("quality");
		}
		this.quality = quality;

		this.outputEncoding = outputEncoding;
	}

	public OutputEncoding getOutputEncoding() {
		return outputEncoding;
	}

	public void setOutputEncoding(OutputEncoding outputEncoding) {
		this.outputEncoding = outputEncoding;
	}

	/**
	 * Checks if the quality value has been set.
	 *
	 * @return
	 */
	public boolean hasQuality() {
		return (quality != -1);
	}

	/**
	 *
	 * @return Return -1 if the quality not set.
	 */
	public int getQuality() {
		return quality;
	}

	/**
	 *
	 * @param quality Must between 1 and 100.
	 */
	public void setQuality(int quality) {
		if(quality <1 || quality >100){
			throw new IllegalArgumentException("quality");
		}
		this.quality = quality;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(getOutputEncoding())
				.append(getQuality())
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof OutputSettings)) {
			return false;
		}

		OutputSettings other = (OutputSettings) obj;
		return new EqualsBuilder()
				.append(getOutputEncoding(), other.getOutputEncoding())
				.append(getQuality(), other.getQuality())
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("outputEncoding", getOutputEncoding())
				.append("quality", getQuality())
				.toString();
	}

	public static enum OutputEncoding {
		PNG, JPEG
	}
}
