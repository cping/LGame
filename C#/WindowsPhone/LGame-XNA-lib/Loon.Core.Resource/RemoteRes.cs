namespace Loon.Core.Resource
{

    using System;
    using System.IO;
    using Loon.Net;

    public class RemoteRes : Resource
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

        public RemoteRes(string url)
        {
            this.path = url;
            this.name = url;
        }

        public virtual Stream GetInputStream()
        {
            try
            {
                if (ins0 != null)
                {
                    return ins0;
                }
                return ins0 = WebHelper.OpenStream(new Uri(path));
            }
            catch (Exception e)
            {
                throw new Exception(e.Message, e);
            }
        }

        public virtual string GetResourceName()
        {
            return name;
        }

        public virtual Uri GetURI()
        {
            try
            {
                return new Uri(path);
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
            RemoteRes other = (RemoteRes)obj;
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
