using System;
using System.IO;
using System.Collections.Generic;
using System.Text;
using System.IO.IsolatedStorage;
using Loon.Java;
using Loon.Core;

namespace Loon.Utils
{
    public sealed class FileUtils
    {
		
        public static void Close(Stream ins)
        {
            if (ins != null)
            {
                try
                {
                    ins.Close();
                }
                catch (IOException e)
                {
                    ClosingFailed(e);
                }
            }
        }

        public static void Close(TextReader reader)
        {
            if (reader != null)
            {
                try
                {
                    reader.Close();
                }
                catch (IOException e)
                {
                    ClosingFailed(e);
                }
            }
        }

        public static void Close(TextWriter writer)
        {
            if (writer != null)
            {
                try
                {
                    writer.Close();
                }
                catch (IOException e)
                {
                    ClosingFailed(e);
                }
            }
        }

        /// <summary>
        /// 关闭指定对象产生异常
        /// </summary>
        ///
        /// <param name="file"></param>
        /// <param name="e"></param>
        public static void ClosingFailed(IOException e)
        {
            throw new Exception(e.Message);
        }

        /// <summary>
        /// 拷贝指定长度数据流
        /// </summary>
        ///
        /// <param name="is"></param>
        /// <param name="os"></param>
        /// <param name="len"></param>
        /// <returns></returns>
        /// <exception cref="IOException"></exception>
        public static long Copy(Stream mask0, Stream os, long len)
        {
            byte[] buf = new byte[1024];
            long copied = 0;
            int read;
            while ((read = mask0.Read(buf, 0, buf.Length)) != 0 && copied < len)
            {
                long leftToCopy = len - copied;
                int toWrite = (read < leftToCopy) ? read : (int)leftToCopy;
                os.Write(buf, 0, toWrite);
                copied += toWrite;
            }
            return copied;
        }

        /// <summary>
        /// 拷贝指定数据流
        /// </summary>
        ///
        /// <param name="in"></param>
        /// <param name="out"></param>
        /// <returns></returns>
        /// <exception cref="IOException"></exception>
        public static long Copy(Stream ins0, Stream xout)
        {
            long written = 0;
            byte[] buffer = new byte[4096];
            while (true)
            {
                int len = ins0.Read(buffer, 0, buffer.Length);
                if (len < 0)
                {
                    break;
                }
                xout.Write(buffer, 0, len);
                written += len;
            }
            return written;
        }


        /// <summary>
        /// 读取指定长度数据流
        /// </summary>
        ///
        /// <param name="is"></param>
        /// <param name="len"></param>
        /// <returns></returns>
        /// <exception cref="IOException"></exception>
        public static byte[] Read(Stream mask, long len)
        {
            MemoryStream xout = new MemoryStream();
            Copy(mask, xout, len);
            return xout.ToArray();
        }

        /// <summary>
        /// 拼装文件名
        /// </summary>
        ///
        /// <param name="name"></param>
        /// <param name="ext"></param>
        /// <returns></returns>
        public static string GetName(string name, string ext)
        {
            return string.Intern((name + "." + ext));
        }

        /// <summary>
        /// 消除文件扩展名
        /// </summary>
        ///
        /// <param name="name"></param>
        /// <returns></returns>
        public static string GetNoExtensionName(string name)
        {
            if (name.IndexOf(".") == -1)
                return name;
            else
                return name.Substring(0, (name.LastIndexOf(GetExtension(name)) - 1) - (0));
        }

        /// <summary>
        /// 获得指定文件大小
        /// </summary>
        ///
        /// <param name="file"></param>
        /// <returns></returns>
        public static long GetKB(FileInfo file)
        {
            return GetKB(file.Length);
        }

        /// <summary>
        /// 将指定长度转化为KB显示
        /// </summary>
        ///
        /// <param name="size"></param>
        /// <returns></returns>
        public static long GetKB(long size)
        {
            size /= 1000L;
            if (size == 0x0L)
            {
                size = 1L;
            }
            return size;
        }

        /// <summary>
        /// 删除指定目录下全部文件
        /// </summary>
        ///
        /// <param name="dir"></param>
        /// <returns></returns>
        public static bool DeleteAll(DirectoryInfo dir)
        {
            string[] fileNames = ToList(dir);
            if (fileNames == null)
                return false;
            for (int i = 0; i < fileNames.Length; i++)
            {
                FileInfo file = new FileInfo(System.IO.Path.Combine(dir.FullName, fileNames[i]));
                if (file.Exists)
                    file.Delete();
                else if (System.IO.Directory.Exists(file.DirectoryName))
                    DeleteAll(file.Directory);
            }
            dir.Delete();
            return true;
        }


        /// <summary>
        /// 读取file
        /// </summary>
        ///
        /// <param name="file"></param>
        /// <returns></returns>
        public static Stream Read(FileInfo file)
        {
            try
            {
                return file.OpenRead();
            }
            catch (FileNotFoundException ex)
            {
                Loon.Utils.Debug.Log.Exception(ex);
                return null;
            }
        }

        /// <summary>
        /// 以指定全路径名读取文件
        /// </summary>
        ///
        /// <param name="fileName"></param>
        /// <returns></returns>
        public static Stream Read(string fileName)
        {
            return Read(new FileInfo(fileName));
        }

        public static void Copy(TextReader from, TextWriter to)
        {
            char[] buffer = new char[8192];
            int charsRead;
            while ((charsRead = from.Read(buffer, 0, buffer.Length)) != -1)
            {
                to.Write(buffer, 0, charsRead);
                to.Flush();
            }
        }

        /// <summary>
        /// 获得指定路径目录列表
        /// </summary>
        ///
        /// <param name="path"></param>
        /// <returns></returns>
        public static string[] ToDirList(FileInfo path)
        {
            string pathName = System.IO.Path.GetFullPath(path.Name);
            string[] fileList;
            if ("".Equals(pathName))
                path = new FileInfo(".");
            else
                path = new FileInfo(pathName);
            // 获得目录文件列表
            if (System.IO.Directory.Exists(path.DirectoryName))
                fileList = ToList(path.Directory);
            else
                return null;
            return fileList;
        }

        /// <summary>
        /// 检查文件是否存在
        /// </summary>
        ///
        /// <param name="file"></param>
        /// <exception cref="IOException"></exception>
        private static void CheckFile(FileInfo file)
        {
            bool exists = file.Exists;
            if (exists && System.IO.Directory.Exists(file.FullName))
            {
                throw new IOException("File " + System.IO.Path.GetFullPath(file.Name)
                        + " is actually not a file.");
            }
        }

        /// <summary>
        /// 获得指定路径目录列表
        /// </summary>
        ///
        /// <param name="path"></param>
        /// <returns></returns>
        public static string[] ToDirList(string pathName)
        {
            return ToDirList(new FileInfo(pathName));
        }

        /// <summary>
        /// 获得指定路径下的所有文件名(包含全路径)
        /// </summary>
        ///
        /// <param name="path">string 指定目录</param>
        /// <returns>ArrayList 所有文件名(包含全路径)</returns>
        /// <exception cref="IOException"></exception>
        public static List<string> GetAllFiles(string path)
        {
            DirectoryInfo file = new DirectoryInfo(path);
            List<string> ret = new List<string>();
            string[] listFile = ToList(file);
            if (listFile != null)
            {
                for (int i = 0; i < listFile.Length; i++)
                {
                    FileInfo tempfile = new FileInfo(path + "/" + listFile[i]);
                    if (System.IO.Directory.Exists(tempfile.DirectoryName))
                    {
                        List<string> arr = GetAllFiles(System.IO.Path.GetFullPath(tempfile.Name));
                        ret.AddRange(arr);
                        arr.Clear();
                        arr = null;
                    }
                    else
                    {
                        ret.Add(tempfile.FullName);

                    }
                }
            }
            return ret;
        }

        /// <summary>
        /// 获得指定路径下的所有目录(包含全路径)
        /// </summary>
        ///
        /// <param name="path">string 指定目录</param>
        /// <returns>ArrayList 所有目录(包含全路径)</returns>
        /// <exception cref="IOException"></exception>
        public static List<string> GetAllDir(string path)
        {
            DirectoryInfo file = new DirectoryInfo(path);
            List<string> ret = new List<string>();
            string[] listFile = ToList(file);
            if (listFile != null)
            {
                for (int i = 0; i < listFile.Length; i++)
                {
                    FileInfo tempfile = new FileInfo(path + "/" + listFile[i]);
                    if (System.IO.Directory.Exists(tempfile.DirectoryName))
                    {
                        ret.Add(tempfile.FullName);
                        List<string> arr = GetAllDir(System.IO.Path.GetFullPath(tempfile.Name));
                        ret.AddRange(arr);
                        arr.Clear();
                        arr = null;

                    }
                }
            }
            return ret;

        }

        /// <summary>
        /// 获得指定路径下指定扩展名的所有文件(包含全路径)
        /// </summary>
        ///
        /// <param name="path">string 指定路径</param>
        /// <param name="ext">string 扩展名</param>
        /// <returns>ArrayList 所有文件(包含全路径)</returns>
        /// <exception cref="IOException"></exception>
        public static List<string> GetAllFiles(string path, string ext)
        {
            DirectoryInfo file = new DirectoryInfo(path);
            List<string> ret = new List<string>();
            string[] exts = StringUtils.Split(ext, ",");
            string[] listFile = ToList(file);
            if (listFile != null)
            {
                for (int i = 0; i < listFile.Length; i++)
                {
                    FileInfo tempfile = new FileInfo(path + "/" + listFile[i]);
                    if (System.IO.Directory.Exists(tempfile.DirectoryName))
                    {
                        List<string> arr = GetAllFiles(System.IO.Path.GetFullPath(tempfile.Name), ext);
                        ret.AddRange(arr);
                        arr.Clear();
                        arr = null;
                    }
                    else
                    {
                        for (int j = 0; j < exts.Length; j++)
                        {
                            if (GetExtension(tempfile.FullName).Equals(exts[j], StringComparison.InvariantCultureIgnoreCase))
                            {
                                ret.Add(tempfile.FullName);
                            }
                        }
                    }
                }
            }
            return ret;
        }

        /// <summary>
        /// 获得指定路径下的文件列表(包含全路径),仅包含一级目录
        /// </summary>
        ///
        /// <param name="path">string 指定路径</param>
        /// <returns>ArrayList 文件名</returns>
        /// <exception cref="IOException"></exception>
        public static List<string> GetFiles(string path)
        {
            DirectoryInfo file = new DirectoryInfo(path);
            List<string> ret = new List<string>();
            string[] listFile = ToList(file);
            if (listFile != null)
            {
                for (int i = 0; i < listFile.Length; i++)
                {
                    FileInfo tempfile = new FileInfo(path + "/" + listFile[i]);

                    if (!System.IO.Directory.Exists(tempfile.DirectoryName))
                    {
                        ret.Add(tempfile.FullName);

                    }

                }
            }
            return ret;
        }

        /// <summary>
        /// 获得指定路径下的子目录(包含全路径),仅包含一级目录
        /// </summary>
        ///
        /// <param name="path">string 指定路径</param>
        /// <returns>ArrayList 子目录</returns>
        /// <exception cref="IOException"></exception>
        public static List<string> GetDir(string path)
        {
            DirectoryInfo file = new DirectoryInfo(path);
            List<string> ret = new List<string>();
            string[] listFile = ToList(file);
            if (listFile != null)
            {
                for (int i = 0; i < listFile.Length; i++)
                {
                    FileInfo tempfile = new FileInfo(path + "/" + listFile[i]);

                    if (System.IO.Directory.Exists(tempfile.DirectoryName))
                    {
                        ret.Add(tempfile.FullName);

                    }
                }
            }
            return ret;
        }

        /// <summary>
        /// 获得指定路径下指定扩展名的文件(包含全路径),仅包含一级目录
        /// </summary>
        ///
        /// <param name="path">string 指定路径</param>
        /// <param name="ext">string 扩展名</param>
        /// <returns>ArrayList 文件名</returns>
        /// <exception cref="IOException"></exception>
        public static List<string> GetFiles(string path, string ext)
        {
            DirectoryInfo file = new DirectoryInfo(path);
            List<string> ret = new List<string>();
            string[] listFile = ToList(file);
            if (listFile != null)
            {
                for (int i = 0; i < listFile.Length; i++)
                {
                    FileInfo tempfile = new FileInfo(path + "/" + listFile[i]);

                    if (!System.IO.Directory.Exists(tempfile.DirectoryName))
                    {
                        if (GetExtension(tempfile.FullName).Equals(ext, StringComparison.InvariantCultureIgnoreCase))
                        {
                            ret.Add(tempfile.FullName);
                        }

                    }
                }
            }
            return ret;
        }

        /// <summary>
        /// 检查指定文件夹是否存在
        /// </summary>
        /// 
        /// <param name="directoryToTest"></param>
        /// <returns></returns>
        public static bool DirectoryExists(string directoryToTest)
        {
            return System.IO.Directory.Exists(directoryToTest);
        }

        /// <summary>
        /// 检查指定文件是否存在
        /// </summary>
        /// 
        /// <param name="directoryToTest"></param>
        /// <returns></returns>
        public static bool FileExists(string directoryToTest)
        {
            return System.IO.File.Exists(directoryToTest);
        }

        /// <summary>
        /// 创建文件夹
        /// </summary>
        /// 
        /// <param name="directoryToCreate"></param>
        public static void CreateDirectory(string directoryToCreate)
        {
            System.IO.Directory.CreateDirectory(directoryToCreate);
        }


        /// <summary>
        /// 获得指定路径的目录名
        /// </summary>
        ///
        /// <param name="name"></param>
        /// <returns></returns>
        public static string GetFileName(string name)
        {
            if (name == null)
            {
                return "";
            }
            int length = name.Length;
            int size = name.LastIndexOf("/") + 1;
            if (size < length)
            {
                return name.Substring(size, (length) - (size));
            }
            else
            {
                return "";
            }
        }

        /// <summary>
        /// 获得指定文件扩展名
        /// </summary>
        ///
        /// <param name="name"></param>
        /// <returns></returns>
        public static string GetExtension(string name)
        {
            if (name == null)
            {
                return "";
            }
            int index = name.LastIndexOf(".");
            if (index == -1)
            {
                return "";
            }
            else
            {
                return name.Substring(index + 1);
            }
        }


        /// <summary>
        /// 删除指定目录下的所有文件
        /// </summary>
        ///
        /// <param name="path">string 指定目录</param>
        /// <exception cref="System.Exception"></exception>
        public static void DeleteFile(string path)
        {
            DirectoryInfo file = new DirectoryInfo(path);
            string[] listFile = ToList(file);
            if (listFile != null)
            {
                for (int i = 0; i < listFile.Length; i++)
                {
                    FileInfo tempfile = new FileInfo(path + "/" + listFile[i]);
                    // 如果是目录
                    if (DirectoryExists(tempfile.DirectoryName))
                    {
                        DeleteFile(System.IO.Path.GetFullPath(tempfile.Name));
                    }
                    else
                    { // 如果不是
                        tempfile.Delete();

                    }
                }
            }
        }

        /// <summary>
        /// 删除指定目录下的所有目录
        /// </summary>
        ///
        /// <param name="path">string 指定目录</param>
        /// <exception cref="System.Exception"></exception>
        public static void DeleteDir(string path)
        {
            DirectoryInfo file = new DirectoryInfo(path);
            string[] listFile = ToList(file);
            if (listFile != null)
            {
                for (int i = 0; i < listFile.Length; i++)
                {
                    FileInfo tempfile = new FileInfo(path + "/" + listFile[i]);
                    if (DirectoryExists(tempfile.DirectoryName))
                    {
                        DeleteDir(System.IO.Path.GetFullPath(tempfile.Name));
                        tempfile.Delete();
                    }
                }
            }

        }

        private static string[] ToList(DirectoryInfo file)
        {
            FileInfo[] files = file.GetFiles();
            int size = files.Length;
            List<string> lists = new List<string>(size);
            for (int i = 0; i < size; i++)
            {
                lists.Add(files[i].Name);
            }
            return lists.ToArray();
        }

    }
}
