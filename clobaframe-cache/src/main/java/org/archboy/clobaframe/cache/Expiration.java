/*
 * Copyright 2011 Spark Young (sparkyoungs@gmail.com). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.archboy.clobaframe.cache;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * The expiration of a cache item.
 *
 * @author young
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
		return new Expiration(milliseconds);
	}

	/**
	 *
	 * @param seconds
	 * @return
	 */
	public static Expiration bySeconds(int seconds) {
		return new Expiration(seconds * 1000L);
	}

	/**
	 *
	 * @param expirationTime
	 * @return
	 */
	public static Expiration onDate(Date expirationTime) {
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
