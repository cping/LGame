namespace Loon.Core.Resource
{
    using System.IO;
    using System;
  
    public class FileRes : Resource
    {

        internal string path;

        internal string name;

        internal Stream ins0;

        internal Uri uri;

        public override int GetHashCode()
        {
            return (name == null) ? base.GetHashCode() : name.GetHashCode();
        }

        public void Dispose()
        {
            if (ins0 != null)
            {
                try
                {
                    ins0.Close();
                    ins0 = null;
                }
                catch
                {
                }
            }
            if (uri != null)
            {
                uri = null;
            }
        }

        public FileRes(string path)
        {
            this.path = path;
            this.name = "file://" + path;
        }

        public Stream GetInputStream()
        {
            try
            {
                if (ins0 != null)
                {
                    return ins0;
                }
                return (ins0 = new FileInfo(path).OpenRead());
            }
            catch (FileNotFoundException e)
            {
                throw new Exception("file " + name + " not found !", e);
            }
        }

        public string GetResourceName()
        {
            return name;
        }

        public Uri GetURI()
        {
            try
            {
                if (uri != null)
                {
                    return uri;
                }
                return (uri = new Uri(path));
            }
            catch (Exception e)
            {
                throw new Exception(e.Message, e);
            }
        }

        public override bool Equals(object obj)
        {
            if ((object)this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if ((object)GetType() != (object)obj.GetType())
            {
                return false;
            }
            FileRes other = (FileRes)obj;
            if (name == null)
            {
                if (other.name != null)
                {
                    return false;
                }
            }
            else if (!name.Equals(other.name))
            {
                return false;
            }
            return true;
        }

    }
}
