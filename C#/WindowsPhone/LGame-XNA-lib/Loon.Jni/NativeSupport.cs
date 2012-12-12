namespace Loon.Jni
{
    public class NativeSupport
    {
        private static bool useLoonNative = false;

        public static void MakeBuffer(byte[] data, int size, byte tag)
        {
            if (useLoonNative)
            {
               // jniencode(data, size, tag);
            }
            else
            {
                for (int i = 0; i < size; i++)
                {
                    data[i] ^= tag;
                }
            }
        }
    }
}
