package org.archboy.clobaframe.cache;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.Assert;

/**
 * The expiration of a cache item.
 *
 * @author yang
 *
 */
public class Expiration {

	private long milliseconds;

	private Expiration(long milliseconds) {
		this.milliseconds = milliseconds;
	}

	/**
	 *
	 * @param milliseconds
	 * @return
	 */
	public static Expiration byMilliseconds(int milliseconds) {
		Assert.isTrue(milliseconds > 0);
		
		return new Expiration(milliseconds);
	}

	/**
	 *
	 * @param seconds
	 * @return
	 */
	public static Expiration bySeconds(int seconds) {
		Assert.isTrue(seconds > 0);
		
		return new Expiration(seconds * 1000L);
	}

	/**
	 *
	 * @param expirationTime
	 * @return
	 */
	public static Expiration onDate(Date expirationTime) {
		Assert.notNull(expirationTime);
		
		Date now = new Date();
		long span = expirationTime.getTime() - now.getTime();
		return new Expiration(span);
	}

	/**
	 *
	 * @return
	 */
	public long getMilliseconds() {
		return milliseconds;
	}

	/**
	 *
	 * @return
	 */
	public int getSeconds() {
		int seconds = Math.round(milliseconds / 1000F);
		return seconds;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}

		if (!(obj instanceof Expiration)) {
			return false;
		}

		Expiration other = (Expiration) obj;
		return new EqualsBuilder()
				.append(getMilliseconds(), other.getMilliseconds())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(getMilliseconds())
				.toHashCode();
	}

}
