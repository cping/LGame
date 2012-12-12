using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework;
using Loon.Utils;
using Loon.Core.Graphics.Opengl;

namespace Loon.Core.Graphics
{
    public class LPixmapData
    {

        private int width, height;

        private bool hasAlpha;

        private bool isDirty, isClose;

        private Color[] pixels, finalPixels;

        private LImage buffer;

        public LPixmapData(int width, int height)
        {
            init(width, height, true);
        }

        public LPixmapData(int width, int height, bool alpha)
        {
            init(width, height, alpha);
        }

        private void init(int width, int height, bool alpha)
        {
            this.width = width;
            this.height = height;
            this.hasAlpha = alpha;
            this.buffer = LImage.CreateImage(width, height, alpha);
            this.pixels = buffer.GetPixels();
            this.finalPixels = (Color[])CollectionUtils.CopyOf(pixels);
        }

        private LPixmapData(Color[] pixels, int width, int height)
        {
            init(width, height, true);
            this.buffer.SetPixels(pixels, width, height);
            this.finalPixels = (Color[])CollectionUtils.CopyOf(pixels);
        }

        public LPixmapData(string resName)
            : this(LImage.CreateImage(resName))
        {

        }

        public LPixmapData(LImage pix)
            : this(pix.GetPixels(), pix.GetWidth(), pix.GetHeight())
        {

        }

        public int GetWidth()
        {
            return width;
        }

        public int GetHeight()
        {
            return height;
        }

        public Color[] GetPixels()
        {
            return pixels;
        }

        public int Size()
        {
            return width * height;
        }

        public uint Get(int x, int y)
        {
            if (x >= 0 && y >= 0 && x < width && y < height)
            {
                return pixels[x + y * width].PackedValue;
            }
            else
            {
                return 0;
            }
        }

        public void Submit()
        {
            if (!isDirty)
            {
                if (buffer == null)
                {
                    return;
                }
                lock (buffer)
                {
                    buffer.SetPixels(pixels, width, height
                        );
                }
                isDirty = true;
            }
        }

        public void Put(int x, int y, uint color)
        {
            if (x > -1 && y > -1 && x < width && y < height)
            {
                pixels[x + y * width].PackedValue = color;
            }
        }

        public void Put(int index, uint color)
        {
            pixels[index].PackedValue = color;
        }

        public void Reset()
        {
            if (isClose)
            {
                return;
            }
            this.pixels = (Color[])CollectionUtils.CopyOf(finalPixels);
        }

        public bool IsDirty()
        {
            return isDirty;
        }

        public void Draw(GLEx g, float x, float y, float w, float h)
        {
            if (isClose)
            {
                return;
            }
            lock (buffer)
            {
                g.DrawTexture2D(buffer.GetBitmap(), x, y, w, h);
            }
        }

        public void Draw(GLEx g, float x, float y)
        {
            Draw(g, x, y, width, height);
        }

        public bool IsClose()
        {
            return this.isClose;
        }

        public void Dispose()
        {
            this.isClose = true;
            if (buffer != null)
            {
                buffer.Dispose();
                buffer = null;
            }
        }
    }
}
