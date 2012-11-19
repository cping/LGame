using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Loon.Core.Graphics.OpenGL;
using Loon.Core.Graphics;
using Loon.Core;

namespace Loon.Utils
{
    public sealed class TextureUtils
    {

        private readonly static uint transparent = LSystem.TRANSPARENT;

        public static LTexture FilterColor(string res, LColor col)
        {
            return FilterColor(res, col.Color);
        }

        public static LTexture FilterColor(string res, Color col)
        {
            LPixmap tmp = new LPixmap(res);
            LPixmap image = new LPixmap(tmp.Width, tmp.Height, true);
            image.DrawPixmap(tmp, 0, 0);
            if (tmp != null)
            {
                tmp.Dispose();
                tmp = null;
            }
            Color[] pixels = image.GetData();
            int size = pixels.Length;
            for (int i = 0; i < size; i++)
            {
                if (pixels[i].Equals(col))
                {
                    pixels[i].PackedValue = transparent;
                }
            }
            image.SetData(pixels);
            return image.Texture;
        }

        public static LTexture FilterColor(string res, uint[] colors)
        {
            LPixmap tmp = new LPixmap(res);
            LPixmap image = new LPixmap(tmp.Width, tmp.Height, true);
            image.DrawPixmap(tmp, 0, 0);
            if (tmp != null)
            {
                tmp.Dispose();
                tmp = null;
            }
            Color[] pixels = image.GetData();
            int size = pixels.Length;
            for (int i = 0; i < size; i++)
            {
                for (int j = 0; j < colors.Length; j++)
                {
                    if (pixels[i].PackedValue == colors[j])
                    {
                        pixels[i].PackedValue = transparent;
                    }
                }
            }
            image.SetData(pixels);
            return image.Texture;
        }

        public static LTexture FilterLimitColor(string res, LColor start,
                LColor end)
        {
            int sred = start.R;
            int sgreen = start.G;
            int sblue = start.B;
            int ered = end.R;
            int egreen = end.G;
            int eblue = end.B;
            LPixmap tmp = new LPixmap(res);
            LPixmap image = new LPixmap(tmp.Width, tmp.Height, true);
            image.DrawPixmap(tmp, 0, 0);
            if (tmp != null)
            {
                tmp.Dispose();
                tmp = null;
            }
            Color[] pixels = image.GetData();
            int size = pixels.Length;
            for (int i = 0; i < size; i++)
            {
                Color pixel = pixels[i];
                if ((pixel.R >= sred && pixel.G >= sgreen && pixel.B >= sblue)
                        && (pixel.R <= ered && pixel.G <= egreen && pixel.B <= eblue))
                {
                    pixels[i].PackedValue = transparent;
                }
            }
            return image.Texture;
        }

        public static LTexture LoadTexture(string fileName)
        {
            return LTextures.LoadTexture(fileName);
        }

        public static LTexture[] GetSplitTextures(string fileName, int width, int height)
        {
            return GetSplitTextures(LoadTexture(fileName), width, height);
        }

        public static LTexture[] GetSplitTextures(LTexture image, int width, int height)
        {
            if (image == null)
            {
                return null;
            }
            image.LoadTexture();
            int frame = 0;
            int wlength = image.Width / width;
            int hlength = image.Height / height;
            int total = wlength * hlength;
            LTexture[] images = new LTexture[total];
            for (int y = 0; y < hlength; y++)
            {
                for (int x = 0; x < wlength; x++)
                {
                    images[frame] = image.GetSubTexture((x * width), (y * height), width,
                            height);
                    frame++;
                }
            }
            return images;
        }

        public static LTexture[] GetDivide(string fileName, int count, int[] width,
            int[] height)
        {
            if (count <= 0)
            {
                throw new System.ArgumentException();
            }
            LTexture image = LoadTexture(fileName);
            if (image == null)
            {
                return null;
            }
            image.LoadTexture();
            if (width == null)
            {
                width = new int[count];
                int w = image.Width;
                for (int j = 0; j < count; j++)
                {
                    width[j] = w / count;
                }
            }
            if (height == null)
            {
                height = new int[count];
                int h = image.Height;
                for (int i = 0; i < count; i++)
                {
                    height[i] = h;
                }
            }
            LTexture[] images = new LTexture[count];
            int offsetX = 0;
            for (int i_0 = 0; i_0 < count; i_0++)
            {
                images[i_0] = image.GetSubTexture(offsetX, 0, width[i_0], height[i_0]);
                offsetX += width[i_0];
            }
            return images;
        }

        /// <summary>
        /// 0.3.2版起新增的分割图片方法，成比例切分图片为指定数量
        /// </summary>
        ///
        /// <param name="fileName"></param>
        /// <param name="count"></param>
        /// <returns></returns>
        public static LTexture[] GetDivide(string fileName, int count)
        {
            return GetDivide(fileName, count, null, null);
        }

        public static LTexture CreateTexture(int width, int height, LColor c)
        {
            LPixmap image = new LPixmap(width, height, false);
            image.SetColor(c);
            image.FillRect(0, 0, width, height);
            return image.Texture;
        }
	
        public static LTexture CreateGradientTexture(int width, int height,
            bool alpha, LColor topLeftColor, LColor topRightColor,
            LColor bottomRightColor, LColor bottomLeftColor)
        {
            return CreateGradientTexture(width, height, alpha, topLeftColor.Color, topRightColor.Color, bottomRightColor.Color, bottomLeftColor.Color);
        }

        public static LTexture CreateGradientTexture(int width, int height,
            bool alpha, Color topLeftColor, Color topRightColor,
            Color bottomRightColor, Color bottomLeftColor)
        {
            LPixmap imgProcessor = new LPixmap(width, height,
                    alpha);
            imgProcessor.FourCornersGradient(topLeftColor,
                    topRightColor, bottomRightColor,
                    bottomLeftColor);
            return imgProcessor.Texture;
        }

        public static LTexture[][] GetSplit2Textures(string fileName, int row,
                int col)
        {
            return GetSplit2Textures(LoadTexture(fileName), row, col);
        }

        public static LTexture[][] GetSplit2Textures(LTexture image, int row,
                int col) {
			if (image == null) {
				return null;
			}
			image.LoadTexture();
			
			int wlength = image.Width / row;
			int hlength = image.Height / col;
            LTexture[][] textures = (LTexture[][])CollectionUtils.XNA_CreateJaggedArray(typeof(LTexture), wlength, hlength);
			for (int y = 0; y < hlength; y++) {
				for (int x = 0; x < wlength; x++) {
					textures[x][y] = image.GetSubTexture((x * row), (y * col), row,
							col);
				}
			}
			return textures;
		}

        /// <summary>
        /// 读取指定图像资源，并为该资源生成阴影效果。
        /// </summary>
        ///
        /// <param name="resName"></param>
        /// <param name="angle"></param>
        /// <returns></returns>
        public static LTexture CreateShadowTexture(string resName, float angle)
        {
            return TextureUtils.CreateShadowTexture(LTextures.LoadTexture(resName),
                    0.4f, 1f, angle);
        }

        /// <summary>
        /// 读取指定纹理，并为该资源生成阴影效果。
        /// </summary>
        ///
        /// <param name="texture"></param>
        /// <param name="angle"></param>
        /// <returns></returns>
        public static LTexture CreateShadowTexture(LTexture texture, float angle)
        {
            return TextureUtils.CreateShadowTexture(texture, 0.4f, 1f, angle);
        }
	
        public static LTexture CreateShadowTexture(LTexture texture, float alpha,
                float scale, float angle)
        {
            int width = texture.Width;
            int height = texture.Height;
            LPixmap image = new LPixmap(texture);
            int centerX = width / 2;
            int centerY = height / 2;
            int offsetX = (int)((width - image.Width) / 2);
            int offsetY = (int)((height - image.Height) / 2);
            Loon.Core.Geom.Matrix.Transform2i t = new Loon.Core.Geom.Matrix.Transform2i();
            t.Rotate(angle, centerX, centerY);
            t.Zoom(scale, centerX, centerY);
            LPixmap shadowProcess = new LPixmap(width, height,
                    image.IsAlpha());
            shadowProcess.DrawPixmap(image, offsetX, offsetY);
            shadowProcess.Transparency();
            shadowProcess.Mul(255, 0, 0, 0);
            shadowProcess.Mul((int)(alpha * 255), 255, 255, 255);
            shadowProcess.Convolve(LPixmap.GaussianBlurKernel());
            shadowProcess.Transform(t);
            if (image != null)
            {
                image.Dispose();
                image = null;
            }
            return shadowProcess.Texture;
        }
    }
}
