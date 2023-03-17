/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.geom;

public class SizeValue {

	private static final String PIXEL = "px";

	private static final String PERCENT = "%";

	private static final String WIDTH_SUFFIX = "w";

	private static final String HEIGHT_SUFFIX = "h";

	private static final String WILDCARD = "*";

	private static final float MAX_PERCENT = 100.0f;

	private final String value;

	private final float percentValue;

	private final float pixelValue;

	private boolean hasWidthSuffix;

	private boolean hasHeightSuffix;

	public SizeValue(final int size) {
		this(size + PIXEL);
	}

	public SizeValue(final String valueParam) {
		if (valueParam != null) {
			if (valueParam.endsWith(PERCENT + WIDTH_SUFFIX)) {
				hasWidthSuffix = true;
				this.value = valueParam.substring(0, valueParam.length() - 1);
			} else if (valueParam.endsWith(PERCENT + HEIGHT_SUFFIX)) {
				hasHeightSuffix = true;
				this.value = valueParam.substring(0, valueParam.length() - 1);
			} else {
				this.value = valueParam;
			}
		} else {
			this.value = valueParam;
		}
		this.percentValue = getPercentValue();
		this.pixelValue = getPixelValue();
	}

	public boolean isPercentOrPixel() {
		return isPercent() || isPixel();
	}

	public float getValue(final float range) {
		if (isPercent()) {
			return (range / MAX_PERCENT) * percentValue;
		} else if (isPixel()) {
			return pixelValue;
		} else {
			return -1;
		}
	}

	public int getValueAsInt(final float range) {
		return (int) getValue(range);
	}

	private float getPercentValue() {
		if (isPercent()) {
			String percent = value.substring(0,
					value.length() - PERCENT.length());
			return Float.parseFloat(percent);
		} else {
			return 0;
		}
	}

	private int getPixelValue() {
		if (isPixel()) {
			if (hasNoSuffix()) {
				return Integer.parseInt(value);
			}
			String pixel = value.substring(0, value.length() - PIXEL.length());
			return Integer.parseInt(pixel);
		} else {
			return 0;
		}
	}

	private boolean isPercent() {
		if (value == null) {
			return false;
		} else {
			return value.endsWith(PERCENT);
		}
	}

	public boolean isPixel() {
		if (value == null) {
			return false;
		} else {
			return !value.equals(WILDCARD)
					&& (value.endsWith(PIXEL) || hasNoSuffix());
		}
	}

	private boolean hasNoSuffix() {
		if (value == null) {
			return false;
		}

		if (value.endsWith(PIXEL) || value.endsWith(PERCENT)
				|| value.endsWith(WIDTH_SUFFIX)
				|| value.endsWith(HEIGHT_SUFFIX)) {
			return false;
		}
		return true;
	}

	public boolean hasWidthSuffix() {
		return hasWidthSuffix;
	}

	public boolean hasHeightSuffix() {
		return hasHeightSuffix;
	}

	public boolean hasWildcard() {
		return WILDCARD.equals(value);
	}

	@Override
	public String toString() {
		return value;
	}
}
