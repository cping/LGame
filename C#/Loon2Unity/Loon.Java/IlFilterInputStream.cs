namespace Loon.Java
{

    using System;
    using System.IO;
    using Loon.Core;

    public class IlFilterInputStream : Stream
    {
        protected InputStream ins0;

        public IlFilterInputStream(Stream stream)
        {
            this.ins0 = stream;
        }

        public override void Flush()
        {
            throw new NotImplementedException();
        }

        public override int Read(byte[] buffer, int offset, int count)
        {
            throw new NotImplementedException();
        }

        public override long Seek(long offset, SeekOrigin origin)
        {
            throw new NotImplementedException();
        }

        public override void SetLength(long value)
        {
            throw new NotImplementedException();
        }

        public override void Write(byte[] buffer, int offset, int count)
        {
            throw new NotImplementedException();
        }

        public override bool CanRead
        {
            get
            {
                return this.ins0.Stream.CanRead;
            }
        }

        public override bool CanSeek
        {
            get
            {
                return this.ins0.Stream.CanSeek;
            }
        }

        public override bool CanWrite
        {
            get
            {
                return this.ins0.Stream.CanWrite;
            }
        }

        public override long Length
        {
            get
            {
                return this.ins0.Stream.Length;
            }
        }

        public override long Position
        {
            get
            {
                throw new NotImplementedException();
            }
            set
            {
                throw new NotImplementedException();
            }
        }
    }



}
