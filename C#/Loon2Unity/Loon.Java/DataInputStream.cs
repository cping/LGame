namespace Loon.Java
{
    using System;
    using System.IO;
    using System.Text;

    public interface DataInput
    {
        bool ReadBoolean();
        sbyte ReadByte();
        char ReadChar();
        double ReadDouble();
        float ReadFloat();
        void ReadFully(byte[] buffer);
        void ReadFully(byte[] buffer, int offset, int count);
        int Read();
        int ReadInt();
        string ReadLine();
        long ReadLong();
        short ReadShort();
        int ReadUnsignedByte();
        int ReadUnsignedShort();
        string ReadUTF();
        int SkipBytes(int count);
        long Skip(long cnt);
    }

    public class DataInputStream : DataInput
    {

        private byte[] buff;
        private int limit = -1;
        private long marked = -1L;
        private Stream stream;

        public DataInputStream(Stream stream)
        {
            this.stream = stream;
            this.buff = new byte[8];
        }

        public int Available()
        {
            return (int)(this.stream.Length - this.stream.Position);
        }

        public static string ConvertUTF8WithBuf(byte[] buf, char[] outb, int offset, int utfSize)
        {
            int num = 0;
            int index = 0;
            while (num < utfSize)
            {
                if ((outb[index] = (char)buf[offset + num++]) < '\x0080')
                {
                    index++;
                }
                else
                {
                    int num3;
                    if (((num3 = outb[index]) & '\x00e0') == 0xc0)
                    {
                        if (num >= utfSize)
                        {
                            throw new Exception("K0062");
                        }
                        int num4 = buf[num++];
                        if ((num4 & 0xc0) != 0x80)
                        {
                            throw new Exception("K0062");
                        }
                        outb[index++] = (char)(((num3 & 0x1f) << 6) | (num4 & 0x3f));
                    }
                    else
                    {
                        if ((num3 & 240) != 0xe0)
                        {
                            throw new Exception("K0065");
                        }
                        if ((num + 1) >= utfSize)
                        {
                            throw new Exception("K0063");
                        }
                        int num5 = buf[num++];
                        int num6 = buf[num++];
                        if (((num5 & 0xc0) != 0x80) || ((num6 & 0xc0) != 0x80))
                        {
                            throw new Exception("K0064");
                        }
                        outb[index++] = (char)((((num3 & 15) << 12) | ((num5 & 0x3f) << 6)) | (num6 & 0x3f));
                    }
                    continue;
                }
            }
            return new string(outb, 0, index);
        }

        private string DecodeUTF(int utfSize)
        {
            return DecodeUTF(utfSize, this);
        }

        private static string DecodeUTF(int utfSize, DataInput stream)
        {
            byte[] buffer = new byte[utfSize];
            char[] outb = new char[utfSize];
            stream.ReadFully(buffer, 0, utfSize);
            return ConvertUTF8WithBuf(buffer, outb, 0, utfSize);
        }

        public void Mark(int limit)
        {
            this.marked = this.stream.Position;
            this.limit = limit;
        }

        public int Read()
        {
            return stream.ReadByte();
        }

        public int Read(byte[] buffer)
        {
            return this.stream.Read(buffer, 0, buffer.Length);
        }

        public int Read(byte[] buffer, int offset, int length)
        {
            return this.stream.Read(buffer, offset, length);
        }

		public int Limit(){
			return this.limit;
		}

        public bool ReadBoolean()
        {
            int num = this.stream.ReadByte();
            if (num < 0)
            {
                throw new EndOfStreamException();
            }
            return (num != 0);
        }

        public sbyte ReadByte()
        {
            int num = this.stream.ReadByte();
            if (num < 0)
            {
                throw new EndOfStreamException();
            }
            return (sbyte)num;
        }

        public char ReadChar()
        {
            if (this.ReadToBuff(2) < 0)
            {
                throw new EndOfStreamException();
            }
            return (char)(((this.buff[0] & 0xff) << 8) | (this.buff[1] & 0xff));
        }

        public double ReadDouble()
        {
            return BitConverter.Int64BitsToDouble(this.ReadLong());
        }

        public float ReadFloat()
        {
            return (float)BitConverter.DoubleToInt64Bits((double)this.ReadInt());
        }

        public void ReadFully(byte[] buffer)
        {
            this.ReadFully(buffer, 0, buffer.Length);
        }

        public void ReadFully(sbyte[] buffer)
        {
            this.ReadFully(buffer, 0, buffer.Length);
        }

        public void ReadFully(byte[] buffer, int offset, int length)
        {
            if (length < 0)
            {
                throw new IndexOutOfRangeException();
            }
            if (length != 0)
            {
                if (this.stream == null)
                {
                    throw new NullReferenceException("Stream is null");
                }
                if (buffer == null)
                {
                    throw new NullReferenceException("buffer is null");
                }
                if ((offset < 0) || (offset > (buffer.Length - length)))
                {
                    throw new IndexOutOfRangeException();
                }
                while (length > 0)
                {
                    int num = this.stream.Read(buffer, offset, length);
                    if (num == 0)
                    {
                        throw new EndOfStreamException();
                    }
                    offset += num;
                    length -= num;
                }
            }
        }

        public void ReadFully(sbyte[] buffer, int offset, int length)
        {
            int num = offset;
            if (length < 0)
            {
                throw new IndexOutOfRangeException();
            }
            if (length != 0)
            {
                if (this.stream == null)
                {
                    throw new NullReferenceException("Stream is null");
                }
                if (buffer == null)
                {
                    throw new NullReferenceException("buffer is null");
                }
                if ((offset < 0) || (offset > (buffer.Length - length)))
                {
                    throw new IndexOutOfRangeException();
                }
                byte[] buffer2 = new byte[buffer.Length];
                while (length > 0)
                {
                    int num2 = this.stream.Read(buffer2, offset, length);
                    if (num2 == 0)
                    {
                        throw new EndOfStreamException();
                    }
                    offset += num2;
                    length -= num2;
                }
                for (int i = num; i < buffer.Length; i++)
                {
                    buffer[i] = (sbyte)buffer2[i];
                }
            }
        }

        public int ReadInt()
        {
            if (this.ReadToBuff(4) < 0)
            {
                throw new EndOfStreamException();
            }
            return (((((this.buff[0] & 0xff) << 0x18) | ((this.buff[1] & 0xff) << 0x10)) | ((this.buff[2] & 0xff) << 8)) | (this.buff[3] & 0xff));
        }

        public string ReadLine()
        {
            int num=0;
            int num2=0;
            StringBuilder builder = new StringBuilder(80);
            bool flag = false;
            while (true)
            {
                do
                {
                    num = this.stream.ReadByte();
                    switch (num)
                    {
                        case -1:
                            if ((builder.Length == 0) && !flag)
                            {
                                return null;
                            }
                            return builder.ToString();

                        case 10:
                            return builder.ToString();
                    }
                }
                while (num2 == 13);
                builder.Append((char)num);
            }
        }

        public long ReadLong()
        {
            if (this.ReadToBuff(8) < 0)
            {
                throw new EndOfStreamException();
            }
            int num = ((((this.buff[0] & 0xff) << 0x18) | ((this.buff[1] & 0xff) << 0x10)) | ((this.buff[2] & 0xff) << 8)) | (this.buff[3] & 0xff);
            int num2 = ((((this.buff[4] & 0xff) << 0x18) | ((this.buff[5] & 0xff) << 0x10)) | ((this.buff[6] & 0xff) << 8)) | (this.buff[7] & 0xff);
            return (long)(((num & 0xffffffffL) << 0x20) | (num2 & 0xffffffffL));
        }

        public short ReadShort()
        {
            if (this.ReadToBuff(2) < 0)
            {
                throw new EndOfStreamException();
            }
            return (short)(((this.buff[0] & 0xff) << 8) | (this.buff[1] & 0xff));
        }

        private int ReadToBuff(int count)
        {
            int offset = 0;
            while (offset < count)
            {
                int num2 = this.stream.Read(this.buff, offset, count);
                if (num2 == 0)
                {
                    return num2;
                }
                offset += num2;
            }
            return offset;
        }

        public int ReadUnsignedByte()
        {
            int num = this.stream.ReadByte();
            if (num < 0)
            {
                throw new EndOfStreamException();
            }
            return num;
        }

        public int ReadUnsignedShort()
        {
            if (this.ReadToBuff(2) < 0)
            {
                throw new EndOfStreamException();
            }
            return (ushort)(((this.buff[0] & 0xff) << 8) | (this.buff[1] & 0xff));
        }

        public string ReadUTF()
        {
            return this.DecodeUTF(this.ReadUnsignedShort());
        }

        public static string ReadUTF(DataInput stream)
        {
            return DecodeUTF(stream.ReadUnsignedShort(), stream);
        }

        public void Reset()
        {
            if (this.marked > -1L)
            {
                this.stream.Position = this.marked;
            }
            else
            {
                this.stream.Position = 0L;
            }
        }

        public long Skip(long cnt)
        {
            long n = cnt;
            while (n > 0)
            {
                if (Read() == -1)
                    return cnt - n;
                n--;
            }
            return cnt - n;
        }

        public int SkipBytes(int count)
        {
            int num = 0;
            while (num < count)
            {
                this.stream.ReadByte();
                num++;
            }
            if (num < 0)
            {
                throw new EndOfStreamException();
            }
            return num;
        }

        public void Close()
        {
            if (stream != null)
            {
                stream.Close();
                stream = null;
            }
        }

    }


}
