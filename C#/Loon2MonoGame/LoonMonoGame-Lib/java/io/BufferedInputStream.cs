namespace java.io
{
    internal class BufferedInputStream : InputStream
    {
        public BufferedInputStream(InputStream s)
        {
            base.Wrapped = s.GetWrappedStream();
        }

        public BufferedInputStream(InputStream s, int bufferSize)
        {
            base.Wrapped = s.GetWrappedStream();
        }
    }
}
