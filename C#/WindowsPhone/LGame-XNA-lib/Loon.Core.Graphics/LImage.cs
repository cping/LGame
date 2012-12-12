using Microsoft.Xna.Framework.Graphics;
using Loon.Java;
using System;
using Loon.Core.Graphics.Opengl;
using Loon.Core.Graphics.Device;
using Loon.Utils;
using Loon.Core.Resource;
using Microsoft.Xna.Framework;
using System.IO;
using System.Collections.Generic;
namespace Loon.Core.Graphics
{
    public class LImage : LRelease
    {
        internal void XNAUpdateAlpha(byte alpha)
        {
            Color[] pixels = new Color[width * height];
            m_data.GetData<Color>(pixels);
            for (int i = 0; i < pixels.Length; i++)
            {
                pixels[i].A = alpha;
            }
            m_data.SetData<Color>(pixels);
        }

        public int Width
        {
            get
            {
                return width;
            }
        }

        public int Height
        {
            get
            {
                return height;
            }
        }


        private Loon.Core.Graphics.Opengl.LTexture.Format m_format;
        private bool bMutable;
        internal Texture2D m_data;
        internal bool isExt, isAutoDispose, isUpdate, isClose, hasAlpha;
        internal int width, height;
        string fileName;

        private static List<LImage> images = new List<LImage>(100);

        private void Init()
        {
            if (!images.Contains(this))
            {
                images.Add(this);
            }
            this.bMutable = true;
        }

        private LImage()
        {
            Init();
        }

        public LImage(string resName)
        {
            string extName = FileUtils.GetExtension(resName);
            if ("".Equals(extName) || "xna".Equals(extName, StringComparison.InvariantCultureIgnoreCase))
            {
                isExt = false;
            }
            else
            {
                isExt = true;
            }
            string newPath = resName;
            if (newPath[0] == '/')
            {
                newPath = newPath.Substring(1);
            }
            if (isExt)
            {
                m_data = Texture2D.FromStream(GL.device, Resources.OpenStream(newPath));
                width = m_data.Width;
                height = m_data.Height;
            }
            else
            {
                if (newPath.IndexOf("/") == -1 && (FileUtils.GetExtension(newPath).Length == 0))
                {
                    newPath = "Content/" + newPath;
                }
                m_data = LSystem.screenActivity.GameRes.Load<Texture2D>(StringUtils.ReplaceIgnoreCase(newPath, ".xnb", ""));
                width = m_data.Width;
                height = m_data.Height;
            }
            fileName = newPath;
            CheckAlpha();
            Init();
        }

        public LImage(Texture2D tex2d)
        {
            m_data = tex2d;
            width = tex2d.Width;
            height = tex2d.Height;
            CheckAlpha();
            Init();
        }

        public static LImage CreateImage(int width, int height)
        {
            return CreateImage(width, height, SurfaceFormat.Color);
        }

        public static LImage CreateImage(int width, int height, SurfaceFormat format)
        {
            LImage image = new LImage();
            image.m_data = new RenderTarget2D(GL.device, width, height, false, format, DepthFormat.None, 0, RenderTargetUsage.PreserveContents);
            image.width = image.m_data.Width;
            image.height = image.m_data.Height;
            image.CheckAlpha();
            return image;
        }

        public static LImage CreateImage(LImage image, int x, int y, int width, int height, int transform)
        {
            LImage result = new LImage();
            if (transform < 4)
            {
                result.m_data = new Texture2D(GL.device, width, height, false, SurfaceFormat.Color);
            }
            else
            {
                result.m_data = new Texture2D(GL.device, height, width, true, SurfaceFormat.Color);
            }
            result.width = image.m_data.Width;
            result.height = image.m_data.Height;
            result.CheckAlpha();
            return result;
        }

        public static LImage CreateImage(byte[] buffer)
        {
            MemoryStream stream = new MemoryStream(buffer);
            return CreateImage(stream);
        }

        public static LImage CreateImage(Color[] pixels, int width, int height, bool hasAlpha)
        {
            LImage image = new LImage();
            image.hasAlpha = hasAlpha;
            image.width = width;
            image.height = height;
            int size = width * height;
            Texture2D texture = new Texture2D(GL.device, width, height);
            texture.SetData<Color>(pixels);
            image.m_data = texture;
            return image;
        }

        public static LImage CreateImage(int width, int height, bool hasAlpha)
        {
            LImage image = new LImage();
            image.hasAlpha = hasAlpha;
            image.width = width;
            image.height = height;
            int size = width * height;

            Color[] pixels = new Color[size];
            if (!hasAlpha)
            {
                for (int i = 0; i < size; i++)
                {
                    pixels[i] = Color.Black;
                }
            }

            Texture2D texture = new Texture2D(GL.device, width, height);
            texture.SetData<Color>(pixels);
            image.m_data = texture;
            return image;
        }

        internal static LImage NewImage(Texture2D tex2d)
        {
            int size = tex2d.Width * tex2d.Height;
            Color[] pixels = new Color[size];
            tex2d.GetData<Color>(pixels);

            LImage image = new LImage();
            Texture2D newTex2d = new Texture2D(GL.device, tex2d.Width, tex2d.Height);
            newTex2d.SetData<Color>(pixels);
            image.m_data = newTex2d;
            image.width = image.m_data.Width;
            image.height = image.m_data.Height;
            image.CheckAlpha();

            return image;
        }

        public static LImage CreateImage(InputStream ins)
        {
            LImage image = null;
            try
            {
                image = new LImage();
                image.m_data = Texture2D.FromStream(GL.device, ins);
                image.width = image.m_data.Width;
                image.height = image.m_data.Height;
                image.isExt = true;
                image.CheckAlpha();
            }
            catch (Exception ex)
            {
                Loon.Utils.Debug.Log.Exception(ex);
            }
            finally
            {
                if (ins != null)
                {
                    try
                    {
                        ins.Close();
                        ins = null;
                    }
                    catch (Exception)
                    {
                    }
                }
            }
            return image;
        }

        public static LImage CreateImage(Texture2D tex2d)
        {
            return new LImage(tex2d);
        }

        public static LImage CreateImage(string resName)
        {
            return new LImage(resName);
        }

        private void CheckAlpha()
        {
            hasAlpha = m_data.Format == SurfaceFormat.Dxt5 ||
            m_data.Format == SurfaceFormat.Dxt3 || m_data.Format == SurfaceFormat.Alpha8 || m_data.Format == SurfaceFormat.Bgra4444 || m_data.Format == SurfaceFormat.Rgba64;
            if (!hasAlpha)
            {
                int w = width - 1;
                int h = height - 1;
                hasAlpha = GetIntPixel(0, 0) == 0;
                if (!hasAlpha)
                {
                    hasAlpha = GetIntPixel(w, 0) == 0;
                }
                if (!hasAlpha)
                {
                    hasAlpha = GetIntPixel(w, h) == 0;
                }
                if (!hasAlpha)
                {
                    hasAlpha = GetIntPixel(0, h) == 0;
                }
                if (!hasAlpha)
                {
                    hasAlpha = GetIntPixel(w / 2, h / 2) == 0;
                }
            }
        }

        public bool HasAlpha()
        {
            return hasAlpha;
        }

        public Texture2D GetBitmap()
        {
            return m_data;
        }

        public LImage Clone()
        {
            LImage image = new LImage();
            image.width = width;
            image.height = height;
            image.hasAlpha = hasAlpha;
            image.m_data = m_data;
            image.fileName = fileName;
            image.isExt = isExt;
            return image;
        }

        private LTexture _texture;

        public LTexture GetTexture()
        {
            if (_texture == null || _texture.isClose || isUpdate)
            {
                SetAutoDispose(false);
                LTexture tmp = _texture;
                _texture = new LTexture(GLLoader.GetTextureData(this), m_format);
                if (tmp != null)
                {
                    tmp.Dispose();
                    tmp = null;
                }
                isUpdate = false;
            }
            return _texture;
        }

        private LGraphics m_g;

        public LGraphics GetLGraphics()
        {
            if (this.bMutable)
            {
                if (m_g == null || m_g.IsClose())
                {
                    m_g = new LGraphics(this);
                    isUpdate = true;
                }
                return m_g;
            }
            return null;
        }

        public LGraphics Create()
        {
            return new LGraphics(this);
        }

        public Color[] GetPixels()
        {
            Color[] pixels = new Color[width * height];
            m_data.GetData<Color>(pixels);
            return pixels;
        }

        public int[] GetIntPixels()
        {
            int[] pixels = new int[width * height];
            m_data.GetData<int>(pixels);
            return pixels;
        }

        public int[] GetIntPixels(int[] pixels)
        {
            m_data.GetData<int>(pixels);
            return pixels;
        }

        public Color[] GetPixels(Color[] pixels)
        {
            m_data.GetData<Color>(pixels);
            return pixels;
        }

        public int[] GetIntPixels(int x, int y, int w, int h)
        {
            int[] pixels = new int[w * h];
            _pixel_rect.X = x;
            _pixel_rect.Y = y;
            _pixel_rect.Width = w;
            _pixel_rect.Height = h;
            m_data.GetData<int>(0, _pixel_rect, pixels, 0, pixels.Length);
            return pixels;
        }

        public Color[] GetPixels(int x, int y, int w, int h)
        {
            Color[] pixels = new Color[w * h];
            _pixel_rect.X = x;
            _pixel_rect.Y = y;
            _pixel_rect.Width = w;
            _pixel_rect.Height = h;
            m_data.GetData<Color>(0, _pixel_rect, pixels, 0, pixels.Length);
            return pixels;
        }

        public int[] GetIntPixels(int offset, int stride, int x, int y, int w, int h)
        {
            int[] pixels = new int[w * h];
            _pixel_rect.X = x;
            _pixel_rect.Y = y;
            _pixel_rect.Width = w;
            _pixel_rect.Height = h;
            m_data.GetData<int>(0, _pixel_rect, pixels, offset, stride);
            return pixels;
        }

        public Color[] GetPixels(int offset, int stride, int x, int y, int w, int h)
        {
            Color[] pixels = new Color[w * h];
            _pixel_rect.X = x;
            _pixel_rect.Y = y;
            _pixel_rect.Width = w;
            _pixel_rect.Height = h;
            m_data.GetData<Color>(0, _pixel_rect, pixels, offset, stride);
            return pixels;
        }

        public int[] GetPixels(int[] pixels, int offset, int stride, int x, int y,
           int w, int h)
        {
            _pixel_rect.X = x;
            _pixel_rect.Y = y;
            _pixel_rect.Width = w;
            _pixel_rect.Height = h;
            m_data.GetData<int>(0, _pixel_rect, pixels, offset, stride);
            return pixels;
        }

        public Color[] GetPixels(Color[] pixels, int offset, int stride, int x, int y,
            int w, int h)
        {
            _pixel_rect.X = x;
            _pixel_rect.Y = y;
            _pixel_rect.Width = w;
            _pixel_rect.Height = h;
            m_data.GetData<Color>(0, _pixel_rect, pixels, offset, stride);
            return pixels;
        }

        public int[] GetIntPixels(int[] pixels, int offset, int stride, int x, int y,
        int w, int h)
        {
            _pixel_rect.X = x;
            _pixel_rect.Y = y;
            _pixel_rect.Width = w;
            _pixel_rect.Height = h;
            m_data.GetData<int>(0, _pixel_rect, pixels, offset, stride);
            return pixels;
        }

        public Color[] GetRGB(Color[] pixels, int offset, int stride, int x, int y,
                int w, int h)
        {
            _pixel_rect.X = x;
            _pixel_rect.Y = y;
            _pixel_rect.Width = w;
            _pixel_rect.Height = h;
            m_data.GetData<Color>(0, _pixel_rect, pixels, offset, stride);
            return pixels;
        }

        public void GetIntRGB(int startX, int startY, int w, int h, ref int[] rgbArray,
            int offset, int scansize)
        {
            _pixel_rect.X = startX;
            _pixel_rect.Y = startY;
            _pixel_rect.Width = w;
            _pixel_rect.Height = h;
            m_data.GetData<int>(0, _pixel_rect, rgbArray, offset, scansize);
        }

        public void GetRGB(int startX, int startY, int w, int h, ref Color[] rgbArray,
            int offset, int scansize)
        {
            _pixel_rect.X = startX;
            _pixel_rect.Y = startY;
            _pixel_rect.Width = w;
            _pixel_rect.Height = h;
            m_data.GetData<Color>(0, _pixel_rect, rgbArray, offset, scansize);
        }

        public Color GetRGB(int x, int y)
        {
            return GetPixel(x, y);
        }

        public void SetIntPixels(int[] pixels)
        {
            isUpdate = true;
            m_data.SetData<int>(pixels);
        }

        public void SetPixels(Color[] pixels)
        {
            isUpdate = true;
            m_data.SetData<Color>(pixels);
        }

        public void SetPixels(int[] pixels, int w, int h)
        {
            isUpdate = true;
            _pixel_rect.X = 0;
            _pixel_rect.Y = 0;
            _pixel_rect.Width = w;
            _pixel_rect.Height = h;
            m_data.SetData<int>(0, _pixel_rect, pixels, 0, pixels.Length);
        }

        public void SetPixels(Color[] pixels, int w, int h)
        {
            isUpdate = true;
            _pixel_rect.X = 0;
            _pixel_rect.Y = 0;
            _pixel_rect.Width = w;
            _pixel_rect.Height = h;
            m_data.SetData<Color>(0, _pixel_rect, pixels, 0, pixels.Length);
        }

        public void SetIntPixels(int[] pixels, int offset, int stride, int x, int y,
                int w, int h)
        {
            isUpdate = true;
            _pixel_rect.X = x;
            _pixel_rect.Y = y;
            _pixel_rect.Width = w;
            _pixel_rect.Height = h;
            m_data.SetData<int>(0, _pixel_rect, pixels, offset, stride);
        }

        public void SetPixels(Color[] pixels, int offset, int stride, int x, int y,
                int w, int h)
        {
            isUpdate = true;
            _pixel_rect.X = x;
            _pixel_rect.Y = y;
            _pixel_rect.Width = w;
            _pixel_rect.Height = h;
            m_data.SetData<Color>(0, _pixel_rect, pixels, offset, stride);
        }

        public void SetIntRGB(int startX, int startY, int w, int h, int[] rgbArray,
                int offset, int scansize)
        {
            SetIntPixels(rgbArray, offset, scansize, startX, startY, w, h);
        }

        public void SetRGB(int startX, int startY, int w, int h, Color[] rgbArray,
                int offset, int scansize)
        {
            SetPixels(rgbArray, offset, scansize, startX, startY, w, h);
        }

        public int[] SetIntPixels(int[] pixels, int x, int y, int w, int h)
        {
            isUpdate = true;
            _pixel_rect.X = x;
            _pixel_rect.Y = y;
            _pixel_rect.Width = w;
            _pixel_rect.Height = h;
            m_data.SetData<int>(0, _pixel_rect, pixels, 0, pixels.Length);
            return pixels;
        }

        public Color[] SetPixels(Color[] pixels, int x, int y, int w, int h)
        {
            isUpdate = true;
            _pixel_rect.X = x;
            _pixel_rect.Y = y;
            _pixel_rect.Width = w;
            _pixel_rect.Height = h;
            m_data.SetData<Color>(0, _pixel_rect, pixels, 0, pixels.Length);
            return pixels;
        }

        private Rectangle _pixel_rect = new Rectangle(0, 0, 1, 1);

        private Color[] color_data = new Color[1];

        public Color GetPixel(int x, int y)
        {
            if (this.m_data.Format != SurfaceFormat.Color)
            {
                return Color.Black;
            }
            _pixel_rect.X = x;
            _pixel_rect.Y = y;
            _pixel_rect.Width = 1;
            _pixel_rect.Height = 1;
            this.m_data.GetData<Color>(0, _pixel_rect, color_data, 0, 1);
            return color_data[0];
        }

        private int[] int_data = new int[1];
        
        public int GetIntPixel(int x, int y)
        {
            _pixel_rect.X = x;
            _pixel_rect.Y = y;
            _pixel_rect.Width = 1;
            _pixel_rect.Height = 1;
            this.m_data.GetData<int>(0, _pixel_rect, int_data, 0, 1);
            return int_data[0];
        }

        public void SetPixel(Color c, int x, int y)
        {
            isUpdate = true;
            _pixel_rect.X = x;
            _pixel_rect.Y = y;
            _pixel_rect.Width = 1;
            _pixel_rect.Height = 1;
            this.m_data.SetData<Color>(0, _pixel_rect, new Color[1] { c }, 0, 1);
        }

        public void SetPixel(uint rgb, int x, int y)
        {
            isUpdate = true;
            _pixel_rect.X = x;
            _pixel_rect.Y = y;
            _pixel_rect.Width = 1;
            _pixel_rect.Height = 1;
            Color c = new Color();
            c.PackedValue = rgb;
            this.m_data.SetData<Color>(0, _pixel_rect, new Color[1] { c }, 0, 1);
        }

        public void SetRGB(uint rgb, int x, int y)
        {
            SetPixel(rgb, x, y);
        }

        public LColor GetColorAt(int x, int y)
        {
            return new LColor(GetPixel(x, y));
        }

        public uint GetRGBAt(int x, int y)
        {
            if (x >= this.width)
            {
                throw new Exception("X is out of bounds: " + x
                        + "," + this.width);
            }
            else if (y >= this.height)
            {
                throw new Exception("Y is out of bounds: " + y
                        + "," + this.height);
            }
            else if (x < 0)
            {
                throw new Exception("X is out of bounds: " + x);
            }
            else if (y < 0)
            {
                throw new Exception("Y is out of bounds: " + y);
            }
            else
            {
                return GetPixel(x, y).PackedValue;
            }
        }

        public Loon.Core.Graphics.Opengl.LTexture.Format GetFormat()
        {
            return m_format;
        }

        public void SetFormat(Loon.Core.Graphics.Opengl.LTexture.Format format)
        {
            this.m_format = format;
            this.isUpdate = true;
        }

        public bool IsAutoDispose()
        {
            return isAutoDispose && !IsClose();
        }

        public void SetAutoDispose(bool dispose)
        {
            this.isAutoDispose = dispose;
        }

        public bool IsClose()
        {
            return isClose || m_data == null
                    || (m_data != null ? m_data.IsDisposed : false);
        }

        public void Dispose()
        {
            Dispose(true);
        }

        public LImage ScaledInstance(int w, int h)
        {
            int width = GetWidth();
            int height = GetHeight();
            if (width == w && height == h)
            {
                return this;
            }
            return GraphicsUtils.GetResize(this, w, h);
        }

        public LImage GetSubImage(int x, int y, int w, int h)
        {
            return this.GetLGraphics().GetSubImage(x, y, w, h);
        }

        public string GetPath()
        {
            return fileName;
        }

        public int GetWidth()
        {
            return width;
        }

        public int GetHeight()
        {
            return height;
        }

        public void CopyPixelsToBuffer(ByteBuffer buffer)
        {
            sbyte[] m_data = new sbyte[(this.m_data.Width * this.m_data.Height) * 4];
            this.m_data.GetData<sbyte>(m_data);
            buffer.Put(m_data, 0, m_data.Length);
            buffer.Rewind();
        }

        public SurfaceFormat GetConfig()
        {
            return m_data.Format;
        }

        private void Dispose(bool remove)
        {
            isClose = true;
            if (m_data != null && !isUpdate && (_texture == null || _texture.IsClose()))
            {
                m_data.Dispose();
                m_data = null;
            }
            if (_texture != null && isAutoDispose)
            {
                _texture.Dispose();
                _texture = null;
            }
            if (remove)
            {
                CollectionUtils.Remove(images, this);
            }
        }

        public static void DisposeAll()
        {
            if (images.Count > 0)
            {
                foreach (LImage img in images)
                {
                    if (img != null)
                    {
                        img.Dispose(false);
                    }
                }
                CollectionUtils.Clear(images);
            }
        }

    }
}
