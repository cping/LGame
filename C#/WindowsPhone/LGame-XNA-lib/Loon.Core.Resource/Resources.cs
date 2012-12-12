using System;
using Microsoft.Xna.Framework;
using Loon.Java;
using Loon.Core.Input;
using System.IO;
using Loon.Utils;
using Loon.Utils.Collection;
using Loon.Net;

namespace Loon.Core.Resource
{
    public sealed class Resources
    {

        public static Resource ClassRes(string path)
        {
            return new ClassRes(path);
        }

        public static Resource FileRes(string path)
        {
            return new FileRes(path);
        }

        public static Resource RemoteRes(string path)
        {
            return new RemoteRes(path);
        }

        public static Resource SdRes(string path)
        {
            return new SDRes(path);
        }

        public static Stream StrRes(string path)
        {
            if (path == null)
            {
                return null;
            }
            Stream ins0 = null;
            if (path.IndexOf("->") == -1)
            {
                if (path.StartsWith("sd:"))
                {
                    ins0 = SdRes(path.Substring(3, (path.Length) - (3))).GetInputStream();
                }
                else if (path.StartsWith("class:"))
                {
                    ins0 = ClassRes(path.Substring(6, (path.Length) - (6)))
                            .GetInputStream();
                }
                else if (path.StartsWith("path:"))
                {
                    ins0 = FileRes(path.Substring(5, (path.Length) - (5))).GetInputStream();
                }
                else if (path.StartsWith("url:"))
                {
                    ins0 = RemoteRes(path.Substring(4, (path.Length) - (4)))
                            .GetInputStream();
                }
            }
            else
            {
                string[] newPath = StringUtils.Split(path, "->");
                ins0 = LPKResource.OpenStream(
                        newPath[0].Trim(), newPath[1].Trim());
            }
            return ins0;
        }


        private static Type appType;

        private static bool supportApplication = true;

        /// <summary>
        /// 以反射方式，尝试通过Application读取数据
        /// </summary>
        /// <param name="pathUri"></param>
        /// <returns></returns>
        public static System.IO.Stream ApplicationResourceStream(Uri pathUri)
        {
            if (!supportApplication)
            {
                return null;
            }
            try
            {
                if (appType == null)
                {
                    try
                    {
                        appType = JavaRuntime.ClassforName("System.Windows.Application");
                        supportApplication = true;
                    }
                    catch
                    {
                        supportApplication = false;
                    }
                }
                object o = JavaRuntime.NewInstance(appType);
                System.Reflection.MethodInfo method = JavaRuntime.GetMethod(appType, "GetResourceStream", pathUri.GetType());
                object res = JavaRuntime.Invoke(method, o, pathUri);
                if (res != null)
                {
                    System.Reflection.MethodInfo info = res.GetType().GetMethod("get_Stream");
                    object open = JavaRuntime.Invoke(info, res);
                    return open as System.IO.Stream;
                }
            }
            catch
            {
            }
            return null;
        }

        /// <summary>
        ///  尝试读取数据流
        /// </summary>
        /// <param name="resName"></param>
        /// <returns></returns>
        public static InputStream OpenSource(string resName)
        {
            System.IO.Stream stream = OpenStream(resName);
            if (stream == null)
            {
                return null;
            }
            //重载为InputStream
            return stream;
        }

        /// <summary>
        /// 尝试读取数据流
        /// </summary>
        /// <param name="resName"></param>
        /// <returns></returns>
        public static System.IO.Stream OpenStream(string resName)
        {
            if (resName.IndexOf("/") == -1 && FileUtils.GetExtension(resName).Length == 0)
            {
                resName = "Content/" + resName;
            }
            Stream resource = StrRes(resName);
            if (resource != null)
            {
                return resource;
            }
            //尝试读取XAP中文件(Content类型)
            System.IO.Stream stream = null;
            try
            {
                stream = TitleContainer.OpenStream(resName);
            }
            catch
            {
                try
                {
                    if (stream == null)
                    {
                        stream = XNAConfig.LoadStream(resName);
                    }
                }
                catch (Exception)
                {
                    if (stream == null)
                    {
                        //尝试读取DLL中文件(Resource类型)
                        Uri pathUri = new Uri("/" + JavaRuntime.GetAssemblyName() + ";component/" + resName, UriKind.RelativeOrAbsolute);
                        stream = ApplicationResourceStream(pathUri);
                    }
                }
            }
            if (stream == null)
            {
                //尝试读取标准路径数据
                try
                {
                    stream = new System.IO.FileStream(resName, System.IO.FileMode.Open);
                }
                catch
                {
                    //尝试读取封闭空间中数据
                    try
                    {
                        System.IO.IsolatedStorage.IsolatedStorageFile store = System.IO.IsolatedStorage.IsolatedStorageFile.GetUserStoreForApplication();
                        stream = store.OpenFile(resName, System.IO.FileMode.Open);
                    }
                    catch (Exception ex)
                    {
                        Loon.Utils.Debug.Log.Exception(ex);
                        Loon.Utils.Debug.Log.DebugWrite("\n" + resName + " file not found !");
                    }
                }
            }
            return stream;
        }

        public static ArrayByte GetResource(string resName)
        {
            if (resName == null)
            {
                return null;
            }
            InputStream resource = OpenStream(resName);
            if (resource != null)
            {
                ArrayByte result = new ArrayByte();
                try
                {
                    result.Write(resource);
                    resource.Close();
                    result.Reset();
                }
                catch (IOException)
                {
                    result = null;
                }
                return result;
            }
            return null;
        }

        public static InputStream GetResourceAsStream(string fileName)
        {
            return new ByteArrayInputStream(GetResource(fileName).GetData());
        }

        public static byte[] GetDataSource(InputStream ins)
        {
            if (ins == null)
            {
                return null;
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[8192];
            try
            {
                int read;
                while ((read = ins.Read(bytes)) >= 0)
                {
                    byteArrayOutputStream.Write(bytes, 0, read);
                }
                bytes = byteArrayOutputStream.ToByteArray();
            }
            catch (IOException)
            {
                return null;
            }
            finally
            {
                try
                {
                    if (byteArrayOutputStream != null)
                    {
                        byteArrayOutputStream.Flush();
                        byteArrayOutputStream = null;
                    }
                    if (ins != null)
                    {
                        ins.Close();
                        ins = null;
                    }
                }
                catch (IOException)
                {
                }
            }
            return bytes;
        }
    }


}
