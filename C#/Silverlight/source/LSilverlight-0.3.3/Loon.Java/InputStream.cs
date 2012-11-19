namespace Loon.Java
{
    using System;
    using System.IO;
    using Loon.Core;

    internal class WrappedSystemStream : Stream
    {
        private InputStream ist;

        private OutputStream ost;

        public WrappedSystemStream(InputStream ist)
        {
            this.ist = ist;
        }

        public WrappedSystemStream(OutputStream ost)
        {
            this.ost = ost;
        }

        public override void Close()
        {
            if (this.ist != null)
            {
                this.ist.Close();
            }
            if (this.ost != null)
            {
                this.ost.Close();
            }
        }

        public override void Flush()
        {
            this.ost.Flush();
        }

        public override int Read(byte[] buffer, int offset, int count)
        {
            int res = this.ist.Read(buffer, offset, count);
            return res != -1 ? res : 0;
        }

        public override int ReadByte()
        {
            return this.ist.Read();
        }

        public override long Seek(long offset, SeekOrigin origin)
        {
            throw new NotSupportedException();
        }

        public override void SetLength(long value)
        {
            throw new NotSupportedException();
        }

        public override void Write(byte[] buffer, int offset, int count)
        {
            this.ost.Write(buffer, offset, count);
        }

        public override void WriteByte(byte value)
        {
            this.ost.Write(value);
        }

        public override bool CanRead
        {
            get { return (this.ist != null); }
        }

        public override bool CanSeek
        {
            get { return false; }
        }

        public override bool CanWrite
        {
            get { return (this.ost != null); }
        }

        public override long Length
        {
            get
            {
                throw new NotSupportedException();
            }
        }

        public override long Position
        {
            get
            {
                throw new NotSupportedException();
            }
            set
            {
                throw new NotSupportedException();
            }
        }
    }

    public class InputStream : IDisposable
    {
        private long mark;

        protected Stream Wrapped;

        public Stream Stream
        {
            get
            {
                return Wrapped;
            }
        }

        public static implicit operator InputStream(Stream s)
        {
            return Wrap(s);
        }

        public static implicit operator Stream(InputStream s)
        {
            return s.GetWrappedStream();
        }

        public virtual int Available()
        {
            return (int)Wrapped.Length;
        }

        public virtual void Close()
        {
            if (Wrapped != null)
            {
                Wrapped.Close();
            }
        }

        public void Dispose()
        {
            Close();
        }

        internal Stream GetWrappedStream()
        {
            if (Wrapped != null)
            {
                return Wrapped;
            }
            return new WrappedSystemStream(this);
        }

        public virtual void Mark(int readlimit)
        {
            if (Wrapped != null)
            {
                this.mark = Wrapped.Position;
            }
        }

        public virtual bool MarkSupported()
        {
            return ((Wrapped != null) && Wrapped.CanSeek);
        }
        
        public virtual int Read()
        {
            if (Wrapped == null)
            {
                throw new NotImplementedException();
            }
           
            return Wrapped.ReadByte();
        }

        public virtual int Read(byte[] buf)
        {
            return Read(buf, 0, buf.Length);
        }

        public virtual int Read(byte[] b, int off, int len)
        {
            if (Wrapped != null)
            {
                int num = Wrapped.Read(b, off, len);
                return ((num <= 0) ? -1 : num);
            }
            int totalRead = 0;
            while (totalRead < len)
            {
                int nr = Read();
                if (nr == -1)
                    return -1;
                b[off + totalRead] = (byte)nr;
                totalRead++;
            }
            return totalRead;
        }

        public virtual void Reset()
        {
            if (Wrapped == null)
            {
                throw new IOException();
            }
            Wrapped.Position = mark;
        }

        public virtual long Skip(long cnt)
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

        static internal InputStream Wrap(Stream s)
        {
            InputStream stream = new InputStream();
            stream.Wrapped = s;
            return stream;
        }
    }
}
