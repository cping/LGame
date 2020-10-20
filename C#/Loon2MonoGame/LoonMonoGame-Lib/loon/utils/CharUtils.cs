using java.lang;

namespace loon.utils
{
    class CharUtils
{

        public readonly static char MIN_HIGH_SURROGATE = '\uD800';

        public readonly static char MAX_HIGH_SURROGATE = '\uDBFF';

        public readonly static char MIN_LOW_SURROGATE = '\uDC00';

        public readonly static char MAX_LOW_SURROGATE = '\uDFFF';

        public readonly static char MIN_SURROGATE = MIN_HIGH_SURROGATE;

        public readonly static char MAX_SURROGATE = MAX_LOW_SURROGATE;

        static class HexChars
        {

            readonly static char[] TABLE = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f' };
        }
        public static char ToChar(byte b)
        {
            return (char)(b & 0xFF);
        }
		public static long GetBytesToLong(byte[] bytes)
		{
			return GetBytesToLong(bytes, 0, bytes.Length);
		}

		public static long GetBytesToLong(byte[] x,  int offset,  int n)
		{
			switch (n)
			{
				case 1:
					return x[offset] & 0xFFL;
				case 2:
					return (x[offset + 1] & 0xFFL) | ((x[offset] & 0xFFL) << 8);
				case 3:
					return (x[offset + 2] & 0xFFL) | ((x[offset + 1] & 0xFFL) << 8) | ((x[offset] & 0xFFL) << 16);
				case 4:
					return (x[offset + 3] & 0xFFL) | ((x[offset + 2] & 0xFFL) << 8) | ((x[offset + 1] & 0xFFL) << 16)
							| ((x[offset] & 0xFFL) << 24);
				case 5:
					return (x[offset + 4] & 0xFFL) | ((x[offset + 3] & 0xFFL) << 8) | ((x[offset + 2] & 0xFFL) << 16)
							| ((x[offset + 1] & 0xFFL) << 24) | ((x[offset] & 0xFFL) << 32);
				case 6:
					return (x[offset + 5] & 0xFFL) | ((x[offset + 4] & 0xFFL) << 8) | ((x[offset + 3] & 0xFFL) << 16)
							| ((x[offset + 2] & 0xFFL) << 24) | ((x[offset + 1] & 0xFFL) << 32) | ((x[offset] & 0xFFL) << 40);
				case 7:
					return (x[offset + 6] & 0xFFL) | ((x[offset + 5] & 0xFFL) << 8) | ((x[offset + 4] & 0xFFL) << 16)
							| ((x[offset + 3] & 0xFFL) << 24) | ((x[offset + 2] & 0xFFL) << 32)
							| ((x[offset + 1] & 0xFFL) << 40) | ((x[offset] & 0xFFL) << 48);
				case 8:
					return (x[offset + 7] & 0xFFL) | ((x[offset + 6] & 0xFFL) << 8) | ((x[offset + 5] & 0xFFL) << 16)
							| ((x[offset + 4] & 0xFFL) << 24) | ((x[offset + 3] & 0xFFL) << 32)
							| ((x[offset + 2] & 0xFFL) << 40) | ((x[offset + 1] & 0xFFL) << 48) | ((x[offset] & 0xFFL) << 56);
				default:
					throw new LSysException("No bytes specified");
			}
		}


		public static byte[] FromHex(string hexStr)
		{
			if (StringUtils.IsEmpty(hexStr))
			{
				return new byte[] { };
			}
			byte[] bytes = new byte[hexStr.Length / 2];
			for (int i = 0; i < bytes.Length; i++)
			{
				int char1 = StringExtensions.CharAt(hexStr,i * 2);
				char1 = char1 > 0x60 ? char1 - 0x57 : char1 - 0x30;
				int char2 = StringExtensions.CharAt(hexStr, i * 2 + 1);
				char2 = char2 > 0x60 ? char2 - 0x57 : char2 - 0x30;
				if (char1 < 0 || char2 < 0 || char1 > 15 || char2 > 15)
				{
					throw new LSysException("Invalid hex number: " + hexStr);
				}
				bytes[i] = (byte)((char1 << 4) + char2);
			}
			return bytes;
		}

	}
}
