using Loon.Utils;
using System;
using Loon.Core.Graphics.Device;
using Microsoft.Xna.Framework.Graphics;
using Loon.Core.Resource;
using Microsoft.Xna.Framework;
using Loon.Java;
namespace Loon.Core.Graphics.Opengl
{

    public class GLLoader : LTextureData
    {

        public static void Destory()
        {
            LTextureBatch.ClearBatchCaches();
        }

        public static LTextureData GetTextureData(InputStream ins)
        {
            return new GLLoader(ins);
        }

        public static LTextureData GetTextureData(LImage img)
        {
            return new GLLoader(img);
        }

        public static LTextureData GetTextureData(string resName)
        {
            return new GLLoader(resName);
        }

        private GLLoader(InputStream ins)
        {
            Create(LImage.CreateImage(ins));
            this.isExt = true;
        }

        private GLLoader(string resName)
        {
            Create(LImage.CreateImage(resName));
        }

        private GLLoader(LImage img)
        {
            Create(img);
        }

        private GLLoader(LTextureData data, bool newCopy)
        {
            this.width = data.width;
            this.height = data.height;
            this.texWidth = data.texWidth;
            this.texHeight = data.texHeight;
            this.isExt = data.isExt;
            this.hasAlpha = data.hasAlpha;
            if (newCopy)
            {
                Color[] colors = new Color[width * height];
                data.buffer.GetData<Color>(colors);
                Texture2D texture = new Texture2D(GL.device, width, height);
                texture.SetData<Color>(colors);
                this.buffer = texture;
                colors = null;
            }
            else
            {
                this.buffer = data.buffer;
            }
            this.fileName = data.fileName;
        }

        private void Create(LImage image)
        {

            if (image == null)
            {
                return;
            }
            if (buffer != null)
            {
                return;
            }

            fileName = image.GetPath();


            this.isExt = image.isExt;
            this.hasAlpha = image.hasAlpha;
            int srcWidth = image.Width;
            int srcHeight = image.Height;

            if (GLEx.IsPowerOfTwo(srcWidth) && GLEx.IsPowerOfTwo(srcHeight))
            {
                this.width = srcWidth;
                this.height = srcHeight;
                this.texHeight = srcHeight;
                this.texWidth = srcWidth;
                this.buffer = image.GetBitmap();
                if (image.IsAutoDispose())
                {
                    image.Dispose();
                    image = null;
                }
                return;
            }

            int texWidth = GLEx.ToPowerOfTwo(srcWidth);
            int texHeight = GLEx.ToPowerOfTwo(srcHeight);

            this.width = srcWidth;
            this.height = srcHeight;
            this.texHeight = texHeight;
            this.texWidth = texWidth;
            Color[] src = image.GetPixels();
            Color[] dst = new Color[texWidth * texHeight];
            for (int size = 0; size < height; size++)
            {
                Array.Copy(src, size * width
                        , dst, size * texWidth, width);
            }
            if (buffer == null)
            {
                buffer = new Texture2D(GL.device, texWidth, texHeight);
            }
            buffer.SetData<Color>(dst);
            src = null;
            dst = null;
            if (image != null && image.IsAutoDispose())
            {
                image.Dispose();
                image = null;
            }
        }

        public static void CopyArea(LImage image, LGraphics g, int x, int y, int width, int height, int dx, int dy)
        {
            LImage tmp = image.GetSubImage(x, y, width, height);
            g.DrawImage(tmp, x + dx, y + dy);
            tmp.Dispose();
            tmp = null;
        }

        public override LTextureData Copy()
        {
            return new GLLoader(this, true);
        }

        public override void CreateTexture()
        {

        }

    }
}
