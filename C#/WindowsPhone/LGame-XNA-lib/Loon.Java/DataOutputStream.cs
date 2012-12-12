namespace Loon.Java
{
    using System;
    using System.IO;

    public interface DataOutput
    {
        void Flush();

        void Write(sbyte[] buffer);

        void Write(int oneByte);

        void Write(byte[] buffer, int offset, int count);

        void Write(sbyte[] buffer, int offset, int count);

        void WriteBoolean(bool val);

        void WriteByte(int val);

        void WriteBytes(string str);

        void WriteChar(char oneByte);

        void WriteChars(string str);

        void WriteDouble(double val);

        void WriteFloat(float val);

        void WriteInt(int val);

        void WriteLong(long val);

        void WriteShort(int val);

        void WriteUTF(string str);
    }

    public class DataOutputStream : DataOutput
    {
        private sbyte[] buff;
        private Stream stream;
        protected int written;

        public DataOutputStream(Stream stream)
        {
            this.stream = stream;
            this.buff = new sbyte[8];
        }

        public int Available()
        {
            return (int)(this.stream.Length - this.stream.Position);
        }

        private long CountUTFBytes(string str)
        {
            int num = 0;
            int length = str.Length;
            for (int i = 0; i < length; i++)
            {
                int num4 = str[i];
                if ((num4 > 0) && (num4 <= 0x7f))
                {
                    num++;
                }
                else if (num4 <= 0x7ff)
                {
                    num += 2;
                }
                else
                {
                    num += 3;
                }
            }
            return (long)num;
        }

        public void Flush()
        {
            this.stream.Flush();
        }

        public int Size()
        {
            if (this.written < 0)
            {
                this.written = 0x7fffffff;
            }
            return this.written;
        }

        public void Write(sbyte[] buffer)
        {
            if (buffer == null)
            {
                throw new NullReferenceException("K0047");
            }
            foreach (byte num in buffer)
            {
                this.stream.WriteByte(num);
            }
        }

        public void Write(int oneByte)
        {
            byte num = (byte)oneByte;
            this.stream.WriteByte(num);
            this.written++;
        }

        public void Write(byte[] buffer)
        {
             Write(buffer, 0, buffer.Length);
        }

        public void Write(byte[] buffer, int offset, int count)
        {
            if (buffer == null)
            {
                throw new NullReferenceException("K0047");
            }
            this.stream.Write(buffer, offset, count);
            this.written += count;
        }

        public void Write(sbyte[] buffer, int offset, int count)
        {
            if (buffer == null)
            {
                throw new NullReferenceException("K0047");
            }
            byte[] buffer2 = new byte[buffer.Length];
            for (int i = 0; i < buffer.Length; i++)
            {
                buffer2[i] = (byte)buffer[i];
            }
            this.stream.Write(buffer2, offset, count);
            this.written += count;
        }

        public void WriteBoolean(bool val)
        {
            this.stream.WriteByte(val ? ((byte)1) : ((byte)0));
            this.written++;
        }

        public void WriteByte(int val)
        {
            this.stream.WriteByte((byte)val);
            this.written++;
        }

        public void WriteBytes(string str)
        {
            if (str.Length != 0)
            {
                sbyte[] buffer = new sbyte[str.Length];
                for (int i = 0; i < str.Length; i++)
                {
                    buffer[i] = (sbyte)str[i];
                }
                this.Write(buffer);
                this.written += buffer.Length;
            }
        }

        public void WriteChar(char val)
        {
            this.buff[0] = (sbyte)(val >> 8);
            this.buff[1] = (sbyte)val;
            this.Write(this.buff, 0, 2);
            this.written += 2;
        }

        public void WriteChars(string str)
        {
            sbyte[] buffer = new sbyte[str.Length * 2];
            for (int i = 0; i < str.Length; i++)
            {
                int index = (i == 0) ? i : (i * 2);
                buffer[index] = (sbyte)(str[i] >> 8);
                buffer[index + 1] = (sbyte)str[i];
            }
            this.Write(buffer);
            this.written += buffer.Length;
        }

        public void WriteDouble(double val)
        {
            this.WriteLong(BitConverter.DoubleToInt64Bits(val));
        }

        public void WriteFloat(float val)
        {
            this.WriteInt((int)BitConverter.DoubleToInt64Bits((double)((int)val)));
        }

        public void WriteInt(int val)
        {
            this.buff[0] = (sbyte)(val >> 0x18);
            this.buff[1] = (sbyte)(val >> 0x10);
            this.buff[2] = (sbyte)(val >> 8);
            this.buff[3] = (sbyte)val;
            this.Write(this.buff, 0, 4);
            this.written += 4;
        }

        public void WriteLong(long val)
        {
            this.buff[0] = (sbyte)(val >> 0x38);
            this.buff[1] = (sbyte)(val >> 0x30);
            this.buff[2] = (sbyte)(val >> 40);
            this.buff[3] = (sbyte)(val >> 0x20);
            this.buff[4] = (sbyte)(val >> 0x18);
            this.buff[5] = (sbyte)(val >> 0x10);
            this.buff[6] = (sbyte)(val >> 8);
            this.buff[7] = (sbyte)val;
            this.Write(this.buff, 0, 8);
            this.written += 8;
        }

        public void WriteShort(int val)
        {
            this.buff[0] = (sbyte)(val >> 8);
            this.buff[1] = (sbyte)val;
            this.Write(this.buff, 0, 2);
            this.written += 2;
        }

        public void WriteUTF(string str)
        {
            long count = this.CountUTFBytes(str);
            if (count > 0xffffL)
            {
                throw new Exception("K0068");
            }
            this.WriteShort((int)count);
            this.WriteUTFBytes(str, count);
        }

        private void WriteUTFBytes(string str, long count)
        {
            int num = (int)count;
            int length = str.Length;
            sbyte[] buffer = new sbyte[num];
            int num3 = 0;
            for (int i = 0; i < length; i++)
            {
                int num5 = str[i];
                if ((num5 > 0) && (num5 <= 0x7f))
                {
                    buffer[num3++] = (sbyte)num5;
                }
                else if (num5 <= 0x7ff)
                {
                    buffer[num3++] = (sbyte)(0xc0 | (0x1f & (num5 >> 6)));
                    buffer[num3++] = (sbyte)(0x80 | (0x3f & num5));
                }
                else
                {
                    buffer[num3++] = (sbyte)(0xe0 | (15 & (num5 >> 12)));
                    buffer[num3++] = (sbyte)(0x80 | (0x3f & (num5 >> 6)));
                    buffer[num3++] = (sbyte)(0x80 | (0x3f & num5));
                }
            }
            this.Write(buffer, 0, num3);
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
