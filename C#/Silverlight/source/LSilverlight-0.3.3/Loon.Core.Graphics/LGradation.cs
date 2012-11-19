using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Core.Graphics.OpenGL;
using Microsoft.Xna.Framework;
using Loon.Utils;
using System.Runtime.CompilerServices;

namespace Loon.Core.Graphics
{
    public class LGradation : LRelease
    {

        private static Dictionary<String, LGradation> gradations;

        private Color start;

        private Color end;

        private int width, height, alpha;

        private LTexture drawTexWidth, drawTexHeight;

        private LPixmap drawImgWidth, drawImgHeight;

        public static LGradation GetInstance(LColor s, LColor e, int w, int h)
        {
            return GetInstance(s.Color, e.Color, w, h, 125);
        }

        public static LGradation GetInstance(LColor s, LColor e, int w, int h, int alpha)
        {
            return GetInstance(s.Color, e.Color, w, h, alpha);
        }

        public static LGradation GetInstance(Color s, Color e, int w, int h)
        {
            return GetInstance(s, e, w, h, 125);
        }

        public static LGradation GetInstance(Color s, Color e, int w, int h,
                int alpha)
        {
            if (gradations == null)
            {
                gradations = new Dictionary<String, LGradation>(10);
            }
            int hashCode = 1;
            hashCode = LSystem.Unite(hashCode, s.PackedValue);
            hashCode = LSystem.Unite(hashCode, e.PackedValue);
            hashCode = LSystem.Unite(hashCode, w);
            hashCode = LSystem.Unite(hashCode, h);
            hashCode = LSystem.Unite(hashCode, alpha);
            String key = "" + (hashCode);
            LGradation o = (LGradation)CollectionUtils.Get(gradations, key);
            if (o == null)
            {
                CollectionUtils.Put(gradations, key, o = new LGradation(s, e, w, h, alpha));
            }
            return o;
        }

        private LGradation()
        {

        }

        private LGradation(Color s, Color e, int w, int h, int alpha)
        {
            this.start = s;
            this.end = e;
            this.width = w;
            this.height = h;
            this.alpha = alpha;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void DrawWidth(GLEx g, int x, int y)
        {
            try
            {
                if (drawTexWidth == null)
                {
                    LPixmap img = new LPixmap(width, height, true);
                    for (int i = 0; i < width; i++)
                    {
                        img.SetColor(
                                (byte)((start.R * (width - i)) / width
                                        + (end.R * i) / width),
                                (byte)((start.G * (width - i)) / width
                                        + (end.G * i) / width),
                                (byte)((start.B * (width - i)) / width
                                        + (end.B * i) / width), (byte)alpha);
                        img.DrawLine(i, 0, i, height);
                    }
                    drawTexWidth = img.Texture;
                }
                g.DrawTexture(drawTexWidth, x, y);
            }
            catch
            {
                for (int i = 0; i < width; i++)
                {
                    g.SetColor(
                                (byte)((start.R * (width - i)) / width
                                        + (end.R * i) / width),
                                (byte)((start.G * (width - i)) / width
                                        + (end.G * i) / width),
                                (byte)((start.B * (width - i)) / width
                                        + (end.B * i) / width), (byte)alpha);
                    g.DrawLine(i + x, y, i + x, y + height);
                }
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void DrawHeight(GLEx g, int x, int y)
        {
            try
            {
                if (drawTexHeight == null)
                {
                    LPixmap img = new LPixmap(width, height, true);
                    for (int i = 0; i < height; i++)
                    {
                        img.SetColor((byte)(
                                (start.R * (height - i)) / height
                                        + (end.R * i) / height),
                                (byte)((start.G * (height - i)) / height
                                        + (end.G * i) / height),
                                (byte)((start.B * (height - i)) / height
                                        + (end.B * i) / height), (byte)(alpha));
                        img.DrawLine(0, i, width, i);
                    }
                    drawTexHeight = img.Texture;
                }
                g.DrawTexture(drawTexHeight, x, y);
            }
            catch
            {
                for (int i = 0; i < height; i++)
                {
                    g.SetColor(
                            (byte)((start.R * (height - i)) / height
                                    + (end.R * i) / height),
                            (byte)((start.G * (height - i)) / height
                                    + (end.G * i) / height),
                            (byte)((start.B * (height - i)) / height
                                    + (end.B * i) / height), (byte)alpha);
                    g.DrawLine(x, i + y, x + width, i + y);
                }
            }
        }


        [MethodImpl(MethodImplOptions.Synchronized)]
        public void DrawWidth(LPixmap g, int x, int y)
        {
            try
            {
                if (drawImgWidth == null)
                {
                    drawImgWidth = new LPixmap(width, height, true);
                    for (int i = 0; i < width; i++)
                    {
                        drawImgWidth.SetColor(
                                (byte)((start.R * (width - i)) / width
                                        + (end.R * i) / width),
                                (byte)((start.G * (width - i)) / width
                                        + (end.G * i) / width),
                                (byte)((start.B * (width - i)) / width
                                        + (end.B * i) / width), (byte)alpha);
                        drawImgWidth.DrawLine(i, 0, i, height);
                    }
                }
                g.DrawPixmap(drawImgWidth, x, y);
            }
            catch
            {
                for (int i = 0; i < width; i++)
                {
                    g.SetColor(
                            (byte)((start.R * (width - i)) / width
                                    + (end.R * i) / width),
                            (byte)((start.G * (width - i)) / width
                                    + (end.G * i) / width),
                            (byte)((start.B * (width - i)) / width
                                    + (end.B * i) / width), (byte)alpha);
                    g.DrawLine(i + x, y, i + x, y + height);
                }
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void DrawHeight(LPixmap g, int x, int y)
        {
            try
            {
                if (drawImgHeight == null)
                {
                    drawImgHeight = new LPixmap(width, height, true);
                    for (int i = 0; i < height; i++)
                    {
                        drawImgHeight.SetColor(
                                (byte)((start.R * (height - i)) / height
                                        + (end.R * i) / height),
                                (byte)((start.G * (height - i)) / height
                                        + (end.G * i) / height),
                                (byte)((start.B * (height - i)) / height
                                        + (end.B * i) / height), (byte)alpha);
                        drawImgHeight.DrawLine(0, i, width, i);
                    }
                }
                g.DrawPixmap(drawImgHeight, x, y);
            }
            catch
            {
                for (int i = 0; i < height; i++)
                {
                    g.SetColor((byte)(
                            (start.R * (height - i)) / height
                                    + (end.R * i) / height),
                           (byte)((start.G * (height - i)) / height
                                    + (end.G * i) / height),
                            (byte)((start.B * (height - i)) / height
                                    + (end.B * i) / height), (byte)alpha);
                    g.DrawLine(x, i + y, x + width, i + y);
                }
            }
        }

        public static void Close()
        {
            if (gradations == null)
            {
                return;
            }
            foreach (LGradation g in gradations.Values)
            {
                if (g != null)
                {
                    g.Dispose();
                }
            }
            gradations.Clear();
        }

        public void Dispose()
        {
            if (drawTexWidth != null)
            {
                drawTexWidth.Destroy();
            }
            if (drawTexHeight != null)
            {
                drawTexHeight.Destroy();
            }
            if (drawImgWidth != null)
            {
                drawImgWidth.Dispose();
            }
            if (drawImgWidth != null)
            {
                drawImgWidth.Dispose();
            }
        }
    }
}
