using Loon.Core.Graphics.Opengl;
using Loon.Core.Graphics;
using Loon.Core.Graphics.Device;
using Microsoft.Xna.Framework;
using Loon.Core;
using System;
namespace Loon.Utils
{
    public class TextureUtils
    {

        public static LTexture FilterColor(string res, LColor height)
        {
            return TextureUtils.FilterColor(res, height, Loon.Core.Graphics.Opengl.LTexture.Format.DEFAULT);
        }

        public static LTexture FilterColor(string res, LColor height, Loon.Core.Graphics.Opengl.LTexture.Format format)
        {
            uint color = height.GetRGB();
            LImage tmp = LImage.CreateImage(res);
            LImage image = LImage.CreateImage(tmp.GetWidth(), tmp.GetHeight(), true);
            LGraphics g = image.GetLGraphics();
            g.DrawImage(tmp, 0, 0);
            g.Dispose();
            if (tmp != null)
            {
                tmp.Dispose();
                tmp = null;
            }
            Color[] pixels = image.GetPixels();
            int size = pixels.Length;
            for (int i = 0; i < size; i++)
            {
                if (pixels[i].PackedValue == color)
                {
                    pixels[i].PackedValue = LSystem.TRANSPARENT;
                }
            }
            image.SetFormat(format);
            image.SetPixels(pixels, image.GetWidth(), image.GetHeight());
            LTexture texture = image.GetTexture();
            if (image != null)
            {
                image.Dispose();
                image = null;
            }
            return texture;
        }

        public static LTexture FilterLimitColor(string res, LColor start, LColor end)
        {
            return TextureUtils.FilterLimitColor(res, start, end, Loon.Core.Graphics.Opengl.LTexture.Format.DEFAULT);
        }

        public static LTexture FilterLimitColor(string res, LColor start,
                LColor end, Loon.Core.Graphics.Opengl.LTexture.Format format)
        {
            int sred = start.R;
            int sgreen = start.G;
            int sblue = start.B;
            int ered = end.R;
            int egreen = end.G;
            int eblue = end.B;
            LImage tmp = LImage.CreateImage(res);
            LImage image = LImage.CreateImage(tmp.GetWidth(), tmp.GetHeight(), true);
            LGraphics g = image.GetLGraphics();
            g.DrawImage(tmp, 0, 0);
            g.Dispose();
            if (tmp != null)
            {
                tmp.Dispose();
                tmp = null;
            }
            Color[] pixels = image.GetPixels();
            int size = pixels.Length;
            for (int i = 0; i < size; i++)
            {
                Color pixel = pixels[i];
                if ((pixel.R >= sred && pixel.G >= sgreen && pixel.B >= sblue)
                        && (pixel.R <= ered && pixel.G <= egreen && pixel.B <= eblue))
                {
                    pixels[i].PackedValue = LSystem.TRANSPARENT;
                }
            }
            image.SetFormat(format);
            image.SetPixels(pixels, image.GetWidth(), image.GetHeight());
            LTexture texture = image.GetTexture();
            if (image != null)
            {
                image.Dispose();
                image = null;
            }
            return texture;
        }

        public static LTexture FilterColor(string res, Color[] colors)
        {
            return TextureUtils.FilterColor(res, colors, Loon.Core.Graphics.Opengl.LTexture.Format.DEFAULT);
        }

        public static LTexture FilterColor(string res, Color[] colors, Loon.Core.Graphics.Opengl.LTexture.Format format)
        {
            LImage tmp = LImage.CreateImage(res);
            LImage image = LImage.CreateImage(tmp.GetWidth(), tmp.GetHeight(), true);
            LGraphics g = image.GetLGraphics();
            g.DrawImage(tmp, 0, 0);
            g.Dispose();
            if (tmp != null)
            {
                tmp.Dispose();
                tmp = null;
            }
            Color[] pixels = image.GetPixels();
            int size = pixels.Length;
            for (int i = 0; i < size; i++)
            {
                for (int j = 0; j < colors.Length; j++)
                {
                    if (pixels[i].Equals(colors[j]))
                    {
                        pixels[i].PackedValue = LSystem.TRANSPARENT;
                    }
                }
            }
            image.SetFormat(format);
            image.SetPixels(pixels, image.GetWidth(), image.GetHeight());
            LTexture texture = image.GetTexture();
            if (image != null)
            {
                image.Dispose();
                image = null;
            }
            return texture;
        }

        public static LTexture LoadTexture(string fileName)
        {
            return LTextures.LoadTexture(fileName);
        }

        public static LTexture[] GetSplitTextures(string fileName, int width,
                int height)
        {
            return GetSplitTextures(LTextures.LoadTexture(fileName), width, height);
        }

        public static LTexture[] GetSplitTextures(LTexture image, int width,
                int height)
        {
            if (image == null)
            {
                return null;
            }
            if (!image.IsLoaded())
            {
                image.LoadTexture();
            }
            int frame = 0;
            int wlength = image.GetWidth() / width;
            int hlength = image.GetHeight() / height;
            int total = wlength * hlength;
            LTexture[] images = new LTexture[total];
            for (int y = 0; y < hlength; y++)
            {
                for (int x = 0; x < wlength; x++)
                {
                    images[frame] = image.GetSubTexture((x * width), (y * height),
                            width, height);
                    frame++;
                }
            }
            return images;
        }

        public static LTexture[][] GetSplit2Textures(string fileName, int width,
                int height)
        {
            return GetSplit2Textures(LTextures.LoadTexture(fileName), width, height);
        }

        public static LTexture[][] GetSplit2Textures(LTexture image, int width,
                int height)
        {
            if (image == null)
            {
                return null;
            }
            if (!image.IsLoaded())
            {
                image.LoadTexture();
            }
            int wlength = image.GetWidth() / width;
            int hlength = image.GetHeight() / height;
            LTexture[][] textures = (LTexture[][])CollectionUtils.XNA_CreateJaggedArray(typeof(LTexture), wlength, hlength);
            for (int y = 0; y < hlength; y++)
            {
                for (int x = 0; x < wlength; x++)
                {
                    textures[x][y] = image.GetSubTexture((x * width), (y * height),
                            width, height);
                }
            }
            return textures;
        }

        public static LTexture[] GetDivide(string fileName, int count, int[] width,
                int[] height)
        {
            if (count <= 0)
            {
                throw new Exception("Divide");
            }
            LTexture image = LTextures.LoadTexture(fileName);
            if (image == null)
            {
                return null;
            }
            if (!image.IsLoaded())
            {
                image.LoadTexture();
            }

            if (width == null)
            {
                width = new int[count];
                int w = image.GetWidth();
                for (int j = 0; j < count; j++)
                {
                    width[j] = w / count;
                }
            }
            if (height == null)
            {
                height = new int[count];
                int h = image.GetHeight();
                for (int i = 0; i < count; i++)
                {
                    height[i] = h;
                }
            }
            LTexture[] images = new LTexture[count];
            int offsetX = 0;
            for (int i = 0; i < count; i++)
            {
                images[i] = image.GetSubTexture(offsetX, 0, width[i], height[i]);
                offsetX += width[i];
            }
            return images;
        }

        public static LTexture[] GetDivide(string fileName, int count)
        {
            return GetDivide(fileName, count, null, null);
        }

        public static LTexture CreateTexture(int width, int height, LColor c)
        {
            LImage image = LImage.CreateImage(width, height, false);
            LGraphics g = image.GetLGraphics();
            g.SetColor(c);
            g.FillRect(0, 0, width, height);
            g.Dispose();
            LTexture tex2d = image.GetTexture();
            if (image != null)
            {
                image.Dispose();
                image = null;
            }
            return tex2d;
        }

    }
}
