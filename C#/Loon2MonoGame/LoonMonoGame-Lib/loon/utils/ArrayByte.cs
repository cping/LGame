using java.io;
using java.lang;
using java.util;
using System;

namespace loon.utils
{
    public class ArrayByte : IArray, LRelease
    {

        public interface ByteArrayComparator : Comparator<sbyte[]>
        {

            int Compare(sbyte[] buffer1, int offset1, int length1, sbyte[] buffer2, int offset2,
                     int length2);
        }

        internal class ByteArrayDataComparator : ByteArrayComparator
        {

            public int Compare(sbyte[] buffer1, sbyte[] buffer2)
            {
                return Compare(buffer1, 0, buffer1.Length, buffer2, 0, buffer2.Length);
            }

            public int Compare(sbyte[] buffer1, int offset1, int length1, sbyte[] buffer2,
                     int offset2, int length2)
            {
                if (buffer1 == buffer2 && offset1 == offset2 && length1 == length2)
                {
                    return 0;
                }
                int enda = offset1 + length1;
                int endb = offset2 + length2;
                for (int i = offset1, j = offset2; i < enda && j < endb; i++, j++)
                {
                    int a = buffer1[i] & 0xff;
                    int b = buffer2[j] & 0xff;
                    if (a != b)
                    {
                        return a - b;
                    }
                }
                return length1 - length2;
            }

            public Comparator<sbyte[]> Reversed()
            {
                return Comparator_Java<sbyte[]>.Reversed(this);
            }

            public Comparator<sbyte[]> ThenComparing(Comparator<sbyte[]> other)
            {
                return Comparator_Java<sbyte[]>.ThenComparing(this, other);
            }
        }

        private readonly static ByteArrayDataComparator BYTES_COMPARATOR = new ByteArrayDataComparator();

        public static bool CheckHead(sbyte[] head, sbyte[] target)
        {
            if (head == null || target == null)
            {
                return false;
            }
            if (target.Length < head.Length)
                return false;
            return GetDefaultByteArrayComparator().Compare(head, 0, head.Length, target, 0, head.Length) == 0;
        }

        public static ByteArrayComparator GetDefaultByteArrayComparator()
        {
            return BYTES_COMPARATOR;
        }

        public static int Compare(sbyte[] a, sbyte[] b)
        {
            return GetDefaultByteArrayComparator().Compare(a, b);
        }

        public static sbyte[] Max(sbyte[] a, sbyte[] b)
        {
            return GetDefaultByteArrayComparator().Compare(a, b) > 0 ? a : b;
        }

        public static sbyte[] Min(sbyte[] a, sbyte[] b)
        {
            return GetDefaultByteArrayComparator().Compare(a, b) < 0 ? a : b;
        }

        public static sbyte[] NullToEmpty(sbyte[] bytes)
        {
            return bytes == null ? new sbyte[0] : bytes;
        }

        public static bool IsEmpty(sbyte[] bytes)
        {
            return bytes == null || bytes.Length == 0;
        }


        public static int GetUTF8ByteLength(string str)
        {
            int len = 0;
            int ch = 0;
            for (int i = 0; i < str.Length; i++)
            {
                ch = str.CharAt(i);
                if (ch < 128)
                {
                    len += 1;
                }
                else if (ch < 2048)
                {
                    len += 2;
                }
                else if ((ch & 0xFC00) == 0xD800 && (str.CharAt(i + 1) & 0xFC00) == 0xDC00)
                {
                    ++i;
                    len += 4;
                }
                else
                {
                    len += 3;
                }
            }
            return len;
        }

        public static ArrayByte EncodeUTF8(string str)
        {
            return EncodeUTF8(str, BIG_ENDIAN);
        }

        public static ArrayByte EncodeUTF8(string str, int orderType)
        {
            int offset = 0;
            int c1 = 0;
            int c2 = 0;
            IntArray buffer = new IntArray(GetUTF8ByteLength(str));
            for (int i = 0; i < str.Length(); i++)
            {
                c1 = str.CharAt(i);
                if (c1 < 128)
                {
                    buffer.Set(offset++, c1);
                }
                else if (c1 < 2048)
                {
                    buffer.Set(offset++, c1 >> 6 | 192);
                    buffer.Set(offset++, c1 & 63 | 128);
                }
                else if ((c1 & 0xFC00) == 0xD800 && ((c2 = str.CharAt(i + 1)) & 0xFC00) == 0xDC00)
                {
                    c1 = 0x10000 + ((c1 & 0x03FF) << 10) + (c2 & 0x03FF);
                    ++i;
                    buffer.Set(offset++, c1 >> 18 | 240);
                    buffer.Set(offset++, c1 >> 12 & 63 | 128);
                    buffer.Set(offset++, c1 >> 6 & 63 | 128);
                    buffer.Set(offset++, c1 & 63 | 128);
                }
                else
                {
                    buffer.Set(offset++, c1 >> 12 | 224);
                    buffer.Set(offset++, c1 >> 6 & 63 | 128);
                    buffer.Set(offset++, c1 & 63 | 128);
                }
            }
            ArrayByte bytes = new ArrayByte(offset);
            bytes.SetByteOrder(orderType);
            for (int i = 0; i < offset; i++)
            {
                bytes.WriteByte(buffer.Get(i));
            }
            return bytes;
        }

        public const int BIG_ENDIAN = 0;

        public const int LITTLE_ENDIAN = 1;

        private sbyte[] data;

        private int position;

        private int byteOrder;

        private bool expandArray = true;

        public ArrayByte() : this(4096)
        {

        }

        public ArrayByte(int length) : this(new sbyte[length])
        {

        }

        public ArrayByte(string base64)
        {
            if (!Base64Coder.IsBase64(base64))
            {
                throw new LSysException("it is not base64 :" + base64);
            }
            this.data = Base64Coder.DecodeBase64(base64.ToCharArray());
            Reset();
        }

        public ArrayByte(sbyte[] data)
        {
            this.data = data;
            Reset();
        }

        public void Reset()
        {
            SetOrder(BIG_ENDIAN);
        }

        public void SetOrder(int type)
        {
            expandArray = true;
            position = 0;
            byteOrder = type;
        }

        public sbyte Get(int idx)
        {
            return data[idx];
        }

        public sbyte Get()
        {
            return data[position++];
        }

        public int GetByteOrder()
        {
            return byteOrder;
        }

        public void SetByteOrder(int byteOrder)
        {
            this.byteOrder = byteOrder;
        }

        public sbyte[] ReadByteArray(int readLength)
        {
            sbyte[]
        readBytes = new sbyte[readLength];
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
                sbyte[] oldData = data;
                data = new sbyte[length];
                JavaSystem.Arraycopy(oldData, 0, data, 0, MathUtils.Min(oldData.Length, length));
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

        public void SetPosition(int position)
        {
            if (position < 0 || position > data.Length)
            {
                throw new LSysException("ArrayByte Index Out Of Bounds !");
            }
            this.position = position;
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
                throw new LSysException("ArrayByte Index Out Of Bounds !");
            }
        }

        public int Read()
        {
            CheckAvailable(1);
            return data[position++] & 0xff;
        }

        public sbyte ReadByte()
        {
            CheckAvailable(1);
            return data[position++];
        }

        public int Read(sbyte[] buffer)
        {
            return Read(buffer, 0, buffer.Length);
        }

        public int Read(sbyte[] buffer, int offset, int length)
        {
            if (length == 0)
            {
                return 0;
            }
            CheckAvailable(length);
            JavaSystem.Arraycopy(data, position, buffer, offset, length);
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
            sbyte[] skipBuffer = new sbyte[size];
            while (remaining > 0)
            {
                nr = Read(skipBuffer, 0, (int)MathUtils.Min(size, remaining));
                if (nr < 0)
                {
                    break;
                }
                remaining -= nr;
            }
            return n - remaining;
        }

        public void Read(OutputStream outs)
        {
            byte[] bytes = GetUNBytes();
            outs.Write(bytes, position, data.Length - position);
            position = data.Length;
        }

        public bool ReadBoolean()
        {
            return (ReadByte() != 0);
        }

        protected int Read2Byte()
        {
            CheckAvailable(2);
            if (byteOrder == LITTLE_ENDIAN)
            {
                return ((data[position++] & 0xff) | ((data[position++] & 0xff) << 8));
            }
            else
            {
                return (((data[position++] & 0xff) << 8) | (data[position++] & 0xff));
            }
        }

        public char ReadChar()
        {
            return (char)(Read2Byte());
        }

        public short ReadShort()
        {
            return (short)Read2Byte();
        }

        public long ReadUInt8()
        {
            return (0x000000FF & (int)ReadByte());
        }

        public long ReadUInt16()
        {
            int firstByte = (0x000000FF & (int)ReadByte());
            int secondByte = (0x000000FF & (int)ReadByte());
            long result = 0;
            if (this.byteOrder == LITTLE_ENDIAN)
            {
                result = (long)((secondByte << 8 | firstByte) & 0xFFFFFFFFL);
            }
            else
            {
                result = (long)((firstByte << 8 | secondByte) & 0xFFFFFFFFL);
            }
            return result;
        }

        public long ReadUInt32()
        {

            int firstByte = (0x000000FF & (int)ReadByte());
            int secondByte = (0x000000FF & (int)ReadByte());
            int thirdByte = (0x000000FF & (int)ReadByte());
            int fourthByte = (0x000000FF & (int)ReadByte());
            long result = 0;
            if (this.byteOrder == LITTLE_ENDIAN)
            {
                result = ((long)(fourthByte << 24 | thirdByte << 16 | secondByte << 8 | firstByte)) & 0xFFFFFFFFL;
            }
            else
            {
                result = ((long)(firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;
            }
            return result;
        }

        public int ReadInt()
        {
            CheckAvailable(4);
            if (byteOrder == LITTLE_ENDIAN)
            {
                return (data[position++] & 0xff) | ((data[position++] & 0xff) << 8) | ((data[position++] & 0xff) << 16)
                        | ((data[position++] & 0xff) << 24);
            }
            else
            {
                return ((data[position++] & 0xff) << 24) | ((data[position++] & 0xff) << 16)
                        | ((data[position++] & 0xff) << 8) | (data[position++] & 0xff);
            }
        }

        public double ReadDouble()
        {
            return NumberUtils.LongBitsToDouble(ReadLong());
        }

        public long ReadLong()
        {
            CheckAvailable(8);
            if (byteOrder == LITTLE_ENDIAN)
            {
                return (ReadInt() & 0xffffffffL) | ((ReadInt() & 0xffffffffL) << 32);
            }
            else
            {
                return ((ReadInt() & 0xffffffffL) << 32) | (ReadInt() & 0xffffffffL);
            }
        }
        public float ReadFloat()
        {
            return NumberUtils.IntBitsToFloat(ReadInt());
        }

        public string ReadUTF()
        {
            CheckAvailable(2);
            int utfLength = ReadShort() & 0xffff;
            CheckAvailable(utfLength);

            int goalPosition = Position() + utfLength;

            StrBuilder strings = new StrBuilder(utfLength);
            while (Position() < goalPosition)
            {
                int a = ReadByte() & 0xff;
                if ((a & 0x80) == 0)
                {
                    strings.Append((char)a);
                }
                else
                {
                    int b = ReadByte() & 0xff;
                    if ((b & 0xc0) != 0x80)
                    {
                        throw new LSysException(StringExtensions.ValueOf(b));
                    }

                    if ((a & 0xe0) == 0xc0)
                    {
                        char ch = (char)(((a & 0x1f) << 6) | (b & 0x3f));
                        strings.Append(ch);
                    }
                    else if ((a & 0xf0) == 0xe0)
                    {
                        int c = ReadByte() & 0xff;
                        if ((c & 0xc0) != 0x80)
                        {
                            throw new LSysException(StringExtensions.ValueOf(c));
                        }
                        char ch = (char)(((a & 0x0f) << 12) | ((b & 0x3f) << 6) | (c & 0x3f));
                        strings.Append(ch);
                    }
                    else
                    {
                        throw new LSysException("null");
                    }
                }
            }
            return strings.ToString();
        }

        private void EnsureCapacity(int dataSize)
        {
            if (position + dataSize > data.Length)
            {
                if (expandArray)
                {
                    SetLength((position + dataSize) * 2);
                }
                else
                {
                    SetLength(position + dataSize);
                }
            }
        }

        public void WriteByte(sbyte v)
        {
            EnsureCapacity(1);
            data[position++] = v;
        }

        public void WriteByte(int v)
        {
            EnsureCapacity(1);
            data[position++] = (sbyte)v;
        }

        public void Write(sbyte[] buffer)
        {
            Write(buffer, 0, buffer.Length);
        }

        public void Write(sbyte[] buffer, int offset, int length)
        {
            if (length == 0)
            {
                return;
            }
            EnsureCapacity(length);
            JavaSystem.Arraycopy(buffer, offset, data, position, length);
            position += length;
        }

        public void WriteBoolean(bool v)
        {
            WriteByte(v ? -1 : 0);
        }

        public void WriteChar(char v)
        {
            WriteShort(v);
        }

        public void WriteShort(int v)
        {
            EnsureCapacity(2);
            if (byteOrder == LITTLE_ENDIAN)
            {
                data[position++] = (sbyte)(v & 0xff);
                data[position++] = (sbyte)((v >> 8) & 0xff);
            }
            else
            {
                data[position++] = (sbyte)((v >> 8) & 0xff);
                data[position++] = (sbyte)(v & 0xff);
            }
        }

        public void WriteInt(sbyte[] ba, int start, int len)
        {
            int end = start + len;
            for (int i = start; i < end; i++)
            {
                WriteInt(ba[i]);
            }
        }

        public void WriteInt(int v)
        {
            EnsureCapacity(4);
            if (byteOrder == LITTLE_ENDIAN)
            {
                data[position++] = (sbyte)(v & 0xff);
                data[position++] = (sbyte)((v >> 8) & 0xff);
                data[position++] = (sbyte)((v >> 16) & 0xff);
                data[position++] = (sbyte)((int)((uint)v >> 24));
            }
            else
            {
                data[position++] = (sbyte)((int)((uint)v >> 24));
                data[position++] = (sbyte)((v >> 16) & 0xff);
                data[position++] = (sbyte)((v >> 8) & 0xff);
                data[position++] = (sbyte)(v & 0xff);
            }
        }

        public void WriteLong(long v)
        {
            EnsureCapacity(8);
            if (byteOrder == LITTLE_ENDIAN)
            {
                WriteInt((int)(v & 0xffffffffL));
                WriteInt((int)((uint)v >> 32));
            }
            else
            {
                WriteInt((int)((uint)v >> 32));
                WriteInt((int)(v & 0xffffffffL));
            }
        }

        public void WriteFloat(float v)
        {
            WriteInt((int)NumberUtils.FloatToIntBits(v));
        }

        public void WriteUTF(string s)
        {

            int utfLength = 0;
            for (int i = 0; i < s.Length; i++)
            {
                char ch = s.CharAt(i);
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
                throw new LSysException(utfLength + " > 65535");
            }

            EnsureCapacity(2 + utfLength);
            WriteShort(utfLength);

            for (int i = 0; i < s.Length; i++)
            {
                int ch = s.CharAt(i);
                if (ch > 0 && ch < 0x80)
                {
                    WriteByte(ch);
                }
                else if (ch == 0 || (ch >= 0x80 && ch < 0x800))
                {
                    WriteByte(0xc0 | (0x1f & (ch >> 6)));
                    WriteByte(0x80 | (0x3f & ch));
                }
                else
                {
                    WriteByte(0xe0 | (0x0f & (ch >> 12)));
                    WriteByte(0x80 | (0x3f & (ch >> 6)));
                    WriteByte(0x80 | (0x3f & ch));
                }
            }
        }

        public sbyte[] GetData()
        {
            return data;
        }
        public byte[] GetUNBytes()
        {
            return CharUtils.ToUNBytes(GetBytes());
        }

        public sbyte[] GetBytes()
        {
            Truncate();
            return data;
        }

        public int Limit()
        {
            return data == null ? 0 : data.Length;
        }

        public bool IsExpandArray()
        {
            return expandArray;
        }

        public void SetExpandArray(bool expandArray)
        {
            this.expandArray = expandArray;
        }

        public ArrayByte SetArray(ArrayByte uarr, int offset)
        {
            int max = uarr.Length() < (this.Length() - offset) ? uarr.Length() : (Length() - offset);
            this.Write(uarr.data, offset, max);
            return this;
        }

        public ArrayByte Slice(int begin, int end)
        {
            if (end == -1)
            {
                end = this.Length();
            }
            int len = end - begin;
            ArrayByte bytes = new ArrayByte(len);
            bytes.Write(this.data, begin, len);
            return bytes;
        }

        public string ToUTF8String()
        {
            return new JavaString(GetUNBytes()).ToString();
        }

        public int Size()
        {
            return Length();
        }


        public void Clear()
        {
            this.Reset();
            this.data = new sbyte[Length()];
        }


        public bool IsEmpty()
        {
            return this.data == null || Length() == 0;
        }

        public ArrayByte CryptARC4Data(string privateKey)
        {
            return ARC4.CryptData(privateKey, this);
        }

        public string CryptMD5Data()
        {
            return MD5.Get().EncryptBytes(this);
        }

        public override string ToString()
        {
            return new JavaString(CharUtils.ToUNBytes(Base64Coder.Encode(data))).ToString();
        }

        public override int GetHashCode()
        {
            int hashCode = 1;
            for (int i = data.Length - 1; i > -1; i--)
            {
                hashCode = 31 * hashCode + data[i];
            }
            return hashCode;
        }

        public void Close()
        {
            data = null;
        }

    }

}
