using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Utils;

namespace Loon.Net
{
    public class Base64Coder
    {
        
	
		private const int BASELENGTH = 255;
	
		private const int LOOKUPLENGTH = 64;
	
		private const int TWENTYFOURBITGROUP = 24;
	
		private const int EIGHTBIT = 8;
	
		private const int SIXTEENBIT = 16;
	
		private const int FOURBYTE = 4;
	
		private const int SIGN = -128;
	
		private const byte PAD = (byte) '=';
	
		private static byte[] BASE64_ALPHABET;
	
		private static byte[] LOOKUP_BASE64_ALPHABET;
	
		private Base64Coder() {
	
		}

        public static byte[] FromBinHexString(string s)
        {
            char[] chars = s.ToCharArray();
            byte[] bytes = new byte[chars.Length / 2 + chars.Length % 2];
            FromBinHexString(chars, 0, chars.Length, bytes);
            return bytes;
        }

        public static int FromBinHexString(char[] chars, int offset,
                int charLength, byte[] buffer)
        {
            int bufIndex = offset;
            for (int i = 0; i < charLength - 1; i += 2)
            {
                buffer[bufIndex] = (chars[i] > '9' ? (byte)(chars[i] - 'A' + 10)
                        : (byte)(chars[i] - '0'));
                buffer[bufIndex] <<= 4;
                buffer[bufIndex] += chars[i + 1] > '9' ? (byte)(chars[i + 1] - 'A' + 10)
                        : (byte)(chars[i + 1] - '0');
                bufIndex++;
            }
            if (charLength % 2 != 0)
                buffer[bufIndex++] = (byte)((chars[charLength - 1] > '9' ? (byte)(chars[charLength - 1] - 'A' + 10)
                        : (byte)(chars[charLength - 1] - '0')) << 4);

            return bufIndex - offset;
        }

		private static void Checking() {
			if (BASE64_ALPHABET == null) {
				BASE64_ALPHABET = new byte[BASELENGTH];
				for (int i = 0; i < BASELENGTH; i++) {
					BASE64_ALPHABET[i] = 0;
				}
				for (int i_0 = 'Z'; i_0 >= 'A'; i_0--) {
					BASE64_ALPHABET[i_0] = (byte) (i_0 - 'A');
				}
				for (int i_1 = 'z'; i_1 >= 'a'; i_1--) {
					BASE64_ALPHABET[i_1] = (byte) (i_1 - 'a' + 26);
				}
	
				for (int i_2 = '9'; i_2 >= '0'; i_2--) {
					BASE64_ALPHABET[i_2] = (byte) (i_2 - '0' + 52);
				}
	
				BASE64_ALPHABET['+'] = 62;
				BASE64_ALPHABET['/'] = 63;
			}
			if (LOOKUP_BASE64_ALPHABET == null) {
				LOOKUP_BASE64_ALPHABET = new byte[LOOKUPLENGTH];
				for (int i_3 = 0; i_3 <= 25; i_3++) {
					LOOKUP_BASE64_ALPHABET[i_3] = (byte) ('A' + i_3);
				}
	
				for (int i_4 = 26, j = 0; i_4 <= 51; i_4++, j++) {
					LOOKUP_BASE64_ALPHABET[i_4] = (byte) ('a' + j);
				}
	
				for (int i_5 = 52, j_6 = 0; i_5 <= 61; i_5++, j_6++) {
					LOOKUP_BASE64_ALPHABET[i_5] = (byte) ('0' + j_6);
				}
				LOOKUP_BASE64_ALPHABET[62] = (byte) '+';
				LOOKUP_BASE64_ALPHABET[63] = (byte) '/';
			}
		}
	
		public static bool IsBase64(string v) {
			return IsArrayByteBase64(StringUtils.GetAsciiBytes(v));
		}
	
		public static bool IsArrayByteBase64(byte[] bytes) {
			Checking();
			int length = bytes.Length;
			if (length == 0) {
				return true;
			}
			for (int i = 0; i < length; i++) {
				if (!Base64Coder.IsBase64(bytes[i])) {
					return false;
				}
			}
			return true;
		}
	
		private static bool IsBase64(byte octect) {
			return (octect == PAD || BASE64_ALPHABET[octect] != 0);
		}
	
		public static byte[] Encode(byte[] binaryData) {
			Checking();
			int lengthDataBits = binaryData.Length * EIGHTBIT;
			int fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP;
			int numberTriplets = lengthDataBits / TWENTYFOURBITGROUP;
			byte[] encodedData = null;
	
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
				byte val1_0 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
						: (byte) ((b1) >> 2 ^ 0xc0);
				encodedData[encodedIndex] = LOOKUP_BASE64_ALPHABET[val1_0];
				encodedData[encodedIndex + 1] = LOOKUP_BASE64_ALPHABET[k << 4];
				encodedData[encodedIndex + 2] = PAD;
				encodedData[encodedIndex + 3] = PAD;
			} else if (fewerThan24bits == SIXTEENBIT) {
				b1 = binaryData[dataIndex];
				b2 = binaryData[dataIndex + 1];
				l = (byte) (b2 & 0x0f);
				k = (byte) (b1 & 0x03);
	
				byte val1_1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
						: (byte) ((b1) >> 2 ^ 0xc0);
				byte val2_2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4)
						: (byte) ((b2) >> 4 ^ 0xf0);
	
				encodedData[encodedIndex] = LOOKUP_BASE64_ALPHABET[val1_1];
				encodedData[encodedIndex + 1] = LOOKUP_BASE64_ALPHABET[val2_2
						| (k << 4)];
				encodedData[encodedIndex + 2] = LOOKUP_BASE64_ALPHABET[l << 2];
				encodedData[encodedIndex + 3] = PAD;
			}
			return encodedData;
		}
	
		public static byte[] Decode(byte[] base64Data) {
			Checking();
			if (base64Data.Length == 0) {
				return new byte[0];
			}
	
			int numberQuadruple = base64Data.Length / FOURBYTE;
			byte[] decodedData = null;
			byte b1 = 0, b2 = 0, b3 = 0, b4 = 0, marker0 = 0, marker1 = 0;
	
			int encodedIndex = 0;
			int dataIndex = 0;
			{
				int lastData = base64Data.Length;
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
	
		public static byte[] DecodeBase64(char[] data) {
			Checking();
	
			int size = data.Length;
			int temp = size;
	
			for (int ix = 0; ix < data.Length; ix++) {
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
			byte[] xout = new byte[len];
	
			int shift = 0;
			int accum = 0;
			int index = 0;
	
			for (int ix_0 = 0; ix_0 < size; ix_0++) {
				int value_ren = (data[ix_0] > 255) ? 0: (byte) (BASE64_ALPHABET[data[ix_0]]);
	
				if (value_ren >= 0) {
					accum <<= 6;
					shift += 6;
					accum |= value_ren;
					if (shift >= 8) {
						shift -= 8;
						xout[index++] = (byte) ((accum >> shift) & 0xff);
					}
				}
			}
	
			if (index != xout.Length) {
				throw new Exception("index != " + xout.Length);
			}
	
			return xout;
		}
	
	}
    
}
