namespace java.io
{
    internal class BufferedOutputStream : OutputStream
    {
        public BufferedOutputStream(OutputStream outs)
        {
            base.Wrapped = outs.GetWrappedStream();
        }

        public BufferedOutputStream(OutputStream outs, int bufferSize)
        {
            base.Wrapped = outs.GetWrappedStream();
        }
    }
}
