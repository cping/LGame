namespace Loon.Java
{

    using System;
    using System.Collections.Generic;
    using System.IO;
    using Loon.Java.Generics;
    using Loon.Core;

    public class SequenceInputStream : InputStream
    {

        Loon.Java.Generics.JavaListInterface.IIterator<Stream> e;

        InputStream ins;

        public SequenceInputStream(Loon.Java.Generics.JavaListInterface.IIterator<Stream> e)
        {
            this.e = e;
            try
            {
                NextStream();
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine("panic" + ex.Message);
            }
        }

        public SequenceInputStream(InputStream s1, InputStream s2)
        {
            List<Stream> v = new List<Stream>(2);

            v.Add(s1);
            v.Add(s2);
            e = new Loon.Java.Generics.IteratorAdapter<Stream>(v.GetEnumerator());
            try
            {
                NextStream();
            }
            catch (IOException ex)
            {
                Console.Error.WriteLine("panic" + ex.Message);
            }
        }

        void NextStream()
        {
            if (ins != null)
            {
                ins.Close();
            }
            if (e.HasNext())
            {
                ins = e.Next();
                if (ins == null)
                {
                    throw new Exception();
                }
            }
            else
            {
                ins = null;
            }

        }

        public override int Available()
        {
            if (ins == null)
            {
                return 0;
            }
            return ins.Available();
        }

        public override int Read()
        {
            if (ins == null)
            {
                return -1;
            }
            int c = ins.Read();
            if (c == -1)
            {
                NextStream();
                return Read();
            }
            return c;
        }

        public override int Read(byte[] b, int off, int len)
        {
            if (ins == null)
            {
                return -1;
            }
            else if (b == null)
            {
                throw new Exception();
            }
            else if (off < 0 || len < 0 || len > b.Length - off)
            {
                throw new Exception();
            }
            else if (len == 0)
            {
                return 0;
            }
            int n = ins.Read(b, off, len);
            if (n <= 0)
            {
                NextStream();
                return Read(b, off, len);
            }
            return n;
        }

        public override void Close()
        {
            do
            {
                NextStream();
            } while (ins != null);
        }
    }
}
