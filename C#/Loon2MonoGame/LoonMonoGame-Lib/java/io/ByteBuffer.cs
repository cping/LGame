namespace java.io
{
    using System;

    public class ByteBuffer
    {
        private sbyte[] data;
        private int pos = 0;

        private ByteBuffer(int size)
        {
            this.data = new sbyte[size];
        }

        public static ByteBuffer AllocateDirect(int size)
        {
            return new ByteBuffer(size);
        }

        public IntBuffer AsIntBuffer()
        {
            return new IntBuffer(this);
        }

        public int Get()
        {
            return this.data[this.pos++];
        }

        public int Get(int index)
        {
            return this.data[index];
        }

        public float GetFloat()
        {
            float num = this.GetFloat(this.pos);
            this.pos += 4;
            return num;
        }

        public float GetFloat(int index)
        {
            byte[] buffer = new byte[] { (byte)this.data[index], (byte)this.data[index + 1], (byte)this.data[index + 2], (byte)this.data[index + 3] };
            return BitConverter.ToSingle(buffer, 0);
        }

        public int GetInt()
        {
            int num = this.GetInt(this.pos);
            this.pos += 4;
            return num;
        }

        public int GetInt(int index)
        {
            return ((((this.data[index] & 0xff) | ((this.data[index + 1] & 0xff) << 8)) | ((this.data[index + 2] & 0xff) << 0x10)) | (this.data[index + 3] << 0x18));
        }

        public short GetShort()
        {
            int num = this.GetShort(this.pos);
            this.pos += 2;
            return (short)num;
        }

        public short GetShort(int index)
        {
            int num = (this.data[index] & 0xff) | (this.data[index + 1] << 8);
            return (short)num;
        }

        public int Position()
        {
            return this.pos;
        }

        public void Position(int pos)
        {
            this.pos = pos;
        }

        public void Put(int index, sbyte value)
        {
            this.data[index] = value;
        }

        public void Put(sbyte[] src, int offset, int length)
        {
            Array.Copy(src, offset, this.data, this.pos, length);
            this.pos += length;
        }

        public void PutFloat(int index, float value)
        {
            byte[] bytes = BitConverter.GetBytes(value);
            this.data[index] = (sbyte)bytes[0];
            this.data[index + 1] = (sbyte)bytes[1];
            this.data[index + 2] = (sbyte)bytes[2];
            this.data[index + 3] = (sbyte)bytes[3];
        }

        public void PutInt(int index, int value)
        {
            this.data[index] = (sbyte)value;
            this.data[index + 1] = (sbyte)(value >> 8);
            this.data[index + 2] = (sbyte)(value >> 0x10);
            this.data[index + 3] = (sbyte)(value >> 0x18);
        }

        public void PutShort(int index, short value)
        {
            this.data[index] = (sbyte)value;
            this.data[index + 1] = (sbyte)(value >> 8);
        }

        public void Rewind()
        {
            this.pos = 0;
        }

    }
}
