using System.IO;
using System;
using System.Net;
using System.Text;
using Loon.Core;
using Loon.Utils.Debug;

namespace Loon.Net
{
    public class WebHelper
    {
    
        public static Stream OpenStream(Uri uri)
        {
            WebHelper web = new WebHelper();
            web.SendRequest(uri);
            return web.Stream;
        }

        Exception exception = null;
        Stream stream = null;
        HttpWebRequest webRequest = null;

        public Exception Exception
        {
            get
            {
                return exception;
            }
        }

        public Stream Stream
        {
            get
            {
                return stream;
            }
        }

        public string Method
        {
            get;
            set;
        }

        public string ContentType
        {
            get;
            set;
        }

        public Uri RequestUri
        {
            get
            {
                if (webRequest != null)
                {
                    return webRequest.RequestUri;
                }
                return null;
            }
        }

        public WebHelper()
        {
            Method = "GET";
        }

        public WebHelper(string m)
        {
            Method = m;
        }

        public void SendRequest(Uri uri)
        {
            SendRequest(uri, null);
        }

        public void SendRequest(Uri uri, string data)
        {
            byte[] postData = null;

            if (data != null)
            {
                postData = Encoding.UTF8.GetBytes(data);
            }

            SendRequest(uri, postData, "application/x-www-form-urlencoded");
        }

        public string[] GetHeaderNames()
        {
            return webRequest.Headers.AllKeys;
        }

        public void SendRequest(Uri uri, byte[] postData, string contentType)
        {
            webRequest = (HttpWebRequest)WebRequest.Create(uri);
            webRequest.Method = Method;
            webRequest.UserAgent = UserAgents.RandomHumanAgent();
            webRequest.Accept = "*/*";
            webRequest.AllowAutoRedirect = true;
            if (postData != null)
            {
                webRequest.ContentType = contentType;
                webRequest.BeginGetRequestStream(BeginRequest, postData);
            }
            else
            {
                webRequest.BeginGetResponse(BeginResponse, null);
            }
        }

        void BeginRequest(IAsyncResult ar)
        {
            using (Stream stm = webRequest.EndGetRequestStream(ar))
            {
                var postData = (byte[])ar.AsyncState;
                stm.Write(postData, 0, (int)postData.Length);
                stm.Close();
            }

            webRequest.BeginGetResponse(BeginResponse, null);
        }

         void BeginResponse(IAsyncResult ar)
        {
  
            Stream response = null;
            try
            {
                using (var webResponse = this.webRequest.EndGetResponse(ar))
                {
                    Stream stm = webResponse.GetResponseStream();
                    ContentType = webResponse.ContentType;

                    using (BinaryReader reader = new BinaryReader(stm))
                    {
                        var buffer = new byte[2048];
                        response = new MemoryStream();
                        int bytesRead;

                        do
                        {
                            bytesRead = reader.Read(buffer, 0, buffer.Length);
                            response.Write(buffer, 0, bytesRead);
                        } while (bytesRead > 0);

                        response.Position = 0;
                    }
                    stm.Close();
                    webResponse.Close();
                }
            }
            catch (WebException e)
            {
                Log.Exception(e);
                this.exception = e;
            }
            catch (System.Security.SecurityException e)
            {
                Log.Exception(e);
                this.exception = e;
            }
            this.stream = response;

        }

    }
}
