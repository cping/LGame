namespace java.io
{
    using System;
    using System.IO;

    public class CharArrayReader : TextReader
    {
  
        protected char[] buffer;
        protected int curMarkedPos;
        protected int length;
        protected int position;
        protected object sync;

        public CharArrayReader(char[] buffer)
        {
            this.curMarkedPos = -1;
            this.buffer = buffer;
            this.length = buffer.Length;
            this.sync = buffer;
        }

        public CharArrayReader(char[] buffer, int offset, int length)
            : this(buffer)
        {
            if (((offset < 0) || (offset > buffer.Length)) || (length < 0))
            {
                throw new ArgumentException();
            }
            this.buffer = buffer;
            this.position = offset;
            this.curMarkedPos = offset;
            this.length = ((this.position + length) < buffer.Length) ? length : buffer.Length;
        }

        public override void Close()
        {
            lock (this.sync)
            {
                if (this.IsOpen)
                {
                    this.buffer = null;
                }
            }
        }

        public void Mark(int limit)
        {
            lock (this.sync)
            {
                if (this.IsClosed)
                {
                    throw new IOException();
                }
                this.curMarkedPos = this.position;
            }
        }

        public bool MarkSupported()
        {
            return true;
        }

        public override int Read()
        {
            lock (this.sync)
            {
                if (this.IsClosed)
                {
                    throw new IOException("Reader is closed.");
                }
                if (this.position == this.length)
                {
                    return -1;
                }
                return this.buffer[this.position++];
            }
        }

        public override int Read(char[] buffer, int offset, int count)
        {
            if (((offset < 0) || (offset > buffer.Length)) || ((count < 0) || (count > (buffer.Length - offset))))
            {
                throw new ArgumentOutOfRangeException();
            }
            lock (this.sync)
            {
                if (this.IsClosed)
                {
                    throw new IOException();
                }
                if (this.position < this.length)
                {
                    int length = ((this.position + count) > this.length) ? (this.length - this.position) : count;
                    Array.Copy(this.buffer, this.position, buffer, offset, length);
                    this.position += length;
                    return length;
                }
                return -1;
            }
        }

        public bool Ready()
        {
            lock (this.sync)
            {
                if (this.IsClosed)
                {
                    throw new IOException("Reader is closed.");
                }
                return (this.position != this.length);
            }
        }

        public void Reset()
        {
            lock (this.sync)
            {
                if (this.IsClosed)
                {
                    throw new IOException("Reader is closed.");
                }
                this.position = (this.curMarkedPos != -1) ? this.curMarkedPos : 0;
            }
        }

        public long Skip(long numberToSkip)
        {
            lock (this.sync)
            {
                if (this.IsClosed)
                {
                    throw new IOException("Reader is closed.");
                }
                if (numberToSkip <= 0L)
                {
                    return 0L;
                }
                long num = 0L;
                if (numberToSkip < (this.length - this.position))
                {
                    this.position += (int)numberToSkip;
                    num = numberToSkip;
                }
                else
                {
                    num = this.length - this.position;
                    this.position = this.length;
                }
                return num;
            }
        }

        private bool IsClosed
        {
            get
            {
                return (this.buffer == null);
            }
        }

        private bool IsOpen
        {
            get
            {
                return (this.buffer != null);
            }
        }
    }


}
