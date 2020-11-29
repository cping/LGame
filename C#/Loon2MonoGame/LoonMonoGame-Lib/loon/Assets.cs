using java.lang;
using loon.utils;
using loon.utils.reply;
using System.IO;

namespace loon
{
    public abstract class Assets
    {

        private class TextRunnable : Runnable
        {


            private readonly GoPromise<string> _result;

            private readonly string _path;

            private readonly Assets _assets;

            internal TextRunnable(GoPromise<string> res, string path, Assets assets)
            {
                this._result = res;
                this._path = path;
                this._assets = assets;
            }


            public void Run()
            {
                try
                {
                    _result.Succeed(_assets.GetTextSync(_path));
                }
                catch (Throwable t)
                {
                    _result.Fail(t);
                }
            }

        }


        private class ByteRunnable : Runnable
        {

            private readonly GoPromise<sbyte[]> _result;

            private readonly string _path;

            private readonly Assets _assets;

            internal ByteRunnable(GoPromise<sbyte[]> res, string path, Assets assets)
            {
                this._result = res;
                this._path = path;
                this._assets = assets;
            }


            public void Run()
            {
                try
                {
                    _result.Succeed(_assets.GetBytesSync(_path));
                }
                catch (System.Exception t)
                {
                    _result.Fail(t);
                }
            }

        }

        protected internal static string pathPrefix = "Content/";

        protected internal readonly Asyn asyn;

        protected internal Assets(Asyn s)
        {
            this.asyn = s;
        }


        public void SetPathPrefix(string prefix)
        {
            if (prefix.StartsWith("/") || prefix.EndsWith("/"))
            {
                throw new LSysException("Prefix must not start or end with '/'.");
            }
            pathPrefix = (prefix.Length() == 0) ? prefix : (prefix + "/");
        }

        public abstract string GetTextSync(string path);

        public virtual GoFuture<string> GetText(string path)
        {
            GoPromise<string> result = asyn.DeferredPromise<string>();
            asyn.InvokeAsync(new TextRunnable(result, path, this));
            return result;
        }

        public virtual ArrayByte GetArrayByte(string path)
        {
            return new ArrayByte(GetBytesSync(path));
        }

        public abstract sbyte[] GetBytesSync(string path);

        public GoFuture<sbyte[]> GetBytes(string path)
        {
            GoPromise<sbyte[]> result = asyn.DeferredPromise<sbyte[]>();
            asyn.InvokeAsync(new ByteRunnable(result, path, this));
            return result;
        }

        public string GetPathPrefix()
        {
            return pathPrefix;
        }

        protected internal abstract Stream OpenStream(string path);

        protected static string GetPath(string path)
        {
            if (path.IndexOf(pathPrefix) == -1)
            {
                path = pathPrefix + path;
            }
            int pathLen;
            do
            {
                pathLen = path.Length();
                path = path.Replace("[^/]+/\\.\\./", "");
            } while (path.Length() != pathLen);
            return path.Replace("\\", "/");
        }


    }
}
