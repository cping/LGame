using java.io;

namespace loon.utils
{
    public class ArrayByteOutput : OutputStream, LRelease
    {

        private readonly ArrayByte _buffer;

        public ArrayByteOutput(int size)
        {
            _buffer = new ArrayByte(size);
        }

        public ArrayByteOutput() : this(8192 * 10)
        {

        }

        public ArrayByte GetArrayByte()
        {
            return _buffer;
        }

        public sbyte[] ToByteArray()
        {
            return _buffer.GetBytes();
        }

        public override void Write(int b)
        {
            _buffer.WriteByte(b);
        }

        public override void Close()
        {
            _buffer.Close();
        }

    }

}
