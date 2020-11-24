using java.lang;

namespace loon.utils
{
    public class ARC4
    {

        public static ArrayByte CryptData(string key, ArrayByte value)
        {
            try
            {
                ARC4 rc4 = new ARC4(key);
                return rc4.GetCrypt(value);
            }
            catch (Throwable)
            {
                return value;
            }
        }

        public static ArrayByte CryptData(string key, string value)
        {
            try
            {
                ARC4 rc4 = new ARC4(key);
                return rc4.GetCrypt(value.GetBytes(LSystem.ENCODING));
            }
            catch (Throwable)
            {
                return new ArrayByte(value.GetBytes());
            }
        }

        private readonly sbyte[] key;
        private readonly sbyte[] state;
        private int x;
        private int y;

        public ARC4(ArrayByte key) : this(key.GetData())
        {

        }

        public ARC4(string key) : this(key.GetBytes(LSystem.ENCODING))
        {

        }

        public ARC4(sbyte[] key)
        {
            this.state = new sbyte[256];
            int length = MathUtils.Min(256, key.Length);
            sbyte[] keyCopy = new sbyte[length];
            JavaSystem.Arraycopy(key, 0, keyCopy, 0, length);
            this.key = keyCopy;
            Reset();
        }

        public void Reset()
        {
            for (int i = 0; i < 256; i++)
            {
                state[i] = (sbyte)i;
            }
            int j = 0;
            for (int i = 0; i < 256; i++)
            {
                j = (j + state[i] + key[i % key.Length]) & 0xff;
                sbyte temp = state[i];
                state[i] = state[j];
                state[j] = temp;
            }

            x = 0;
            y = 0;
        }

        public ArrayByte GetCrypt(sbyte[] data)
        {
            sbyte[] buffer = new sbyte[data.Length];
            Crypt(data, buffer);
            return new ArrayByte(buffer);
        }

        public ArrayByte GetCrypt(ArrayByte input)
        {
            sbyte[] output = new sbyte[input.Available()];
            Crypt(input.GetData(), output);
            return new ArrayByte(output);
        }

        /**
         * ARC4这算法加密一次是加密,加密两次就变成解密了……
         * 
         * @param data
         */
        public void Crypt(sbyte[] data)
        {
            Crypt(data, data);
        }

        public void Crypt(sbyte[] input, sbyte[] output)
        {
            for (int i = 0; i < input.Length; i++)
            {
                x = (x + 1) & 0xff;
                y = (state[x] + y) & 0xff;
                sbyte temp = state[x];
                state[x] = state[y];
                state[y] = temp;
                output[i] = (sbyte)((input[i] ^ state[(state[x] + state[y]) & 0xff]));
            }
        }
    }
}
