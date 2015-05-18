namespace Loon.Core.Resource
{
    using System;
    using System.IO;
    using System.Reflection;
    using Loon.Java;

    public class ClassRes : Resource
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

        private Assembly classLoader;

        public ClassRes(string path):this(path, null)
        {
            
        }

        public ClassRes(string path, Assembly c)
        {
            this.path = path;
            this.name = "classpath://" + path;
            this.classLoader = c;
        }

        public virtual Stream GetInputStream()
        {
            try
            {
                if (ins0 != null)
                {
                    return ins0;
                }
                if (classLoader == null)
                {
                    return (ins0 = Resources.OpenStream(path));
                }
                else
                {
                    try
                    {
                        //return (ins0 = XNAConfig.LoadStream(path));
						return null;
                    }
                    catch
                    {
                       // return (ins0 = Resources.ApplicationResourceStream(new Uri(path)));
						return null;
                    }
                }
            }
            catch (Exception e)
            {
                Loon.Utils.Debug.Log.Exception(e);
            }
            return null;
        }

        public virtual string GetResourceName()
        {
            return name;
        }

        public virtual Uri GetURI()
        {
            try
            {
                if (uri != null)
                {
                    return uri;
                }
                return (uri = JavaRuntime.GetResource(classLoader, path));
            }
            catch (Exception ex)
            {
                throw new Exception(ex.Message, ex);
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
            ClassRes other = (ClassRes)obj;
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
