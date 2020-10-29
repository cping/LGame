namespace loon.utils
{
    public class ArrayByteReader : LRelease
    {

        private const sbyte R = (sbyte)'\r';
        private const sbyte N = (sbyte)'\n';

        private readonly ArrayByte ins;

        public ArrayByteReader(ArrayByte stream)
        {
            ins = stream;
        }


        public void Close()
        {
            ins.Close();
        }

        public ArrayByteReader Reset()
        {
            ins.Reset();
            return this;
        }

        public void Skip(long n)
        {
            if (ins == null)
            {
                return;
            }
            ins.Skip(n);
        }

        public int Read()
        {
            int c = -1;
            if (ins == null)
            {
                return c;
            }
            return c = ins.ReadByte();
        }

        public int Read(sbyte[] buf)
        {
            int c = -1;
            if (ins == null)
            {
                return c;
            }
            return c = ins.Read(buf);
        }

        public int Read(sbyte[] buf, int offset, int length)
        {
            int c = -1;
            if (ins == null)
            {
                return c;
            }
            return c = ins.Read(buf, offset, length);
        }

        public string ReadLine()
        {
            if (ins == null)
            {
                return LSystem.EMPTY;
            }
            if (ins.Available() <= 0)
            {
                return null;
            }
            StrBuilder sbr = new StrBuilder();
            int c = -1;
            bool keepReading = true;
            do
            {
                c = ins.ReadByte();
                switch (c)
                {
                    case N:
                        keepReading = false;
                        break;
                    case R:
                        continue;
                    case -1:
                        return null;
                    default:
                        sbr.Append((char)c);
                        break;
                }
                if (ins.Available() <= 0)
                {
                    keepReading = false;
                }
            } while (keepReading);
            return sbr.ToString();
        }
    }

}
