/**
 * Copyright 2013 The Loon Authors
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
 */
package loon.foundation;

import loon.utils.MathUtils;
import loon.utils.StringUtils;

public class NSNumber extends NSObject {

	public static final int INTEGER = 0;

	public static final int REAL = 1;

	public static final int BOOLEAN = 2;

	private int type;

	private long longValue;

	private double doubleValue;

	private boolean boolValue;

	protected static final long parseUnsignedInt(byte[] bytes) {
		long l = 0;
		for (byte b : bytes) {
			l <<= 8;
			l |= b & 0xFF;
		}
		l &= 0xFFFFFFFFL;
		return l;
	}

	protected static final long parseLong(byte[] bytes) {
		long l = 0;
		for (byte b : bytes) {
			l <<= 8;
			l |= b & 0xFF;
		}
		return l;
	}

	protected static final double parseDouble(byte[] bytes) {
		if (bytes.length != 8) {
			throw new IllegalArgumentException("bad byte array length "
					+ bytes.length);
		}
		return Double.longBitsToDouble(parseLong(bytes));
	}

	public NSNumber(byte[] bytes, int type) {
		switch (type) {
		case INTEGER: {
			doubleValue = longValue = parseLong(bytes);
			break;
		}
		case REAL: {
			doubleValue = parseDouble(bytes);
			longValue = (long) doubleValue;
			break;
		}
		default: {
			throw new IllegalArgumentException("Type argument is not valid.");
		}
		}
		this.type = type;
	}

	public NSNumber(String text) {
		if (text.equalsIgnoreCase("yes") || text.equalsIgnoreCase("true")) {
			boolValue = true;
			doubleValue = longValue = 1;
			type = BOOLEAN;
			return;
		} else if (text.equalsIgnoreCase("no")
				|| text.equalsIgnoreCase("false")) {
			boolValue = false;
			doubleValue = longValue = 0;
			type = BOOLEAN;
			return;
		}
		if (!MathUtils.isNan(text)) {
			throw new IllegalArgumentException("[" + text
					+ "] value must be a boolean or numeric !");
		}
		if (StringUtils.isAlphabetNumeric(text) && text.indexOf('.') == -1) {
			long l = Long.parseLong(text);
			doubleValue = longValue = l;
			type = INTEGER;
		} else if (StringUtils.isAlphabetNumeric(text)
				&& text.indexOf('.') != -1) {
			double d = Double.parseDouble(text);
			longValue = (long) (doubleValue = d);
			type = REAL;
		} else {
			try {
				long l = Long.parseLong(text);
				doubleValue = longValue = l;
				type = INTEGER;
			} catch (Exception e1) {
				try {
					double d = Double.parseDouble(text);
					longValue = (long) (doubleValue = d);
					type = REAL;
				} catch (Exception e2) {
					try {
						boolValue = Boolean.parseBoolean(text);
						doubleValue = longValue = boolValue ? 1 : 0;
					} catch (Exception e3) {
						throw new IllegalArgumentException(
								"Given text neither represents a double, int nor boolean value.");
					}
				}
			}
		}
	}

	public NSNumber(int i) {
		type = INTEGER;
		doubleValue = longValue = i;
	}

	public NSNumber(double d) {
		longValue = (long) (doubleValue = d);
		type = REAL;
	}

	public NSNumber(boolean b) {
		boolValue = b;
		doubleValue = longValue = b ? 1 : 0;
		type = BOOLEAN;
	}

	public int type() {
		return type;
	}

	public boolean booleanValue() {
		if (type == BOOLEAN) {
			return boolValue;
		} else {
			return longValue != 0;
		}
	}

	public long longValue() {
		return longValue;
	}

	public int intValue() {
		return (int) longValue;
	}

	public double doubleValue() {
		return doubleValue;
	}

	@Override
	public boolean equals(Object obj) {
		return obj.getClass().equals(NSNumber.class)
				&& obj.hashCode() == hashCode();
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 37 * hash + (int) (this.longValue ^ (this.longValue >>> 32));
		hash = 37
				* hash
				+ (int) (Double.doubleToLongBits(this.doubleValue) ^ (Double
						.doubleToLongBits(this.doubleValue) >>> 32));
		hash = 37 * hash + (booleanValue() ? 1 : 0);
		return hash;
	}

	@Override
	public String toString() {
		switch (type) {
		case INTEGER:
			return String.valueOf(longValue());
		case REAL:
			return String.valueOf(doubleValue());
		case BOOLEAN:
			return String.valueOf(booleanValue());
		default:
			return super.toString();
		}
	}

	@Override
	protected void addSequence(StringBuilder sbr, String indent) {
		sbr.append(indent);
		switch (type) {
		case INTEGER: {
			sbr.append("<integer>");
			sbr.append(String.valueOf(longValue));
			sbr.append("</integer>");
			return;
		}
		case REAL: {
			sbr.append("<real>");
			sbr.append(String.valueOf(doubleValue));
			sbr.append("</real>");
			return;
		}
		case BOOLEAN:
			if (boolValue) {
				sbr.append("<true/>");
				return;
			} else {
				sbr.append("<false/>");
				return;
			}
		default:
			return;
		}
	}
}
