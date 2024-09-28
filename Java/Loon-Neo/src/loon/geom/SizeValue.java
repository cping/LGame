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

import loon.utils.reply.TValue;

public class SizeValue extends TValue<String> {

	private static final String PIXEL = "px";

	private static final String PERCENT = "%";

	private static final String WIDTH_SUFFIX = "w";

	private static final String HEIGHT_SUFFIX = "h";

	private static final String WILDCARD = "*";

	private static final float MAX_PERCENT = 100.0f;

	private final float _percentValue;

	private final float _pixelValue;

	private boolean _hasWidthSuffix;

	private boolean _hasHeightSuffix;

	public SizeValue(final int size) {
		this(size + PIXEL);
	}

	public SizeValue(final String valueParam) {
		super(valueParam);
		if (valueParam != null) {
			if (valueParam.endsWith(PERCENT + WIDTH_SUFFIX)) {
				_hasWidthSuffix = true;
				this._value = valueParam.substring(0, valueParam.length() - 1);
			} else if (valueParam.endsWith(PERCENT + HEIGHT_SUFFIX)) {
				_hasHeightSuffix = true;
				this._value = valueParam.substring(0, valueParam.length() - 1);
			} else {
				this._value = valueParam;
			}
		} else {
			this._value = valueParam;
		}
		this._percentValue = getPercentValue();
		this._pixelValue = getPixelValue();
	}

	public boolean isPercentOrPixel() {
		return isPercent() || isPixel();
	}

	public float getValue(final float range) {
		if (isPercent()) {
			return (range / MAX_PERCENT) * _percentValue;
		} else if (isPixel()) {
			return _pixelValue;
		} else {
			return -1;
		}
	}

	public int getValueAsInt(final float range) {
		return (int) getValue(range);
	}

	private float getPercentValue() {
		if (isPercent()) {
			String percent = _value.substring(0, _value.length() - PERCENT.length());
			return Float.parseFloat(percent);
		} else {
			return 0;
		}
	}

	private int getPixelValue() {
		if (isPixel()) {
			if (hasNoSuffix()) {
				return Integer.parseInt(_value);
			}
			String pixel = _value.substring(0, _value.length() - PIXEL.length());
			return Integer.parseInt(pixel);
		} else {
			return 0;
		}
	}

	private boolean isPercent() {
		if (_value == null) {
			return false;
		} else {
			return _value.endsWith(PERCENT);
		}
	}

	public boolean isPixel() {
		if (_value == null) {
			return false;
		} else {
			return !_value.equals(WILDCARD) && (_value.endsWith(PIXEL) || hasNoSuffix());
		}
	}

	private boolean hasNoSuffix() {
		if (_value == null) {
			return false;
		}

		if (_value.endsWith(PIXEL) || _value.endsWith(PERCENT) || _value.endsWith(WIDTH_SUFFIX)
				|| _value.endsWith(HEIGHT_SUFFIX)) {
			return false;
		}
		return true;
	}

	public boolean hasWidthSuffix() {
		return _hasWidthSuffix;
	}

	public boolean hasHeightSuffix() {
		return _hasHeightSuffix;
	}

	public boolean hasWildcard() {
		return WILDCARD.equals(_value);
	}

	@Override
	public SizeValue cpy() {
		return new SizeValue(_value);
	}
}
