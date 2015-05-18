using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Java;
using System.IO;

namespace Loon.Utils.Collection
{
    public class ArrayByte
    {

        public const int BIG_ENDIAN = 0;

        public const int LITTLE_ENDIAN = 1;

        public int type;

        private byte[] data;

        private int position;

        private int byteOrder;

        public ArrayByte():this(1024 * 10)
        {
            
        }

        public ArrayByte(int length): this(new byte[length])
        {
           
        }

        public ArrayByte(Stream ins, int type_0)
        {
            MemoryStream output = new MemoryStream();
            byte[] buffer = new byte[1024 * 10];
            int n = 0;
            while (0 != (n = ins.Read(buffer, 0, buffer.Length)))
            {
                output.Write(buffer, 0, n);
            }
            this.data = output.ToArray();
            Reset(type_0);
            if (ins != null)
            {
                ins.Close();
                ins = null;
            }
            output = null;
        }

        public ArrayByte(byte[] data_0)
        {
            this.data = data_0;
            Reset();
        }

        public void Reset()
        {
            Reset(BIG_ENDIAN);
        }

        public void Reset(int type_0)
        {
            position = 0;
            byteOrder = type_0;
        }

        public byte[] GetData()
        {
            return data;
        }

        public int GetByteOrder()
        {
            return byteOrder;
        }

        public void SetByteOrder(int byteOrder_0)
        {
            this.byteOrder = byteOrder_0;
        }

        public byte[] ReadByteArray(int readLength)
        {
            byte[] readBytes = new byte[readLength];
            Read(readBytes);
            return readBytes;
        }

        public int Length()
        {
            return data.Length;
        }

        public void SetLength(int length)
        {
            if (length != data.Length)
            {
                byte[] oldData = data;
                data = new byte[length];
                System.Array.Copy((Array)(oldData), 0, (Array)(data), 0, Math.Min(oldData.Length, length));
                if (position > length)
                {
                    position = length;
                }
            }
        }

        public int Position()
        {
            return position;
        }

        public void SetPosition(int position_0)
        {
            if (position_0 < 0 || position_0 > data.Length)
            {
                throw new IndexOutOfRangeException();
            }

            this.position = position_0;
        }

        public void Truncate()
        {
            SetLength(position);
        }

        public int Available()
        {
            return Length() - Position();
        }

        private void CheckAvailable(int length)
        {
            if (Available() < length)
            {
                throw new IndexOutOfRangeException();
            }
        }

        public byte ReadByte()
        {
            CheckAvailable(1);
            return data[position++];
        }

        public int Read(byte[] buffer)
        {
            return Read(buffer, 0, buffer.Length);
        }

        public int Read(byte[] buffer, int offset, int length)
        {
            if (length == 0)
            {
                return 0;
            }
            CheckAvailable(length);
            System.Array.Copy((Array)(data), position, (Array)(buffer), offset, length);
            position += length;
            return length;
        }

        public long Skip(long n)
        {
            long remaining = n;
            int nr;
            if (n <= 0)
            {
                return 0;
            }
            int size = (int)MathUtils.Min(2048, remaining);
            byte[] skipBuffer = new byte[size];
            while (remaining > 0)
            {
                nr = Read(skipBuffer, 0, (int)Math.Min(size, remaining));
                if (nr < 0)
                {
                    break;
                }
                remaining -= nr;
            }
            return n - remaining;
        }

        public void Read(Stream xout)
        {
            xout.Write(data, position, data.Length - position);
            position = data.Length;
        }

        public bool ReadBoolean()
        {
            return (ReadByte() != 0);
        }

        public short ReadShort()
        {
            CheckAvailable(2);
            if (byteOrder == type)
            {
                return (short)((data[position++] & 0xff) | ((data[position++] & 0xff) << 8));
            }
            else
            {
                return (short)(((data[position++] & 0xff) << 8) | (data[position++] & 0xff));
            }
        }

        public int ReadInt()
        {
            CheckAvailable(4);
            if (byteOrder == type)
            {
                return (data[position++] & 0xff) | ((data[position++] & 0xff) << 8)
                        | ((data[position++] & 0xff) << 16)
                        | ((data[position++] & 0xff) << 24);
            }
            else
            {
                return ((data[position++] & 0xff) << 24)
                        | ((data[position++] & 0xff) << 16)
                        | ((data[position++] & 0xff) << 8)
                        | (data[position++] & 0xff);
            }
        }

        public long ReadLong()
        {
            CheckAvailable(8);
            if (byteOrder == LITTLE_ENDIAN)
            {
                return (ReadInt() & 0xffffffffL)
                        | ((ReadInt() & 0xffffffffL) << 32);
            }
            else
            {
                return ((ReadInt() & 0xffffffffL) << 32)
                        | (ReadInt() & 0xffffffffL);
            }
        }

        public float ReadFloat()
        {
            return BitConverter.DoubleToInt64Bits(ReadInt());
        }

        public double ReadDouble()
        {
            return BitConverter.Int64BitsToDouble(ReadLong());
        }

        public string ReadUTF()
        {
            CheckAvailable(2);
            int utfLength = ReadShort() & 0xffff;
            CheckAvailable(utfLength);

            int goalPosition = Position() + utfLength;

            StringBuilder str0 = new StringBuilder(utfLength);
            while (Position() < goalPosition)
            {
                int a = ReadByte() & 0xff;
                if ((a & 0x80) == 0)
                {
                    str0.Append((char)a);
                }
                else
                {
                    int b = ReadByte() & 0xff;
                    if ((b & 0xc0) != 0x80)
                    {
                        throw new Exception();
                    }

                    if ((a & 0xe0) == 0xc0)
                    {
                        char ch = (char)(((a & 0x1f) << 6) | (b & 0x3f));
                        str0.Append(ch);
                    }
                    else if ((a & 0xf0) == 0xe0)
                    {
                        int c = ReadByte() & 0xff;
                        if ((c & 0xc0) != 0x80)
                        {
                            throw new Exception();
                        }
                        char ch_0 = (char)(((a & 0x0f) << 12) | ((b & 0x3f) << 6) | (c & 0x3f));
                        str0.Append(ch_0);
                    }
                    else
                    {
                        throw new Exception();
                    }
                }
            }
            return str0.ToString();
        }

        private void EnsureCapacity(int dataSize)
        {
            if (position + dataSize > data.Length)
            {
                SetLength(position + dataSize);
            }
        }

        public void WriteByte(int v)
        {
            EnsureCapacity(1);
            data[position++] = (byte)v;
        }

        public void Write(byte[] buffer)
        {
            Write(buffer, 0, buffer.Length);
        }

        public void Write(byte[] buffer, int offset, int length)
        {
            if (length == 0)
            {
                return;
            }
            EnsureCapacity(length);
            System.Array.Copy((Array)(buffer), offset, (Array)(data), position, length);
            position += length;
        }

        public void Write(Stream ins0)
        {
            Write(ins0, 8192);
        }

        public void Write(Stream ins0, int size)
        {
            byte[] buffer = new byte[size];
            while (true)
            {
                int bytesRead = ins0.Read(buffer, 0, buffer.Length);
                if (bytesRead == -1)
                {
                    return;
                }
                Write(buffer, 0, bytesRead);
            }
        }

        public void WriteBoolean(bool v)
        {
            WriteByte((v) ? -1 : 0);
        }

        public void WriteShort(int v)
        {
            EnsureCapacity(2);
            if (byteOrder == type)
            {
                data[position++] = (byte)(v & 0xff);
                data[position++] = (byte)((v >> 8) & 0xff);
            }
            else
            {
                data[position++] = (byte)((v >> 8) & 0xff);
                data[position++] = (byte)(v & 0xff);
            }
        }

        public void WriteInt(int v)
        {
            EnsureCapacity(4);
            if (byteOrder == type)
            {
                data[position++] = (byte)(v & 0xff);
                data[position++] = (byte)((v >> 8) & 0xff);
                data[position++] = (byte)((v >> 16) & 0xff);
                data[position++] = (byte)((int)(((uint)v) >> 24));
            }
            else
            {
                data[position++] = (byte)((int)(((uint)v) >> 24));
                data[position++] = (byte)((v >> 16) & 0xff);
                data[position++] = (byte)((v >> 8) & 0xff);
                data[position++] = (byte)(v & 0xff);
            }
        }

        public void WriteLong(long v)
        {
            EnsureCapacity(8);
            if (byteOrder == type)
            {
                WriteInt((int)(v & 0xffffffffL));
                WriteInt((int)((long)(((ulong)v) >> 32)));
            }
            else
            {
                WriteInt((int)((long)(((ulong)v) >> 32)));
                WriteInt((int)(v & 0xffffffffL));
            }
        }
        
        public void WriteFloat(float v)
        {
            WriteInt((int)v);
        }

        public void WriteDouble(double v)
        {
            WriteLong(BitConverter.DoubleToInt64Bits(v));
        }

        public void WriteUTF(string s)
        {

            int utfLength = 0;
            for (int i = 0; i < s.Length; i++)
            {
                char ch = s[i];
                if (ch > 0 && ch < 0x80)
                {
                    utfLength++;
                }
                else if (ch == 0 || (ch >= 0x80 && ch < 0x800))
                {
                    utfLength += 2;
                }
                else
                {
                    utfLength += 3;
                }
            }

            if (utfLength > 65535)
            {
                throw new Exception();
            }

            EnsureCapacity(2 + utfLength);
            WriteShort(utfLength);

            for (int i_0 = 0; i_0 < s.Length; i_0++)
            {
                int ch_1 = s[i_0];
                if (ch_1 > 0 && ch_1 < 0x80)
                {
                    WriteByte(ch_1);
                }
                else if (ch_1 == 0 || (ch_1 >= 0x80 && ch_1 < 0x800))
                {
                    WriteByte(0xc0 | (0x1f & (ch_1 >> 6)));
                    WriteByte(0x80 | (0x3f & ch_1));
                }
                else
                {
                    WriteByte(0xe0 | (0x0f & (ch_1 >> 12)));
                    WriteByte(0x80 | (0x3f & (ch_1 >> 6)));
                    WriteByte(0x80 | (0x3f & ch_1));
                }
            }
        }

        public int GetArrayType()
        {
            return type;
        }

        public void SetArrayType(int type_0)
        {
            this.type = type_0;
        }
	

    }
}
