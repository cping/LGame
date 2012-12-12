using Loon.Core.Geom;
using Loon.Utils;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework;
using System;

namespace Loon.Core.Graphics.Device
{
    public class LGraphics : LGraphicsMath,  LRelease
    {

        public class Transform2i
        {

            public Transform2i()
            {
                this.matrixs = Empty();
            }

            public int[][] matrixs;

            public void Idt()
            {
                this.matrixs = Empty();
            }

            public int Get(int x, int y)
            {
                return matrixs[x][y];
            }

            public void Set(int[][] matrixs_0)
            {
                this.matrixs = matrixs_0;
            }

            public void Mul(int[][] matrixs_0)
            {
                this.matrixs = Mul(matrixs_0, matrixs_0);
            }

            public int[] Mul(int[] fpV)
            {
                return Mul(matrixs, fpV);
            }

            public void Rotate(float alpha, float x, float y)
            {
                if (alpha != 1f)
                {
                    int[][] angle = RotationMatrix(alpha, x, y);
                    this.matrixs = Mul(matrixs, angle);
                }
            }

            public void Zoom(float scale, float x, float y)
            {
                if (scale != 1f)
                {
                    int[][] zoom = ZoomMatrix(scale, x, y);
                    this.matrixs = Mul(matrixs, zoom);
                }
            }

            public static int[][] Empty()
            {
                int[][] id = {
						new int[] {
								Loon.Utils.MathUtils.ONE_FIXED,
								0, 0 },
						new int[] {
								0,
								Loon.Utils.MathUtils.ONE_FIXED,
								0 },
						new int[] {
								0,
								0,
								Loon.Utils.MathUtils.ONE_FIXED } };
                return id;
            }

            public static int[][] Def()
            {
                int[][] id = { new int[] { 0, 0, 0 }, new int[] { 0, 0, 0 },
						new int[] { 0, 0, 0 } };
                return id;
            }

            public static int[][] Mul(int[][] a, int[][] b)
            {
                int[][] matrixs_0 = Def();
                for (int i = 0; i < 3; ++i)
                {
                    for (int j = 0; j < 3; ++j)
                    {
                        for (int n = 0; n < 3; ++n)
                        {
                            matrixs_0[i][j] += Loon.Utils.MathUtils.Mul(a[i][n], b[n][j]);
                        }
                    }
                }
                return matrixs_0;
            }

            public static int[] Mul(int[][] a, int[] b)
            {
                int[] matrixs_0 = { 0, 0, 0 };
                for (int i = 0; i < 3; ++i)
                {
                    for (int j = 0; j < 3; ++j)
                    {
                        matrixs_0[i] += Loon.Utils.MathUtils.Mul(a[i][j], b[j]);
                    }
                }
                return matrixs_0;
            }

            public static int[][] ZoomMatrix(float scale,
                    float x, float y)
            {
                int mu = (0 == scale) ? Int32.MaxValue : Loon.Utils.MathUtils
                        .FromFloat(1 / scale);
                if (Loon.Utils.MathUtils.ONE_FIXED == mu)
                {
                    return Def();
                }
                int x_c = Loon.Utils.MathUtils.FromFloat(x);
                int y_c = Loon.Utils.MathUtils.FromFloat(y);
                int transX = x_c - Loon.Utils.MathUtils.Mul(x_c, mu);
                int transY = y_c - Loon.Utils.MathUtils.Mul(y_c, mu);
                int[][] zoom = {
						new int[] { mu, 0, transX },
						new int[] { 0, mu, transY },
						new int[] {
								0,
								0,
								Loon.Utils.MathUtils.ONE_FIXED } };
                return zoom;
            }

            public static int[][] RotationMatrix(float alpha,
                    float x, float y)
            {
                if (0 == alpha % (2 * Loon.Utils.MathUtils.PI))
                {
                    return Empty();
                }
                int cosAlpha = Loon.Utils.MathUtils.FromDouble(Loon.Utils.MathUtils.Cos(alpha));
                int sinAlpha = Loon.Utils.MathUtils.FromDouble(Loon.Utils.MathUtils.Sin(alpha));
                int x_c = Loon.Utils.MathUtils.FromFloat(x);
                int y_c = Loon.Utils.MathUtils.FromFloat(y);
                int transX = Loon.Utils.MathUtils.Mul(x_c, Loon.Utils.MathUtils.ONE_FIXED
                        - cosAlpha)
                        + Loon.Utils.MathUtils.Mul(y_c, sinAlpha);
                int transY = Loon.Utils.MathUtils.Mul(y_c, Loon.Utils.MathUtils.ONE_FIXED
                        - cosAlpha)
                        - Loon.Utils.MathUtils.Mul(x_c, sinAlpha);
                int[][] angle = {
						new int[] { cosAlpha, -sinAlpha, transX },
						new int[] { sinAlpha, cosAlpha, transY },
						new int[] {
								0,
								0,
								Loon.Utils.MathUtils.ONE_FIXED } };
                return angle;
            }

        }
	


        private interface CircleUpdate
        {
            void NewPoint(int xLeft, int yTop, int xRight, int yBottom);
        }

        private void DrawArcImpl(int[] xPoints, int[] yPoints, int nPoints,
                RectBox bounds, int xLeft, int xRight, int y)
        {
            if (y >= clip.y && y < clip.y + clip.height)
            {
                for (float x = MathUtils.Max(xLeft, clip.x); x <= xRight; x++)
                {
                    if (Contains(xPoints, yPoints, nPoints, bounds, (int)x,
                            (int)y))
                    {
                        DrawPoint((int)x, (int)y);
                    }
                }
            }
        }

        private uint transparent = LSystem.TRANSPARENT;

        private int translateX, translateY;

        private bool isClose;

        private RectBox defClip;

        private RectBox clip;

        private Texture2D store;

        private Color colorValue;

        private Color[] pixels;

        private bool alpha, isDitry;

        private int width, height;

        private int size;

        public LGraphics(LImage buffer)
        {
            this.alpha = true;
            this.width = buffer.width;
            this.height = buffer.height;
            this.size = width * height;
            this.colorValue = new Color(255, 255, 255, 255);
            this.clip = new RectBox(0, 0, width, height);
            this.defClip = new RectBox(0, 0, width, height);
            this.pixels = new Color[size];
            this.store = buffer.m_data;
            store.GetData(pixels);
            this.isDitry = false;
        }

        public int GetColorsCount()
        {
            if (width < 1 || height < 1)
            {
                return 0;
            }
            int[] map = new int[(256 * 256 * 256) / 8];
            int colors = 0;
            for (int i = 0; i < height; i++)
            {
                for (int j = 0; j < width; j++)
                {
                    int rgb = (int)(Get(j, i) & 0x00ffffff);
                    int index = rgb / 8;
                    int bit = 1 << (rgb % 8);
                    if ((map[index] & bit) == 0)
                    {
                        map[index] |= bit;
                        colors++;
                    }
                }
            }
            return colors;
        }

        public void UpdateAlpha(byte alpha)
        {
            for (int i = 0; i < size; i++)
            {
                pixels[i].A = alpha;
            }
            isDitry = true;
        }

        public void SetAlphaValue(byte r, byte g, byte b, byte a)
        {
            uint pixel = LColor.GetARGB(r, g, b, a);
            for (int i = 0; i < size; i++)
            {
                pixels[i].PackedValue = pixel;
            }
            colorValue.PackedValue = pixel;
            this.isDitry = true;
        }

        public void SetAlphaValue(float alpha)
        {
            SetAlphaValue((byte)(255 * alpha));
        }

        public void SetAlphaValue(byte alpha)
        {
            if (alpha < 0 || alpha > 255)
            {
                return;
            }
            UpdatePixels();
            int decrement = 255 - alpha;
            for (int start = 0; start < size; start += width)
            {
                int end = start + width;
                for (int index = start; index < end; ++index)
                {
                    Color pixel = pixels[index];
                    int a = pixel.A;
                    a -= decrement;
                    if (a < 0)
                    {
                        a = 0;
                    }
                    pixel.A = alpha;
                }
            }
            colorValue.A = alpha;
            this.isDitry = true;
        }

        public void SetAlpha(byte alpha)
        {
            colorValue.A = alpha;
        }

        public void SetAlpha(float alpha)
        {
            this.colorValue.A = (byte)(alpha * 255);
        }

        public float GetAlpha()
        {
            return this.colorValue.A / 255;
        }

        public float GetAlphaValue()
        {
            return this.colorValue.A;
        }

        public Color Color
        {
            get
            {
                return this.colorValue;
            }
        }


        public void Translate(int x, int y)
        {
            if (isClose)
            {
                return;
            }

            translateX = x;
            translateY = y;
            if (defClip != null)
            {
                defClip.x += translateX;
                defClip.y += translateY;
                clip.x = MathUtils.Min(clip.x + translateX, width);
                clip.y = MathUtils.Min(clip.y + translateY, height);
                clip.width = (int)MathUtils.Min(clip.width + translateX, width
                        - translateX);
                clip.height = (int)MathUtils.Min(clip.height + translateY, height
                        - translateY);
            }
        }


        public RectBox GetClipBounds()
        {
            if (isClose)
            {
                return null;
            }
            return defClip != null ? new RectBox(defClip.x, defClip.y,
                    defClip.width, defClip.height) : new RectBox(0, 0, width,
                    height);
        }

        public void ClipRect(int x, int y, int width, int height)
        {
            if (isClose)
            {
                return;
            }
            if (defClip != null)
            {
                defClip = defClip.GetIntersection(new RectBox(x, y, width, height));
                clip = clip.GetIntersection(new RectBox(x + translateX, y
                        + translateY, width, height));
            }
            else
            {
                defClip = new RectBox(x, y, width, height);
                clip = new RectBox(x + translateX, y + translateY, width, height);
            }
        }

        public void SetClip(int x, int y, int width, int height)
        {
            if (isClose)
            {
                return;
            }
            if (defClip == null)
            {
                defClip = new RectBox(x, y, width, height);
            }
            else
            {
                defClip.SetBounds(x, y, width, height);
            }
            clip = new RectBox(MathUtils.Max(x + translateX, 0), MathUtils.Max(y
                    + translateY, 0), MathUtils.Min(width, width - translateX),
                    MathUtils.Min(height, height - translateY));
        }

        public RectBox GetClip()
        {
            if (isClose)
            {
                return null;
            }
            return GetClipBounds();
        }

        public void DrawRoundRect(int x, int y, int width, int height,
                int arcWidth, int arcHeight)
        {
            if (isClose)
            {
                return;
            }
            DrawLine(x + arcWidth / 2, y, x + width - arcWidth / 2, y);
            DrawLine(x, y + arcHeight / 2, x, y + height - arcHeight / 2);
            DrawLine(x + arcWidth / 2, y + height, x + width - arcWidth / 2, y
                    + height);
            DrawLine(x + width, y + arcHeight / 2, x + width, y + height
                    - arcHeight / 2);
            DrawArc(x, y, arcWidth, arcHeight, 90, 90);
            DrawArc(x + width - arcWidth, y, arcWidth, arcHeight, 0, 90);
            DrawArc(x, y + height + -arcHeight, arcWidth, arcHeight, 180, 90);
            DrawArc(x + width - arcWidth, y + height + -arcHeight, arcWidth,
                    arcHeight, 270, 90);
        }

        public void FillRoundRect(int x, int y, int width, int height,
                int arcWidth, int arcHeight)
        {
            if (isClose)
            {
                return;
            }
            FillRect(x + arcWidth / 2, y, width - arcWidth + 1, height);
            FillRect(x, y + arcHeight / 2 - 1, arcWidth / 2, height - arcHeight);
            FillRect(x + width - arcWidth / 2, y + arcHeight / 2 - 1, arcWidth / 2,
                    height - arcHeight);

            FillArc(x, y, arcWidth - 1, arcHeight - 1, 90, 90);
            FillArc(x + width - arcWidth, y, arcWidth - 1, arcHeight - 1, 0, 90);
            FillArc(x, y + height + -arcHeight, arcWidth - 1, arcHeight - 1, 180,
                    90);
            FillArc(x + width - arcWidth, y + height + -arcHeight, arcWidth - 1,
                    arcHeight - 1, 270, 90);
        }

        public void Draw3DRect(int x, int y, int width, int height, bool raised)
        {
            if (isClose)
            {
                return;
            }
            Color currentARGB = colorValue;
            LColor col = new LColor(colorValue);
            LColor brighter = col.Brighter();
            LColor darker = col.Darker();
            colorValue = raised ? brighter.Color : darker.Color;
            DrawLine(x, y, x, y + height);
            DrawLine(x + 1, y, x + width - 1, y);
            colorValue = raised ? darker.Color : brighter.Color;
            DrawLine(x + 1, y + height, x + width, y + height);
            DrawLine(x + width, y, x + width, y + height - 1);
            colorValue = currentARGB;
        }

        public void Fill3DRect(int x, int y, int width, int height, bool raised)
        {
            if (isClose)
            {
                return;
            }
            Color currentARGB = colorValue;
            LColor col = new LColor(colorValue);
            LColor brighter = col.Brighter();
            LColor darker = col.Darker();
            if (!raised)
            {
                colorValue = darker.Color;
            }
            FillRect(x + 1, y + 1, width - 2, height - 2);
            colorValue = raised ? brighter.Color : darker.Color;
            DrawLine(x, y, x, y + height - 1);
            DrawLine(x + 1, y, x + width - 2, y);
            colorValue = raised ? darker.Color : brighter.Color;
            DrawLine(x + 1, y + height - 1, x + width - 1, y + height - 1);
            DrawLine(x + width - 1, y, x + width - 1, y + height - 2);
            colorValue = currentARGB;
        }

        private void DrawPoint(Color[] pixels, int pixelIndex, uint c)
        {
            pixels[pixelIndex].PackedValue = c;
        }

        public void DrawImage(LImage image, int x, int y)
        {
            if (image == null)
            {
                return;
            }
            else
            {
                DrawImage(image, x, y, image.width, image.height, 0, 0, image.width, image.height);
                return;
            }
        }

        public LImage GetSubImage(int x, int y, int w, int h)
        {
            return Copy(x, y, w, h);
        }

        public LImage Copy(int x, int y, int w, int h)
        {
            if (isClose)
            {
                return null;
            }
            LImage pixel = LImage.CreateImage(w, h, alpha);
            pixel.width = w;
            pixel.height = h;
            LGraphics g = pixel.GetLGraphics();
            if (x < 0)
            {
                w -= x;
                x = 0;
            }
            if (y < 0)
            {
                h -= y;
                y = 0;
            }
            if (x + w > width)
            {
                w -= (x + w) - width;
            }
            if (y + h > height)
            {
                h -= (y + h) - height;
            }
            try
            {
                for (int size = 0; size < h; size++)
                {
                    Array.Copy(pixels, (y + size) * width + x,
                            g.pixels, size * pixel.width, w);
                }
            }
            catch (Exception)
            {
            }
            g.Dispose();
            return pixel;
        }

        public LImage[] Split(int row, int col)
        {
            if (isClose)
            {
                return null;
            }
            int count = row * col;
            int w = width / row;
            int h = height / col;

            LImage[] pixels = new LImage[count];
            for (int i = 0; i < count; i++)
            {
                int x = (i % row) * w;
                int y = (i / row) * h;
                pixels[i] = Copy(x, y, w, h);
            }

            return pixels;
        }

        private void DrawImage(LGraphics pixel, int x, int y, int w, int h,
                int offsetX, int offsetY)
        {
            if (isClose)
            {
                return;
            }
            pixel.UpdatePixels();

            x += translateX;
            y += translateY;

            Color[] currentPixels = pixel.pixels;
            uint transparent = pixel.transparent;
            if (x < 0)
            {
                w += x;
                offsetX -= x;
                x = 0;
            }
            if (y < 0)
            {
                h += y;
                offsetY -= y;
                y = 0;
            }
            if (x + w > width)
            {
                w = width - x;
            }
            if (y + h > height)
            {
                h = height - y;
            }
            if (w < 0 || h < 0)
            {
                return;
            }
            if (transparent < 0)
            {
                for (int size = 0; size < h; size++)
                {
                    Array.Copy(currentPixels, (offsetY + size) * pixel.width
                            + offsetX, pixels, (y + size) * width + x, w);
                }
            }
            else
            {
                int findIndex = y * width + x;
                int drawIndex = offsetY * pixel.width + offsetX;
                int moveFind = width - w;
                int moveDraw = pixel.width - w;
                for (int i = 0; i < h; i++)
                {
                    for (int j = 0; j < w; )
                    {
                        if (Inside(j, i))
                        {
                            continue;
                        }
                        if (currentPixels[drawIndex].PackedValue != transparent)
                        {
                            DrawPoint(pixels, findIndex,
                                    currentPixels[drawIndex].PackedValue);
                        }
                        j++;
                        findIndex++;
                        drawIndex++;
                    }
                    findIndex += moveFind;
                    drawIndex += moveDraw;
                }
            }
        }

        public void DrawImage(LImage pixel)
        {
            if (isClose)
            {
                return;
            }
            LGraphics g = pixel.GetLGraphics();
            int w = pixel.width;
            int h = pixel.height;
            Color[] currentPixels = g.pixels;
            for (int size = 0; size < h; size++)
            {
                Array.Copy(currentPixels, size * pixel.width
                        , pixels, size * width, w);
            }
        }

        public void DrawImage(LImage image, int x, int y, int w, int h)
        {
            if (image == null)
            {
                return;
            }
            else
            {
                DrawImage(image, x, y, w, h, 0, 0, image.width, image.height);
                return;
            }
        }

        static uint Blend(uint src_r, uint src_g, uint src_b, uint src_a, Color dst)
        {
            byte dst_r = dst.R;
            byte dst_g = dst.G;
            byte dst_b = dst.B;
            byte dst_a = dst.A;

            if (dst_a == 0)
            {
                return ((src_a << 24) | (src_b << 16) | (src_g << 8) | src_r);
            }

            dst_r = (byte)(dst_r + src_a * (src_r - dst_r) / 255);
            dst_g = (byte)(dst_g + src_a * (src_g - dst_g) / 255);
            dst_b = (byte)(dst_b + src_a * (src_b - dst_b) / 255);
            dst_a = (byte)(((1.0f - (1.0f - src_a / 255.0f) * (1.0f - dst_a / 255.0f)) * 255));

            return (uint)((dst_a << 24) | (dst_b << 16) | (dst_g << 8) | dst_r);
        }

        public void DrawImage(LImage img, int dstX, int dstY, int dstWidth, int dstHeight,
                int srcX, int srcY, int srcWidth, int srcHeight)
        {
            if (isClose || img == null || img.IsClose())
            {
                return;
            }

            LGraphics pixel = img.GetLGraphics();

            pixel.UpdatePixels();

            dstX += translateX;
            dstY += translateY;
            srcX += translateX;
            srcY += translateY;

            if (pixel == null || dstWidth <= 0 || dstHeight <= 0 || srcWidth <= 0 || srcHeight <= 0)
            {
                return;
            }
            if (dstWidth == srcWidth && dstHeight == srcHeight)
            {
                DrawImage(pixel, dstX, dstY, dstWidth, dstHeight, srcX, srcY);
                return;
            }

            Color[] currentPixels = pixel.pixels;

            int spitch = pixel.width;
            int dpitch = this.width;

            float x_ratio = ((float)srcWidth - 1) / dstWidth;
            float y_ratio = ((float)srcHeight - 1) / dstHeight;
            float x_diff = 0F;
            float y_diff = 0F;

            int dx = dstX;
            int dy = dstY;
            int sx = srcX;
            int sy = srcY;
            int i = 0;
            int j = 0;

            for (; i < dstHeight; i++)
            {
                sy = (int)(i * y_ratio) + srcY;
                dy = i + dstY;
                y_diff = (y_ratio * i + srcY) - sy;
                if (sy < 0 || dy < 0)
                {
                    continue;
                }
                if (sy >= pixel.height || dy >= this.height)
                {
                    break;
                }

                for (j = 0; j < dstWidth; j++)
                {
                    sx = (int)(j * x_ratio) + srcX;
                    dx = j + dstX;
                    x_diff = (x_ratio * j + srcX) - sx;
                    if (sx < 0 || dx < 0)
                    {
                        continue;
                    }
                    if (sx >= pixel.width || dx >= this.width)
                    {
                        break;
                    }

                    int src_ptr = sx + sy * spitch;
                    int dst_ptr = dx + dy * dpitch;
                    Color src_color = currentPixels[src_ptr];
                    uint src_pixel = src_color.PackedValue;

                    if (src_pixel != LSystem.TRANSPARENT)
                    {
                        float ta = (1 - x_diff) * (1 - y_diff);
                        float tb = (x_diff) * (1 - y_diff);
                        float tc = (1 - x_diff) * (y_diff);
                        float td = (x_diff) * (y_diff);

                        uint a = (uint)(((src_pixel & 0xff000000) >> 24) * ta +
                                                ((src_pixel & 0xff000000) >> 24) * tb +
                                                ((src_pixel & 0xff000000) >> 24) * tc +
                                                ((src_pixel & 0xff000000) >> 24) * td) & 0xff;
                        uint b = (uint)(((src_pixel & 0xff0000) >> 16) * ta +
                                                ((src_pixel & 0xff0000) >> 16) * tb +
                                                ((src_pixel & 0xff0000) >> 16) * tc +
                                                ((src_pixel & 0xff0000) >> 16) * td) & 0xff;
                        uint g = (uint)(((src_pixel & 0xff00) >> 8) * ta +
                                                ((src_pixel & 0xff00) >> 8) * tb +
                                                ((src_pixel & 0xff00) >> 8) * tc +
                                                ((src_pixel & 0xff00) >> 8) * td) & 0xff;
                        uint r = (uint)((src_pixel & 0xff) * ta +
                                                (src_pixel & 0xff) * tb +
                                                (src_pixel & 0xff) * tc +
                                                (src_pixel & 0xff) * td) & 0xff;

                        Color dst_color = pixels[dst_ptr];
                        DrawPoint(pixels, dst_ptr, Blend(r, g, b, a, dst_color));
                    }
                    else
                    {
                        DrawPoint(pixels, dst_ptr, LSystem.TRANSPARENT);
                    }
                }
            }

        }

        public void DrawSixStart(LColor color, int x, int y, int r)
        {
            DrawSixStart(color.Color, x, y, r);
        }

        public void DrawSixStart(Color color, int x, int y, int r)
        {
            if (isClose)
            {
                return;
            }
            SetColor(color);
            DrawTriangle(color, x, y, r);
            DrawRTriangle(color, x, y, r);
        }

        public void DrawTriangle(LColor color, int x, int y, int r)
        {
            DrawTriangle(color.Color, x, y, r);
        }

        public void DrawTriangle(Color color, int x, int y, int r)
        {
            if (isClose)
            {
                return;
            }
            int x1 = x;
            int y1 = y - r;
            int x2 = x - (int)(r * MathUtils.Cos(MathUtils.PI / 6));
            int y2 = y + (int)(r * MathUtils.Sin(MathUtils.PI / 6));
            int x3 = x + (int)(r * MathUtils.Cos(MathUtils.PI / 6));
            int y3 = y + (int)(r * MathUtils.Sin(MathUtils.PI / 6));
            int[] xpos = new int[3];
            xpos[0] = x1;
            xpos[1] = x2;
            xpos[2] = x3;
            int[] ypos = new int[3];
            ypos[0] = y1;
            ypos[1] = y2;
            ypos[2] = y3;
            SetColor(color);
            FillPolygon(xpos, ypos, 3);
        }

        public void DrawRTriangle(LColor color, int x, int y, int r)
        {
            DrawRTriangle(color.Color, x, y, r);
        }

        public void DrawRTriangle(Color color, int x, int y, int r)
        {
            if (isClose)
            {
                return;
            }
            int x1 = x;
            int y1 = y + r;
            int x2 = x - (int)(r * MathUtils.Cos(MathUtils.PI / 6.0));
            int y2 = y - (int)(r * MathUtils.Sin(MathUtils.PI / 6.0));
            int x3 = x + (int)(r * MathUtils.Cos(MathUtils.PI / 6.0));
            int y3 = y - (int)(r * MathUtils.Sin(MathUtils.PI / 6.0));
            int[] xpos = new int[3];
            xpos[0] = x1;
            xpos[1] = x2;
            xpos[2] = x3;
            int[] ypos = new int[3];
            ypos[0] = y1;
            ypos[1] = y2;
            ypos[2] = y3;
            SetColor(color);
            FillPolygon(xpos, ypos, 3);
        }

        public void FillTriangle(Triangle2f[] ts)
        {
            FillTriangle(ts, 0, 0);
        }

        public void FillTriangle(Triangle2f[] ts, int x, int y)
        {
            if (isClose)
            {
                return;
            }
            if (ts == null)
            {
                return;
            }
            int size = ts.Length;
            for (int i = 0; i < size; i++)
            {
                FillTriangle(ts[i], x, y);
            }
        }

        public void FillTriangle(Triangle2f t)
        {
            FillTriangle(t, 0, 0);
        }

        public void FillTriangle(Triangle2f t, int x, int y)
        {
            if (isClose)
            {
                return;
            }
            if (t == null)
            {
                return;
            }
            int[] xpos = new int[3];
            int[] ypos = new int[3];
            xpos[0] = x + (int)t.xpoints[0];
            xpos[1] = x + (int)t.xpoints[1];
            xpos[2] = x + (int)t.xpoints[2];
            ypos[0] = y + (int)t.ypoints[0];
            ypos[1] = y + (int)t.ypoints[1];
            ypos[2] = y + (int)t.ypoints[2];
            FillPolygon(xpos, ypos, 3);
        }

        public void DrawTriangle(Triangle2f[] ts)
        {
            DrawTriangle(ts, 0, 0);
        }

        public void DrawTriangle(Triangle2f[] ts, int x, int y)
        {
            if (isClose)
            {
                return;
            }
            if (ts == null)
            {
                return;
            }
            int size = ts.Length;
            for (int i = 0; i < size; i++)
            {
                DrawTriangle(ts[i], x, y);
            }
        }

        public void DrawTriangle(Triangle2f t)
        {
            DrawTriangle(t, 0, 0);
        }

        public void DrawTriangle(Triangle2f t, int x, int y)
        {
            if (isClose)
            {
                return;
            }
            if (t == null)
            {
                return;
            }
            int[] xpos = new int[3];
            int[] ypos = new int[3];
            xpos[0] = x + (int)t.xpoints[0];
            xpos[1] = x + (int)t.xpoints[1];
            xpos[2] = x + (int)t.xpoints[2];
            ypos[0] = y + (int)t.ypoints[0];
            ypos[1] = y + (int)t.ypoints[1];
            ypos[2] = y + (int)t.ypoints[2];
            DrawPolygon(xpos, ypos, 3);
        }

        public void CopyArea(int x, int y, int width, int height, int dx, int dy)
        {
            if (isClose)
            {
                return;
            }
            x += translateX;
            y += translateY;

            int xStart = x;
            int xEnd = x + width - 1;
            int xStep = 1;
            if (dx < 0)
            {
                xStart = x + width - 1;
                xEnd = x;
                xStep = -1;
            }
            int yStart = y;
            int yEnd = y + height - 1;
            int yStep = 1;
            if (dy < 0)
            {
                yStart = y + height - 1;
                yEnd = y;
                yStep = -1;
            }
            for (x = xStart; x <= xEnd; x += xStep)
            {
                for (y = yStart; y <= yEnd; y += yStep)
                {
                    if (!Inside(x + dx, y + dy) && x >= 0 && x < width && y >= 0
                            && y < height)
                    {
                        this.pixels[x + dx + (y + dy) * width] = this.pixels[x + y
                                * width];
                    }
                }
            }
            this.isDitry = true;
        }

        public void SetColor(byte r, byte g, byte b)
        {
            SetColor(r, g, b, (byte)255);
        }

        public void SetColor(byte r, byte g, byte b, byte a)
        {
            this.colorValue.R = r;
            this.colorValue.G = g;
            this.colorValue.B = b;
            this.colorValue.A = a;
        }

        public void SetColor(int r, int g, int b)
        {
            SetColor(r, g, b, 255);
        }

        public void SetColor(int r, int g, int b, int a)
        {
            this.colorValue.R = (byte)r;
            this.colorValue.G = (byte)g;
            this.colorValue.B = (byte)b;
            this.colorValue.A = (byte)a;
        }

        public void SetColor(Color c)
        {
            this.colorValue = c;
        }

        public void SetColor(LColor c)
        {
            if (c == null)
            {
                return;
            }
            this.colorValue = c.Color;
        }

        public void ClearDraw(uint pixel)
        {
            for (int i = 0; i < this.size; i++)
            {
                pixels[i].PackedValue = pixel;
            }
            this.isDitry = true;
        }

        public void ClearDraw(LColor c)
        {
            if (c != null)
            {
                ClearDraw(c.Color);
            }
        }

        public void ClearDraw(Color c)
        {
            for (int i = 0; i < this.size; i++)
            {
                pixels[i].PackedValue = c.PackedValue;
            }
            this.isDitry = true;
        }

        /// <summary>
        /// 清空数据
        /// </summary>
        public void Clear()
        {
            ClearDraw(Color.Black);
        }

        /// <summary>
        /// 清空屏幕
        /// </summary>
        public void Fill()
        {
            for (int i = 0; i < size; i++)
            {
                if (alpha)
                {
                    pixels[i].PackedValue = LColor.GetARGB(0, 0, 0, 0);
                }
                else
                {
                    pixels[i].PackedValue = LColor.GetRGB(0, 0, 0);
                }
            }
            this.isDitry = true;
        }

        private bool Inside(int x, int y)
        {
            return (x < clip.x || x >= clip.x + clip.width || y < clip.y || y >= clip.y
                    + clip.height);
        }

        private void DrawPoint(int pixelIndex)
        {
            pixels[pixelIndex].PackedValue = colorValue.PackedValue;
            isDitry = true;
        }

        public void DrawPoint(int x, int y, uint color)
        {
            if (!Inside(x, y))
            {
                int pixelIndex = x + y * clip.width;
                pixels[pixelIndex].PackedValue = color;
                isDitry = true;
            }
        }

        private void DrawPoint(int x, int y)
        {
            DrawPoint(x, y, colorValue);
        }

        public void DrawPoint(int x, int y, LColor c)
        {
            DrawPoint(x, y, c.Color);
        }

        public void DrawPoint(int x, int y, Color c)
        {
            if (!Inside(x, y))
            {
                int pixelIndex = x + y * clip.width;
                pixels[pixelIndex] = c;
                isDitry = true;
            }
        }

        public void DrawPoint(int x, int y, byte r, byte g, byte b, byte a)
        {
            if (!Inside(x, y))
            {
                int pixelIndex = x + y * clip.width;
                pixels[pixelIndex].R = r;
                pixels[pixelIndex].G = g;
                pixels[pixelIndex].B = b;
                pixels[pixelIndex].A = a;
                isDitry = true;
            }
        }

        public void DrawPoint(int x, int y, byte r, byte g, byte b)
        {
            DrawPoint(x, y, r, g, b, 255);
        }

        public void FillRect(int @x, int @y, int @width, int @height)
        {
            if (isClose)
            {
                return;
            }
            int maxX = (int)MathUtils.Min(x + width - 1 + translateX, clip.x
            + clip.width - 1);
            int maxY = (int)MathUtils.Min(y + height - 1 + translateY, clip.y
                    + clip.height - 1);
            for (int row = (int)MathUtils.Max(y + translateY, clip.y); row <= maxY; row++)
            {
                for (int col = (int)MathUtils.Max(x + translateX, clip.x); col <= maxX; col++)
                {
                    DrawPoint(col, row);
                }
            }
        }

        public void DrawRect(int x1, int y1, int w1, int h1)
        {
            if (isClose)
            {
                return;
            }
            int tempX = x1;
            int tempY = y1;
            int tempWidth = x1 + w1;
            int tempHeight = y1 + h1;
            if (tempX > tempWidth)
            {
                x1 = tempX;
                tempX = tempWidth;
                tempWidth = x1;
            }
            if (tempY > tempHeight)
            {
                y1 = tempY;
                tempY = tempHeight;
                tempHeight = y1;
            }
            DrawLine(tempX, tempY, tempHeight, tempY);
            DrawLine(tempX, tempY + 1, tempX, tempHeight);
            DrawLine(tempHeight, tempHeight, tempX + 1, tempHeight);
            DrawLine(tempHeight, tempHeight - 1, tempHeight, tempY + 1);
        }


        public void DrawLine(int @x1, int @y1, int @x2, int @y2)
        {
            if (isClose)
            {
                return;
            }

            x1 += translateX;
            y1 += translateY;
            x2 += translateX;
            y2 += translateY;

            int dx = x2 - x1;
            int dy = y2 - y1;

            if (dx == 0)
            {
                if (y1 < y2)
                {
                    DrawVerticalLine(x1, y1, y2);
                }
                else
                {
                    DrawVerticalLine(x1, y2, y1);
                }
            }
            else if (dy == 0)
            {
                if (x1 < x2)
                {
                    DrawLineImpl(x1, x2, y1);
                }
                else
                {
                    DrawLineImpl(x2, x1, y1);
                }
            }
            else
            {
                bool swapXY = false;
                int dxNeg = 1;
                int dyNeg = 1;
                bool negativeSlope = false;
                if (MathUtils.Abs(dy) > MathUtils.Abs(dx))
                {
                    int temp = x1;
                    x1 = y1;
                    y1 = temp;
                    temp = x2;
                    x2 = y2;
                    y2 = temp;
                    dx = x2 - x1;
                    dy = y2 - y1;
                    swapXY = true;
                }

                if (x1 > x2)
                {
                    int temp = x1;
                    x1 = x2;
                    x2 = temp;
                    temp = y1;
                    y1 = y2;
                    y2 = temp;
                    dx = x2 - x1;
                    dy = y2 - y1;
                }

                if (dy * dx < 0)
                {
                    if (dy < 0)
                    {
                        dyNeg = -1;
                        dxNeg = 1;
                    }
                    else
                    {
                        dyNeg = 1;
                        dxNeg = -1;
                    }
                    negativeSlope = true;
                }

                int d = 2 * (dy * dyNeg) - (dx * dxNeg);
                int incrH = 2 * dy * dyNeg;
                int incrHV = 2 * ((dy * dyNeg) - (dx * dxNeg));
                int x = x1;
                int y = y1;
                int tempX = x;
                int tempY = y;

                if (swapXY)
                {
                    int temp = x;
                    x = y;
                    y = temp;
                }

                DrawPoint(x, y);
                x = tempX;
                y = tempY;

                while (x < x2)
                {
                    if (d <= 0)
                    {
                        x++;
                        d += incrH;
                    }
                    else
                    {
                        d += incrHV;
                        x++;
                        if (!negativeSlope)
                        {
                            y++;
                        }
                        else
                        {
                            y--;
                        }
                    }

                    tempX = x;
                    tempY = y;
                    if (swapXY)
                    {
                        int temp = x;
                        x = y;
                        y = temp;
                    }
                    DrawPoint(x, y);
                    x = tempX;
                    y = tempY;
                }
            }
        }
        private void DrawLineImpl(int x1, int x2, int y)
        {
            if (isClose)
            {
                return;
            }
            if (y >= clip.y && y < clip.y + clip.height)
            {
                y *= width;
                int maxX = (int)MathUtils.Min(x2, clip.x + clip.width - 1);
                if (pixels != null)
                {
                    for (int x = (int)MathUtils.Max(x1, clip.x); x <= maxX; x++)
                    {
                        DrawPoint(x + y);
                    }
                }
            }
        }

        private void DrawVerticalLine(int x, int y1, int y2)
        {
            if (x >= clip.x && x < clip.x + clip.width)
            {
                int maxY = (int)(MathUtils.Min(y2, clip.y + clip.height - 1) * width);
                if (pixels != null)
                {
                    for (int y = (int)(MathUtils.Max(y1, clip.y) * width); y <= maxY; y += width)
                    {
                        DrawPoint(x + y);
                    }
                }
            }
        }

        private void DrawArcPoint(int[] xPoints, int[] yPoints, int nPoints,
            RectBox bounds, int x, int y)
        {
            if (Contains(xPoints, yPoints, nPoints, bounds, x, y))
            {
                DrawPoint(x, y);
            }
        }

        public void ClearRect(int x, int y, int width, int height)
        {
            FillRect(x, y, width, height);
        }

        private class DrawOval_CircleUpdate : CircleUpdate
        {

            private LGraphics pixmap;

            public DrawOval_CircleUpdate(LGraphics bit)
            {
                this.pixmap = bit;
            }

            public void NewPoint(int xLeft, int yTop, int xRight, int yBottom)
            {
                pixmap.DrawPoint(xLeft, yTop);
                pixmap.DrawPoint(xRight, yTop);
                pixmap.DrawPoint(xLeft, yBottom);
                pixmap.DrawPoint(xRight, yBottom);
            }

        }


        private void DrawCircle(int x, int y, int width, int height, bool Fill,
                CircleUpdate listener)
        {
            int a = width / 2;
            int b = height / 2;
            long squareA = width * width / 4;
            long squareB = height * height / 4;
            long squareAB = Round((long)width * width * height * height, 16L);

            x += translateX;
            y += translateY;
            int centerX = x + a;
            int centerY = y + b;

            int deltaX = (width % 2 == 0) ? 0 : 1;
            int deltaY = (height % 2 == 0) ? 0 : 1;

            int currentY = b;
            int currentX = 0;

            int lastx1 = centerX - currentX;
            int lastx2 = centerX + currentX + deltaX;
            int lasty1 = centerY - currentY;
            int lasty2 = centerY + currentY + deltaY;
            while (currentX <= a && currentY >= 0)
            {
                long deltaA = (currentX + 1) * (currentX + 1) * squareB + currentY
                        * currentY * squareA - squareAB;
                long deltaB = (currentX + 1) * (currentX + 1) * squareB
                        + (currentY - 1) * (currentY - 1) * squareA - squareAB;
                long deltaC = currentX * currentX * squareB + (currentY - 1)
                        * (currentY - 1) * squareA - squareAB;
                if (deltaA <= 0)
                {
                    currentX++;
                }
                else if (deltaC >= 0)
                {
                    currentY--;
                }
                else
                {
                    int min = (int)MathUtils.Min(
                            MathUtils.Abs(deltaA),
                            MathUtils.Min(MathUtils.Abs(deltaB),
                                    MathUtils.Abs(deltaC)));
                    if (min == MathUtils.Abs(deltaA))
                    {
                        currentX++;
                    }
                    else if (min == MathUtils.Abs(deltaC))
                    {
                        currentY--;
                    }
                    else
                    {
                        currentX++;
                        currentY--;
                    }
                }

                int x1 = centerX - currentX;
                int x2 = centerX + currentX + deltaX;
                int y1 = centerY - currentY;
                int y2 = centerY + currentY + deltaY;
                if (!Fill || lasty1 != y1)
                {
                    listener.NewPoint(lastx1, lasty1, lastx2, lasty2);
                    lasty1 = y1;
                    lasty2 = y2;
                }
                lastx1 = x1;
                lastx2 = x2;
            }
            if (lasty1 < lasty2)
            {
                for (; lasty1 <= lasty2; lasty1++, lasty2--)
                {
                    listener.NewPoint(centerX - a, lasty1, centerX + a + deltaX,
                            lasty2);
                }
            }
        }

        public void DrawOval(int x, int y, int width, int height)
        {
            if (isClose)
            {
                return;
            }
            DrawCircle(x, y, width, height, false, new DrawOval_CircleUpdate(this));
        }

        private class FillOval_CircleUpdate : CircleUpdate
        {

            private LGraphics pixmap;

            public FillOval_CircleUpdate(LGraphics bit)
            {
                this.pixmap = bit;
            }

            public void NewPoint(int xLeft, int yTop, int xRight, int yBottom)
            {
                pixmap.DrawLineImpl(xLeft, xRight, yTop);
                if (yTop != yBottom)
                {
                    pixmap.DrawLineImpl(xLeft, xRight, yBottom);
                }
            }

        }

        public void FillOval(int x, int y, int width, int height)
        {
            if (isClose)
            {
                return;
            }
            DrawCircle(x, y, width, height, true, new FillOval_CircleUpdate(this));
        }

        private class DrawArc_CircleUpdate : CircleUpdate
        {

            private LGraphics pixmap;

            int[] xPoints, yPoints;

            int nPoints;

            RectBox bounds;


            public DrawArc_CircleUpdate(LGraphics bit, int[] xs, int[] ys, int n, RectBox b)
            {
                this.pixmap = bit;
                xPoints = xs;
                yPoints = ys;
                nPoints = n;
                bounds = b;
            }

            public void NewPoint(int xLeft, int yTop, int xRight, int yBottom)
            {
                pixmap.DrawArcPoint(xPoints, yPoints, nPoints, bounds, xLeft, yTop);
                pixmap.DrawArcPoint(xPoints, yPoints, nPoints, bounds, xRight, yTop);
                pixmap.DrawArcPoint(xPoints, yPoints, nPoints, bounds, xLeft, yBottom);
                pixmap.DrawArcPoint(xPoints, yPoints, nPoints, bounds, xRight, yBottom);
            }

        }

        public void DrawArc(int x, int y, int width, int height, int start,
                int arcAngle)
        {
            if (isClose)
            {
                return;
            }
            if (arcAngle == 0)
            {
                return;
            }
            start %= 360;
            if (start < 0)
            {
                start += 360;
            }
            if (arcAngle % 360 == 0)
            {
                arcAngle = 360;
            }
            else
            {
                arcAngle %= 360;
            }
            int startAngle = arcAngle > 0 ? start
                   : (start + arcAngle < 0 ? start + arcAngle + 360 : start
                           + arcAngle);

            int centerX = x + translateX + width / 2;
            int centerY = y + translateY + height / 2;
            int[] xPoints = new int[7];
            int[] yPoints = new int[7];
            int nPoints = GetBoundingShape(xPoints, yPoints, startAngle,
                   MathUtils.Abs(arcAngle), centerX, centerY, x + translateX - 1,
                   y + translateY - 1, width + 2, height + 2);
            RectBox bounds = GetBoundingBox(xPoints, yPoints, nPoints)
                   .GetIntersection(clip);
            this.DrawCircle(x, y, width, height, false, new DrawArc_CircleUpdate(this, xPoints, yPoints, nPoints, bounds));
        }

        private class FillArc_CircleUpdate : CircleUpdate
        {

            private LGraphics pixmap;

            int[] xPoints, yPoints;

            int nPoints;

            RectBox bounds;

            public FillArc_CircleUpdate(LGraphics bit, int[] xs, int[] ys, int n, RectBox b)
            {
                this.pixmap = bit;
                xPoints = xs;
                yPoints = ys;
                nPoints = n;
                bounds = b;
            }

            public void NewPoint(int xLeft, int yTop, int xRight, int yBottom)
            {
                pixmap.DrawArcImpl(xPoints, yPoints, nPoints, bounds, xLeft, xRight,
                        yTop);
                if (yTop != yBottom)
                {
                    pixmap.DrawArcImpl(xPoints, yPoints, nPoints, bounds, xLeft,
                            xRight, yBottom);
                }
            }
        }

        public void FillArc(int x, int y, int width, int height, int start,
                int arcAngle)
        {
            if (arcAngle == 0)
            {
                return;
            }
            start %= 360;
            if (start < 0)
            {
                start += 360;
            }
            if (arcAngle % 360 == 0)
            {
                FillOval(x, y, width, height);
            }
            else
            {
                arcAngle %= 360;
            }
            int startAngle = arcAngle > 0 ? start
                   : (start + arcAngle < 0 ? start + arcAngle + 360 : start
                           + arcAngle);
            int centerX = x + translateX + width / 2;
            int centerY = y + translateY + height / 2;
            int[] xPoints = new int[7];
            int[] yPoints = new int[7];
            int nPoints = GetBoundingShape(xPoints, yPoints, startAngle,
                   MathUtils.Abs(arcAngle), centerX, centerY, x + translateX - 1,
                   y + translateY - 1, width + 2, height + 2);
            RectBox bounds = GetBoundingBox(xPoints, yPoints, nPoints)
                   .GetIntersection(clip);
            this.DrawCircle(x, y, width, height, true, new FillArc_CircleUpdate(this, xPoints, yPoints, nPoints, bounds));
        }

        public void DrawPolyline(int[] xPoints, int[] yPoints, int nPoints)
        {
            if (isClose)
            {
                return;
            }
            for (int i = 1; i < nPoints; i++)
            {
                DrawLine(xPoints[i - 1], yPoints[i - 1], xPoints[i], yPoints[i]);
            }
        }

        public void DrawPolygon(Polygon.Polygon2i p)
        {
            DrawPolygon(p.xpoints, p.ypoints, p.npoints);
        }

        public void DrawPolygon(int[] xPoints, int[] yPoints, int nPoints)
        {
            DrawPolyline(xPoints, yPoints, nPoints);
            DrawLine(xPoints[nPoints - 1], yPoints[nPoints - 1], xPoints[0],
                    yPoints[0]);
        }

        public void FillPolygon(Polygon.Polygon2i p)
        {
            FillPolygon(p.xpoints, p.ypoints, p.npoints);
        }

        public void FillPolygon(int[] xPoints, int[] yPoints, int nPoints)
        {
            if (isClose)
            {
                return;
            }
            int[] xPointsCopy;
            if (translateX == 0)
            {
                xPointsCopy = xPoints;
            }
            else
            {
                xPointsCopy = (int[])xPoints.Clone();
                for (int i = 0; i < nPoints; i++)
                {
                    xPointsCopy[i] += translateX;
                }
            }
            int[] yPointsCopy;
            if (translateY == 0)
            {
                yPointsCopy = yPoints;
            }
            else
            {
                yPointsCopy = (int[])yPoints.Clone();
                for (int i = 0; i < nPoints; i++)
                {
                    yPointsCopy[i] += translateY;
                }
            }
            RectBox bounds = GetBoundingBox(xPointsCopy, yPointsCopy, nPoints)
                    .GetIntersection(clip);
            for (float x = bounds.x; x < bounds.x + bounds.width; x++)
            {
                for (float y = bounds.y; y < bounds.y + bounds.height; y++)
                {
                    if (Contains(xPointsCopy, yPointsCopy, nPoints, bounds,
                            (int)x, (int)y))
                    {
                        DrawPoint((int)x, (int)y);
                    }
                }
            }

        }

        private void UpdatePixels()
        {
            if (isDitry)
            {
                store.SetData<Color>(pixels);
                isDitry = false;
            }
        }

        public void Rotate(float angle)
        {
            Rotate(angle, Width / 2, Height / 2);
        }

        private void UpdateStore(Texture2D s)
        {
            this.store = s;
            this.width = s.Width;
            this.height = s.Height;
            this.size = width * height;
            this.clip.SetBounds(0, 0, width, height);
            this.defClip.SetBounds(0, 0, width, height);
        }

        public void Rotate(float angle, float x, float y)
        {
            UpdatePixels();
            Transform2i transform = new Transform2i();
            transform.Rotate(angle, x, y);
            Color[] processedPixels = new Color[size];
            for (int j = 0; j < height; ++j)
            {
                int yOffset = j * width;
                int fpY = MathUtils.FromInt(j);
                int constX = MathUtils.Mul(fpY, transform.matrixs[0][1])
                        + transform.matrixs[0][2];
                int constY = MathUtils.Mul(fpY, transform.matrixs[1][1])
                        + transform.matrixs[1][2];
                for (int i = 0; i < width; ++i)
                {
                    int fpX = MathUtils.FromInt(i);
                    int tx = MathUtils.ToInt(MathUtils.Mul(fpX,
                            transform.matrixs[0][0]) + constX);
                    if (tx < 0 || width <= tx)
                    {
                        processedPixels[i + yOffset].PackedValue = transparent;
                    }
                    else
                    {
                        int ty = MathUtils.ToInt(MathUtils.Mul(fpX,
                                transform.matrixs[1][0]) + constY);
                        if (ty < 0 || height <= ty)
                        {
                            processedPixels[i + yOffset].PackedValue = transparent;
                        }
                        else
                        {
                            processedPixels[i + yOffset] = pixels[tx + ty
                                    * width];
                        }
                    }
                }
            }
            this.pixels = processedPixels;
            this.isDitry = true;
        }

        public void Scale(float s)
        {
            Zoom(s, Width / 2, Height / 2);
        }

        public void Zoom(float lambda)
        {
            Zoom(lambda, Width / 2, Height / 2);
        }

        public void Zoom(float lambda, float x, float y)
        {
            UpdatePixels();
            if (0 == lambda)
            {
                return;
            }
            Color[] processedPixels = new Color[size];
            int fpLambda = MathUtils.FromFloat(lambda);
            int x_c = MathUtils.FromFloat(x);
            int y_c = MathUtils.FromFloat(y);
            int transX = x_c - MathUtils.Div(x_c, fpLambda);
            int transY = y_c - MathUtils.Div(y_c, fpLambda);
            for (int j = 0; j < height; ++j)
            {
                int yOffset = j * width;
                int fpY = MathUtils.FromInt(j);
                int ty = MathUtils.ToInt(MathUtils.Div(fpY, fpLambda)
                        + transY);
                for (int i = 0; i < width; ++i)
                {
                    if (ty < 0 || height <= ty)
                    {
                        processedPixels[i + yOffset].PackedValue = transparent;
                    }
                    else
                    {
                        int fpX = MathUtils.FromInt(i);
                        int tx = MathUtils.ToInt(MathUtils.Div(fpX, fpLambda)
                                + transX);
                        if (tx < 0 || width <= tx)
                        {
                            processedPixels[i + yOffset].PackedValue = transparent;
                        }
                        else
                        {
                            processedPixels[i + yOffset] = pixels[tx + ty
                                    * width];
                        }
                    }
                }
            }
            this.pixels = processedPixels;
            this.isDitry = true;
        }

        public void Transform(Transform2i t)
        {
            UpdatePixels();
            Color[] processedPixels = new Color[size];
            for (int y = 0; y < height; ++y)
            {
                int yOffset = y * width;
                int fpY = MathUtils.FromInt(y);
                int constX = MathUtils.Mul(fpY, t.matrixs[0][1])
                        + t.matrixs[0][2];
                int constY = MathUtils.Mul(fpY, t.matrixs[1][1])
                        + t.matrixs[1][2];
                for (int x = 0; x < width; ++x)
                {
                    int fpX = MathUtils.FromInt(x);
                    int tx = MathUtils.ToInt(MathUtils.Mul(fpX,
                            t.matrixs[0][0]) + constX);
                    if (tx < 0 || width <= tx)
                    {
                        processedPixels[x + yOffset].PackedValue = transparent;
                    }
                    else
                    {
                        int ty = MathUtils.ToInt(MathUtils.Mul(fpX,
                                t.matrixs[1][0]) + constY);
                        if (ty < 0 || height <= ty)
                        {
                            processedPixels[x + yOffset].PackedValue = transparent;
                        }
                        else
                        {
                            processedPixels[x + yOffset] = pixels[tx + ty
                                    * width];
                        }
                    }
                }
            }
            this.pixels = processedPixels;
            this.isDitry = true;
        }

        public void Convolve(int[][] kernels)
        {
            UpdatePixels();
            Color[] processedPixels = new Color[size];
            int M = kernels.Length;
            int N = kernels[0].Length;
            for (int i = 0; i < height; ++i)
            {
                int iOffset = i * width;
                for (int j = 0; j < width; ++j)
                {
                    int fpA = 0;
                    int fpR = 0;
                    int fpG = 0;
                    int fpB = 0;
                    for (int k = 0; k < M; ++k)
                    {
                        int y = i - 1 + k;
                        if (0 <= y && y < height)
                        {
                            int yOffset = y * width;
                            for (int l = 0; l < N; ++l)
                            {
                                int x = j - 1 + l;
                                if (0 <= x && x < width)
                                {
                                    Color color = pixels[x + yOffset];
                                    int fp = kernels[k][l];
                                    fpA += MathUtils.Mul(MathUtils.FromInt(color.A), fp);
                                    fpR += MathUtils
                                            .Mul(MathUtils.FromInt(color.R), fp);
                                    fpG += MathUtils.Mul(MathUtils.FromInt(color.G), fp);
                                    fpB += MathUtils.Mul(MathUtils.FromInt(color.B), fp);
                                }
                            }
                        }
                    }
                    processedPixels[j + iOffset].PackedValue = LColor.GetARGB(
                            SafeComponent(MathUtils.ToInt(fpR)),
                            SafeComponent(MathUtils.ToInt(fpG)),
                            SafeComponent(MathUtils.ToInt(fpB)),
                            SafeComponent(MathUtils.ToInt(fpA)));
                }
            }
            this.pixels = processedPixels;
            this.isDitry = true;
        }

        public int SafeComponent(int component)
        {
            return (int)MathUtils.Min(MathUtils.Max(0, component), 255);
        }

        public void Wave(float f, float t)
        {
            UpdatePixels();
            Color[] processedPixels = new Color[size];
            float cos = MathUtils.Cos(2 * MathUtils.PI * f * t);
            int dx = (int)(cos * 10);
            for (int y = 0; y < height; ++y)
            {
                int yOffset = y * width;
                for (int x = 0; x < width; ++x)
                {
                    if (y % 2 == 0)
                    {
                        processedPixels[x + yOffset] = pixels[(x + width + dx)
                                % width + yOffset];
                    }
                    else
                    {
                        processedPixels[x + yOffset] = pixels[(x + width - dx)
                                % width + yOffset];
                    }
                }
            }
            this.pixels = processedPixels;
            this.isDitry = true;
        }

        public void Mul(LColor mulColor)
        {
            Mul(mulColor.Color);
        }

        public void Mul(Color mulColor)
        {
            Mul(mulColor.A, mulColor.R, mulColor.G, mulColor.B);
        }

        public void Mul(int red, int green, int blue)
        {
            Mul(255, red, green, blue);
        }

        public void Mul(int alpha, int red, int green, int blue)
        {
            UpdatePixels();
            Color[] processedPixels = new Color[size];
            float a = alpha / 255f;
            float r = red / 255f;
            float g = green / 255f;
            float b = blue / 255f;
            for (int j = 0; j < height; ++j)
            {
                int yOffset = j * width;
                int end = yOffset + width;
                for (int i = yOffset; i < end; ++i)
                {
                    Color pixel = pixels[i];
                    processedPixels[i].PackedValue = LColor.GetARGB(
                            (int)(pixel.R * r),
                            (int)(pixel.G * g),
                            (int)(pixel.B * b),
                            (int)(pixel.A * a));
                }
            }
            this.pixels = processedPixels;
            this.isDitry = true;
        }

        public void Invert()
        {
            UpdatePixels();
            Color[] processedPixels = new Color[size];
            for (int j = 0; j < height; ++j)
            {
                int yOffset = j * width;
                int end = yOffset + width;
                for (int i = yOffset; i < end; ++i)
                {
                    Color c = pixels[i];
                    processedPixels[i].PackedValue = LColor.GetARGB(255 - c.R,
                            255 - c.G,
                            255 - c.B, c.A);
                }
            }
            this.pixels = processedPixels;
            this.isDitry = true;
        }

        public void Transparency()
        {
            this.UpdatePixels();
            uint transparentColor = Get(1, 0);
            if (transparent != transparentColor)
            {
                for (int x = 0; x < width; ++x)
                {
                    for (int y = 0; y < height; ++y)
                    {
                        if (Get(x, y) == transparentColor)
                        {
                            Set(x, y, transparent);
                        }
                    }
                }
            }
            this.isDitry = true;
        }

        public static int[][] BlurKernel()
        {
            float centerValue = 0.2f;
            float edgeValue = (1 - centerValue) / 8;
            int c = MathUtils.FromFloat(centerValue);
            int e = MathUtils.FromFloat(edgeValue);
            int[][] kernels = { new int[] { e, e, e }, new int[] { e, c, e }, new int[] { e, e, e } };
            return kernels;
        }

        public static int[][] GaussianBlurKernel()
        {
            float sum = 16;
            float centerValue = 4f / sum;
            float edgeValue = 1f / sum;
            float midValues = 2f / sum;
            int c = MathUtils.FromFloat(centerValue);
            int e = MathUtils.FromFloat(edgeValue);
            int m = MathUtils.FromFloat(midValues);
            int[][] kernels = { new int[] { e, m, e }, new int[] { m, c, m }, new int[] { e, m, e } };
            return kernels;
        }

        public static int[][] EdgeKernel()
        {
            float centerValue = 1;
            float edgeValue = -1 / 8;
            int c = MathUtils.FromFloat(centerValue);
            int e = MathUtils.FromFloat(edgeValue);
            int[][] kernels = { new int[] { e, e, e }, new int[] { e, c, e }, new int[] { e, e, e } };
            return kernels;
        }

        public static int[][] EmbossKernel()
        {
            float centerValue = 0.5f;
            float edgeValue = 1 - centerValue;
            int c = MathUtils.FromFloat(centerValue);
            int e = MathUtils.FromFloat(edgeValue);
            int[][] kernels = { new int[] { 0, 0, 0 }, new int[] { 0, c, 0 }, new int[] { 0, 0, e } };
            return kernels;
        }

        public static int[][] SharpenKernel()
        {
            float centerValue = 5;
            float edgeValue = -1;
            int c = MathUtils.FromFloat(centerValue);
            int e = MathUtils.FromFloat(edgeValue);
            int[][] kernels = { new int[] { 0, e, 0 }, new int[] { e, c, e }, new int[] { 0, e, 0 } };
            return kernels;
        }

        private Color ColorLerp(int fpX, Color color1,
                Color color2)
        {
            int fpY = MathUtils.ONE_FIXED - fpX;
            int a = MathUtils
                    .ToInt(MathUtils.Mul(
                            MathUtils.FromInt(color1.A), fpY)
                            + MathUtils.Mul(
                                    MathUtils.FromInt(color2.A), fpX));
            int r = MathUtils.ToInt(MathUtils.Mul(
                    MathUtils.FromInt(color1.R), fpY)
                    + MathUtils.Mul(MathUtils.FromInt(color2.R), fpX));
            int g = MathUtils
                    .ToInt(MathUtils.Mul(
                            MathUtils.FromInt(color1.G), fpY)
                            + MathUtils.Mul(
                                    MathUtils.FromInt(color2.G), fpX));
            int b = MathUtils
                    .ToInt(MathUtils.Mul(MathUtils.FromInt(color1.B),
                            fpY)
                            + MathUtils.Mul(
                                    MathUtils.FromInt(color2.B), fpX));
            return new Color(r, g, b, a);
        }

        public void FourCornersGradient(LColor topLeftColor,
                LColor topRightColor, LColor bottomRightColor,
                LColor bottomLeftColor)
        {
            FourCornersGradient(topLeftColor.Color, topRightColor.Color, bottomRightColor.Color, bottomLeftColor.Color);
        }

        public void FourCornersGradient(Color topLeftColor,
                Color topRightColor, Color bottomRightColor,
                Color bottomLeftColor)
        {
            UpdatePixels();
            Color[] processedPixels = new Color[size];
            int fpH = MathUtils.FromInt(height);
            int fpW = MathUtils.FromInt(width);
            for (int y = 0; y < height; ++y)
            {
                int yOffset = y * width;
                int fpYRatio = MathUtils.Div(MathUtils.FromInt(y), fpH);
                Color leftColor = ColorLerp(fpYRatio, topLeftColor,
                        bottomLeftColor);
                Color rightColor = ColorLerp(fpYRatio, topRightColor,
                        bottomRightColor);
                for (int x = 0; x < width; ++x)
                {
                    int fpXRatio = MathUtils.Div(MathUtils.FromInt(x), fpW);
                    processedPixels[x + yOffset] = ColorLerp(fpXRatio, leftColor,
                            rightColor);
                }
            }
            this.pixels = processedPixels;
            this.isDitry = true;
        }

        public void Interleave(LGraphics pix, int[][] mask)
        {
            pix.UpdatePixels();
            Color[] processedPixels = new Color[size];
            int m = mask.Length, n = mask[0].Length;
            int i = 0, j = 0;
            for (int y = 0; y < height; ++y)
            {
                int yOffset = y * width;
                for (int x = 0; x < width; ++x)
                {
                    int offset = x + yOffset;
                    processedPixels[offset] = (mask[i][j] == 1) ? pixels[offset]
                            : pix.pixels[offset];
                    if (++j >= n)
                    {
                        j = 0;
                    }
                }
                if (++i >= m)
                {
                    i = 0;
                }
            }
            this.pixels = processedPixels;
            this.isDitry = true;
        }

        public void ApplyGradient()
        {
            UpdatePixels();
            uint transparentColor = Get(1, 0);
            for (int y = 0; y < height; ++y)
            {
                int yOffset = y * width;
                uint mulColor = Get(0, y);
                if (mulColor != transparentColor)
                {
                    float a = LColor.GetAlpha(mulColor) / 255f;
                    float r = LColor.GetRed(mulColor) / 255f;
                    float g = LColor.GetGreen(mulColor) / 255f;
                    float b = LColor.GetBlue(mulColor) / 255f;
                    int end = yOffset + width;
                    for (int j = yOffset; j < end; ++j)
                    {
                        Color pixel = pixels[j];
                        if (pixel.PackedValue != transparentColor)
                            pixels[j].PackedValue = LColor.GetARGB((int)MathUtils.Min(255,
                                    (pixel.R * r)), (int)MathUtils
                                    .Min(255, (pixel.G * g)),
                                    (int)MathUtils.Min(
                                            (pixel.B * b), 255),
                                    (int)MathUtils.Min(255,
                                            (pixel.A * a)));
                    }
                }
            }
            this.isDitry = true;
        }

        private int pixelsParam;

        public void Scroll(float dx)
        {
            UpdatePixels();
            pixelsParam += MathUtils.FromFloat(dx);
            int dpx = MathUtils.FromInt(pixelsParam);
            if (dpx > width)
            {
                pixelsParam -= MathUtils.ToInt(width);
            }
            Color[] processedPixels = new Color[size];
            for (int y = 0; y < height; ++y)
            {
                int yOffset = y * width;
                for (int x = 0; x < width; ++x)
                {
                    processedPixels[x + yOffset] = pixels[(x + width + dpx)
                            % width + yOffset];
                }
            }
            this.pixels = processedPixels;
            this.isDitry = true;
        }

        public void ColorFilter(LColor color)
        {
            ColorFilter(color.Color);
        }

        public void ColorFilter(Color color)
        {
            UpdatePixels();
            int red = color.R;
            int green = color.G;
            int blue = color.B;
            int alpha = color.A;
            int decrementR = 255 - red;
            int decrementG = 255 - green;
            int decrementB = 255 - blue;
            int decrementA = 255 - alpha;
            for (int start = 0; start < size; start += width)
            {
                int end = start + width;
                for (int index = start; index < end; ++index)
                {
                    Color pixel = pixels[index];
                    int r = pixel.R;
                    r -= decrementR;
                    if (r < 0)
                    {
                        r = 0;
                    }
                    int g = pixel.G;
                    g -= decrementG;
                    if (g < 0)
                    {
                        g = 0;
                    }
                    int b = pixel.B;
                    b -= decrementB;
                    if (b < 0)
                    {
                        b = 0;
                    }
                    int a = pixel.A;
                    a -= decrementA;
                    if (a < 0)
                    {
                        a = 0;
                    }
                    pixels[index].PackedValue = LColor.GetARGB(r, g, b, a);
                }
            }
            this.isDitry = true;
        }

        protected internal void Set(int x, int y, uint pixel)
        {
            pixels[x + y * width].PackedValue = pixel;
        }

        protected internal uint Get(int x, int y)
        {
            return pixels[x + y * width].PackedValue;
        }

        public Color[] GetData()
        {
            return pixels;
        }

        public void SetData(Color[] d)
        {
            this.pixels = d;
            this.isDitry = true;
        }

        public uint GetTransparent()
        {
            return transparent;
        }

        public void SetTransparent(uint t)
        {
            this.transparent = t;
        }

        public bool IsAlpha()
        {
            return alpha && colorValue.A != 255;
        }

        public bool IsClose()
        {
            return isClose;
        }

        public int GetWidth()
        {
            return Width;
        }

        public int GetHeight()
        {
            return Height;
        }

        public int Width
        {
            get
            {
                if (width != 0)
                {
                    return width;
                }
                return width;
            }
        }

        public int Height
        {
            get
            {
                if (height != 0)
                {
                    return height;
                }
                return height;
            }
        }

        private int m_fontsize = 20;

        public void SetFont(LFont font)
        {
            this.m_fontsize = font.GetSize();
        }

        public void SetFont(int size)
        {
            this.m_fontsize = size;
        }

        public int GetFontSize()
        {
            return m_fontsize;
        }

        private System.Collections.Generic.Dictionary<string, PixelType> font_cache = new System.Collections.Generic.Dictionary<string, PixelType>(10);

        public void DrawString(string mes, int x, int y)
        {
            DrawString(mes, x, y, Color.White);
        }

        public void DrawString(string mes, int x, int y, Color col)
        {
            if (isClose)
            {
                return;
            }
            if (font_cache.Count > (LSystem.DEFAULT_MAX_CACHE_SIZE / 2))
            {
                foreach (PixelType t in font_cache.Values)
                {
                    if (t != null)
                    {
                        t.Dispose();
                    }
                }
                font_cache.Clear();
            }
            string key = (mes + m_fontsize + col.PackedValue).ToLower();
            PixelType type = (PixelType)CollectionUtils.Get(font_cache, key);
            if (type == null)
            {
                LFont.Load();
                int width = 0;
                char[] chArray = mes.ToCharArray();
                for (int i = 0; i < chArray.Length; i++)
                {
                    if (chArray[i] <= '\x00ff')
                    {
                        width += LFont.realsize / 2;
                    }
                    else
                    {
                        width += LFont.realsize;
                    }
                }
                if (width == 0)
                {
                    return;
                }
                Color[] data = new Color[width * (LFont.realsize + LFont.offy)];
                int off1 = 0;
                int off2 = 0;
                int off3 = LFont.realsize / 2;
                int off4 = 0;
                int index = 0;
                for (int idx = 0; idx < chArray.Length; idx++)
                {
                    int size = (chArray[idx] <= '\x00ff') ? off3 : LFont.realsize;
                    int space = chArray[idx] * LFont.fontSpace;
                    for (int i = 0; i < (LFont.realsize + LFont.offy); i++)
                    {
                        off2 = (i * width) + off1;
                        if (LFont.realsize > 0x10)
                        {
                            index = space + (i * 4);
                            off4 = (((LFont.fontData[index] << 0x18) + (LFont.fontData[index + 1] << 0x10)) + (LFont.fontData[index + 2] << 8)) + LFont.fontData[index + 3];
                        }
                        else
                        {
                            index = space + (i * 2);
                            off4 = (short)((LFont.fontData[index] << 8) + LFont.fontData[index + 1]);
                        }
                        for (int j = 0; j < size; j++)
                        {
                            if ((off4 & (((int)1) << j)) > 0)
                            {
                                data[off2 + j] = col;
                            }
                        }
                    }
                    off1 += size;
                }
                type = new PixelType();
                type.pixels = data;
                type.width = width;
                type.height = (LFont.realsize + LFont.offy);
                type.scale = (float)((m_fontsize * 1f) / ((float)LFont.realsize));
                CollectionUtils.Put(font_cache, key, type);
            }
            PutImage(type, x, y, (int)(type.width * type.scale), (int)(type.height * type.scale));
        }

        private class PixelType : LRelease 
        {
            public Color[] pixels;
            public int width, height;
            public float scale;

            public void Dispose()
            {
                if (pixels != null)
                {
                    pixels = null;
                }
            }
        }

        private void PutImage(PixelType pixel, int dstX, int dstY, int dstWidth, int dstHeight)
        {

            dstX += translateX;
            dstY += translateY;

            if (dstWidth <= 0 || dstHeight <= 0)
            {
                return;
            }

            UpdatePixels();

            Color[] currentPixels = pixel.pixels;

            int spitch = pixel.width;
            int dpitch = this.width;

            float x_ratio = ((float)pixel.width - 1) / dstWidth;
            float y_ratio = ((float)pixel.height - 1) / dstHeight;
            float x_diff = 0F;
            float y_diff = 0F;

            int dx = dstX;
            int dy = dstY;
            int sx = 0;
            int sy = 0;
            int i = 0;
            int j = 0;

            for (; i < dstHeight; i++)
            {
                sy = (int)(i * y_ratio);
                dy = i + dstY;
                y_diff = (y_ratio * i) - sy;
                if (sy < 0 || dy < 0)
                {
                    continue;
                }
                if (sy >= pixel.height || dy >= this.height)
                {
                    break;
                }

                uint col = pixels[0].PackedValue; 
                for (j = 0; j < dstWidth; j++)
                {
                    sx = (int)(j * x_ratio);
                    dx = j + dstX;
                    x_diff = (x_ratio * j) - sx;
                    if (sx < 0 || dx < 0)
                    {
                        continue;
                    }
                    if (sx >= pixel.width || dx >= this.width)
                    {
                        break;
                    }
                    int src_ptr = sx + sy * spitch;
                    int dst_ptr = dx + dy * dpitch;
                    Color src_color = currentPixels[src_ptr];
                    uint src_pixel = src_color.PackedValue;
                    if (src_pixel != LSystem.TRANSPARENT)
                    {
                        DrawPoint(pixels, dst_ptr, src_pixel);
                    }
                    else
                    {
                        DrawPoint(pixels, dst_ptr, col);
                    }
                }
            }

        }

        public void Dispose()
        {
            isClose = true;
            isDitry = true;
            UpdatePixels();
            if (pixels != null)
            {
                pixels = null;
            }
            foreach (PixelType t in font_cache.Values)
            {
                if (t != null)
                {
                    t.Dispose();
                }
            }
            font_cache.Clear();
        }

    }
    
}
