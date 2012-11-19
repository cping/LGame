using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework;
using Loon.Utils;
using Loon.Core.Graphics.OpenGL;

namespace Loon.Core.Graphics
{
    public class LPixmapData
    {

        private int width, height;

        private bool hasAlpha;

        private bool isDirty, isClose;

        private Color[] pixels, finalPixels;

        private LPixmap buffer;

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
            this.buffer = new LPixmap(width, height, alpha);
            this.pixels = buffer.GetData();
            this.finalPixels = (Color[])CollectionUtils.CopyOf(pixels);
        }

        private LPixmapData(Color[] pixels, int width, int height)
        {
            init(width, height, true);
            this.buffer.SetData(pixels);
            this.finalPixels = (Color[])CollectionUtils.CopyOf(pixels);
        }

        public LPixmapData(string resName)
            : this(new LPixmap(resName))
        {

        }

        public LPixmapData(LPixmap pix)
            : this(pix.GetData(), pix.GetWidth(), pix.GetHeight())
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
                    buffer.SetData(pixels);
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
                g.DrawTexture(buffer.Texture, x, y, w, h);
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
