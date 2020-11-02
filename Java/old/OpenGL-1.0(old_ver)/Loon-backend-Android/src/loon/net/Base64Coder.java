package loon.net;

import loon.utils.StringUtils;

/**
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
/**
 * 自0.3.2版起新增类，用以统一跨平台的BASE64处理方法
 */
public class Base64Coder {

	private static final int BASELENGTH = 255;

	private static final int LOOKUPLENGTH = 64;

	private static final int TWENTYFOURBITGROUP = 24;

	private static final int EIGHTBIT = 8;

	private static final int SIXTEENBIT = 16;

	private static final int FOURBYTE = 4;

	private static final int SIGN = -128;

	private static final byte PAD = (byte) '=';

	private static byte[] BASE64_ALPHABET;

	private static byte[] LOOKUP_BASE64_ALPHABET;

	private Base64Coder() {

	}

	public static byte[] fromBinHexString(String s) {
		char[] chars = s.toCharArray();
		byte[] bytes = new byte[chars.length / 2 + chars.length % 2];
		fromBinHexString(chars, 0, chars.length, bytes);
		return bytes;
	}

	public static int fromBinHexString(char[] chars, int offset,
			int charLength, byte[] buffer) {
		int bufIndex = offset;
		for (int i = 0; i < charLength - 1; i += 2) {
			buffer[bufIndex] = (chars[i] > '9' ? (byte) (chars[i] - 'A' + 10)
					: (byte) (chars[i] - '0'));
			buffer[bufIndex] <<= 4;
			buffer[bufIndex] += chars[i + 1] > '9' ? (byte) (chars[i + 1] - 'A' + 10)
					: (byte) (chars[i + 1] - '0');
			bufIndex++;
		}
		if (charLength % 2 != 0)
			buffer[bufIndex++] = (byte) ((chars[charLength - 1] > '9' ? (byte) (chars[charLength - 1] - 'A' + 10)
					: (byte) (chars[charLength - 1] - '0')) << 4);

		return bufIndex - offset;
	}

	private final static void checking() {
		if (BASE64_ALPHABET == null) {
			BASE64_ALPHABET = new byte[BASELENGTH];
			for (int i = 0; i < BASELENGTH; i++) {
				BASE64_ALPHABET[i] = -1;
			}
			for (int i = 'Z'; i >= 'A'; i--) {
				BASE64_ALPHABET[i] = (byte) (i - 'A');
			}
			for (int i = 'z'; i >= 'a'; i--) {
				BASE64_ALPHABET[i] = (byte) (i - 'a' + 26);
			}

			for (int i = '9'; i >= '0'; i--) {
				BASE64_ALPHABET[i] = (byte) (i - '0' + 52);
			}

			BASE64_ALPHABET['+'] = 62;
			BASE64_ALPHABET['/'] = 63;
		}
		if (LOOKUP_BASE64_ALPHABET == null) {
			LOOKUP_BASE64_ALPHABET = new byte[LOOKUPLENGTH];
			for (int i = 0; i <= 25; i++) {
				LOOKUP_BASE64_ALPHABET[i] = (byte) ('A' + i);
			}

			for (int i = 26, j = 0; i <= 51; i++, j++) {
				LOOKUP_BASE64_ALPHABET[i] = (byte) ('a' + j);
			}

			for (int i = 52, j = 0; i <= 61; i++, j++) {
				LOOKUP_BASE64_ALPHABET[i] = (byte) ('0' + j);
			}
			LOOKUP_BASE64_ALPHABET[62] = (byte) '+';
			LOOKUP_BASE64_ALPHABET[63] = (byte) '/';
		}
	}

	public static boolean isBase64(String v) {
		return isArrayByteBase64(StringUtils.getAsciiBytes(v));
	}

	public static boolean isArrayByteBase64(byte[] bytes) {
		checking();
		int length = bytes.length;
		if (length == 0) {
			return true;
		}
		for (int i = 0; i < length; i++) {
			if (!Base64Coder.isBase64(bytes[i])) {
				return false;
			}
		}
		return true;
	}

	private static boolean isBase64(byte octect) {
		return (octect == PAD || BASE64_ALPHABET[octect] != -1);
	}

	public static byte[] encode(byte[] binaryData) {
		checking();
		int lengthDataBits = binaryData.length * EIGHTBIT;
		int fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP;
		int numberTriplets = lengthDataBits / TWENTYFOURBITGROUP;
		byte encodedData[] = null;

		if (fewerThan24bits != 0) {
			encodedData = new byte[(numberTriplets + 1) * 4];
		} else {
			encodedData = new byte[numberTriplets * 4];
		}

		byte k = 0;
		byte l = 0;
		byte b1 = 0;
		byte b2 = 0;
		byte b3 = 0;
		int encodedIndex = 0;
		int dataIndex = 0;
		int i = 0;
		for (i = 0; i < numberTriplets; i++) {

			dataIndex = i * 3;
			b1 = binaryData[dataIndex];
			b2 = binaryData[dataIndex + 1];
			b3 = binaryData[dataIndex + 2];

			l = (byte) (b2 & 0x0f);
			k = (byte) (b1 & 0x03);

			encodedIndex = i * 4;
			byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
					: (byte) ((b1) >> 2 ^ 0xc0);

			byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4)
					: (byte) ((b2) >> 4 ^ 0xf0);
			byte val3 = ((b3 & SIGN) == 0) ? (byte) (b3 >> 6)
					: (byte) ((b3) >> 6 ^ 0xfc);

			encodedData[encodedIndex] = LOOKUP_BASE64_ALPHABET[val1];
			encodedData[encodedIndex + 1] = LOOKUP_BASE64_ALPHABET[val2
					| (k << 4)];
			encodedData[encodedIndex + 2] = LOOKUP_BASE64_ALPHABET[(l << 2)
					| val3];
			encodedData[encodedIndex + 3] = LOOKUP_BASE64_ALPHABET[b3 & 0x3f];
		}

		dataIndex = i * 3;
		encodedIndex = i * 4;
		if (fewerThan24bits == EIGHTBIT) {
			b1 = binaryData[dataIndex];
			k = (byte) (b1 & 0x03);
			byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
					: (byte) ((b1) >> 2 ^ 0xc0);
			encodedData[encodedIndex] = LOOKUP_BASE64_ALPHABET[val1];
			encodedData[encodedIndex + 1] = LOOKUP_BASE64_ALPHABET[k << 4];
			encodedData[encodedIndex + 2] = PAD;
			encodedData[encodedIndex + 3] = PAD;
		} else if (fewerThan24bits == SIXTEENBIT) {
			b1 = binaryData[dataIndex];
			b2 = binaryData[dataIndex + 1];
			l = (byte) (b2 & 0x0f);
			k = (byte) (b1 & 0x03);

			byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
					: (byte) ((b1) >> 2 ^ 0xc0);
			byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4)
					: (byte) ((b2) >> 4 ^ 0xf0);

			encodedData[encodedIndex] = LOOKUP_BASE64_ALPHABET[val1];
			encodedData[encodedIndex + 1] = LOOKUP_BASE64_ALPHABET[val2
					| (k << 4)];
			encodedData[encodedIndex + 2] = LOOKUP_BASE64_ALPHABET[l << 2];
			encodedData[encodedIndex + 3] = PAD;
		}
		return encodedData;
	}

	public static byte[] decode(byte[] base64Data) {
		checking();
		if (base64Data.length == 0) {
			return new byte[0];
		}

		int numberQuadruple = base64Data.length / FOURBYTE;
		byte decodedData[] = null;
		byte b1 = 0, b2 = 0, b3 = 0, b4 = 0, marker0 = 0, marker1 = 0;

		int encodedIndex = 0;
		int dataIndex = 0;
		{
			int lastData = base64Data.length;
			while (base64Data[lastData - 1] == PAD) {
				if (--lastData == 0) {
					return new byte[0];
				}
			}
			decodedData = new byte[lastData - numberQuadruple];
		}

		for (int i = 0; i < numberQuadruple; i++) {
			dataIndex = i * 4;
			marker0 = base64Data[dataIndex + 2];
			marker1 = base64Data[dataIndex + 3];

			b1 = BASE64_ALPHABET[base64Data[dataIndex]];
			b2 = BASE64_ALPHABET[base64Data[dataIndex + 1]];

			if (marker0 != PAD && marker1 != PAD) {
				b3 = BASE64_ALPHABET[marker0];
				b4 = BASE64_ALPHABET[marker1];

				decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
				decodedData[encodedIndex + 1] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
				decodedData[encodedIndex + 2] = (byte) (b3 << 6 | b4);
			} else if (marker0 == PAD) {
				decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
			} else if (marker1 == PAD) {
				b3 = BASE64_ALPHABET[marker0];
				decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
				decodedData[encodedIndex + 1] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
			}
			encodedIndex += 3;
		}
		return decodedData;
	}

	public static byte[] decodeBase64(char[] data) {
		checking();

		int size = data.length;
		int temp = size;

		for (int ix = 0; ix < data.length; ix++) {
			if ((data[ix] > 255) || BASE64_ALPHABET[data[ix]] < 0) {
				--temp;
			}
		}

		int len = (temp / 4) * 3;
		if ((temp % 4) == 3) {
			len += 2;
		}
		if ((temp % 4) == 2) {
			len += 1;
		}
		byte[] out = new byte[len];

		int shift = 0;
		int accum = 0;
		int index = 0;

		for (int ix = 0; ix < size; ix++) {
			int value = (data[ix] > 255) ? -1 : BASE64_ALPHABET[data[ix]];

			if (value >= 0) {
				accum <<= 6;
				shift += 6;
				accum |= value;
				if (shift >= 8) {
					shift -= 8;
					out[index++] = (byte) ((accum >> shift) & 0xff);
				}
			}
		}

		if (index != out.length) {
			throw new RuntimeException("index != " + out.length);
		}

		return out;
	}
}
