using System;
using Microsoft.Xna.Framework;
using Loon.Java;

namespace Loon.Core.Graphics
{
    public class LColor
    {
        
	    public readonly static LColor white = new LColor(255, 255, 255);

	    public readonly static LColor lightGray = new LColor(192, 192, 192);

	    public readonly static LColor gray = new LColor(128, 128, 128);

	    public readonly static LColor darkGray = new LColor(64, 64, 64);

	    public readonly static LColor black = new LColor(0, 0, 0);

	    public readonly static LColor red = new LColor(255, 0, 0);

	    public readonly static LColor pink = new LColor(255, 175, 175);

	    public readonly static LColor orange = new LColor(255, 200, 0);

	    public readonly static LColor yellow = new LColor(255, 255, 0);

	    public readonly static LColor green = new LColor(0, 255, 0);

	    public readonly static LColor magenta = new LColor(255, 0, 255);

	    public readonly static LColor cyan = new LColor(0, 255, 255);

	    public readonly static LColor blue = new LColor(0, 0, 255);

        private Color xnaColor;

        public override int GetHashCode()
        {
            return JavaRuntime.IdentityHashCode(this);
        }

        private LColor() { }

        public LColor(LColor color)
        {
            this.xnaColor.PackedValue = color.Color.PackedValue;
        }

        public LColor(Color color)
        {
            this.xnaColor.PackedValue = color.PackedValue;
        }

        public void SetColor(byte r, byte g, byte b, byte a)
        {
            xnaColor.R = r;
            xnaColor.G = g;
            xnaColor.B = b;
            xnaColor.A = a;
        }

        public void SetColor(float r, float g, float b, float a)
        {
            byte red = (byte)(r * 255f);
            byte green = (byte)(g * 255f);
            byte blue = (byte)(b * 255f);
            byte alpha = (byte)(a * 255f);
            xnaColor.R = red;
            xnaColor.G = green;
            xnaColor.B = blue;
            xnaColor.A = alpha;
        }

        public void setIntColor(int r, int g, int b, int a)
        {
            xnaColor.R = (byte)r;
            xnaColor.G = (byte)g;
            xnaColor.B = (byte)b;
            xnaColor.A = (byte)a;
        }

        public void setFloatColor(float r, float g, float b, float a)
        {
            byte red = (byte)(r * 255f);
            byte green = (byte)(g * 255f);
            byte blue = (byte)(b * 255f);
            byte alpha = (byte)(a * 255f);
            xnaColor.R = red;
            xnaColor.G = green;
            xnaColor.B = blue;
            xnaColor.A = alpha;
        }

        public void SetColor(LColor c)
        {
            this.xnaColor.PackedValue = c.xnaColor.PackedValue;
        }

        /// <summary>
        /// 转变像素为ARGB形式
        /// </summary>
        /// <param name="r"></param>
        /// <param name="g"></param>
        /// <param name="b"></param>
        /// <param name="a"></param>
        /// <returns></returns>
        public static uint GetARGB(byte r, byte g, byte b, byte a)
        {
            return (uint)a << 24 | (uint)b << 16 | (uint)g << 8 | (uint)r;
        }

        public static uint GetARGB(int r, int g, int b, int a)
        {
            return (uint)a << 24 | (uint)b << 16 | (uint)g << 8 | (uint)r;
        }

        /// <summary>
        /// 转变像素为RGB形式
        /// </summary>
        /// <param name="r"></param>
        /// <param name="g"></param>
        /// <param name="b"></param>
        /// <returns></returns>
        public static uint GetRGB(byte r, byte g, byte b)
        {
            return GetARGB(r, g, b, (byte)255);
        }

        public static uint GetRGB(int r, int g, int b)
        {
            return GetARGB(r, g, b, 255);
        }

        /// <summary>
        /// 获得r,g,b的像素集合
        /// </summary>
        /// <param name="pixel"></param>
        /// <returns></returns>
        public static byte[] GetRGBs(uint pixel)
        {
            byte[] rgbs = new byte[3];
            rgbs[2] = (byte)((pixel >> 16) & 0xff);
            rgbs[1] = (byte)((pixel >> 8) & 0xff);
            rgbs[0] = (byte)((pixel) & 0xff);
            return rgbs;
        }

        /// <summary>
        /// 获得r,g,b,a的像素集合
        /// </summary>
        /// <param name="pixel"></param>
        /// <returns></returns>
        public static byte[] GetARGBs(uint pixel)
        {
            byte[] argbs = new byte[4];
            argbs[3] = (byte)((pixel >> 16) & 0xff);
            argbs[2] = (byte)((pixel >> 8) & 0xff);
            argbs[1] = (byte)((pixel) & 0xff);
            argbs[0] = (byte)((pixel >> 24) & 0xff);
            return argbs;
        }

        public LColor(int r, int g, int b)
        {
            this.xnaColor = new Color(r, g, b);
        }

        public LColor(int r, int g, int b, int a)
        {
            this.xnaColor = new Color(r, g, b, a);
        }

        public LColor(float r, float g, float b)
        {
            this.xnaColor = new Color(r, g, b);
        }

        public LColor(float r, float g, float b, float a)
        {
            this.xnaColor = new Color(r, g, b, a);
        }

        internal LColor(uint packedValue)
        {
            this.xnaColor = new Color();
            this.xnaColor.PackedValue = packedValue;
        }

        public byte R
        {
            get
            {
                return xnaColor.R;
            }
            set
            {
                xnaColor.R = value; 
            }
        }

        public byte G
        {
            get
            {
                return xnaColor.G;
            }
            set
            {
                xnaColor.G = value;
            }
        }

        public byte B
        {
            get
            {
                return xnaColor.B;
            }
            set
            {
                xnaColor.B = value;
            }
        }

        public byte A
        {
            get
            {
                return xnaColor.A;
            }
            set
            {
                xnaColor.A = value;
            }
        }

        public float r
        {
            get
            {
                return xnaColor.R / 255;
            }
            set
            {
                xnaColor.R = (byte)(value * 255);
            }
        }

        public float g
        {
            get
            {
                return xnaColor.G / 255;
            }
            set
            {
                xnaColor.G = (byte)(value * 255);
            }
        }

        public float b
        {
            get
            {
                return xnaColor.B / 255;
            }
            set
            {
                xnaColor.B = (byte)(value * 255);
            }
        }

        public float a
        {
            get
            {
                return xnaColor.A / 255;
            }
            set
            {
                xnaColor.A = (byte)(value * 255);
            }
        }

        public uint PackedValue
        {
            get
            {
                return xnaColor.PackedValue;
            }
            set
            {
                xnaColor.PackedValue = value;
            }
        }

        public byte GetR()
        {
            return xnaColor.R;
        }

        public byte GetG()
        {
            return xnaColor.G;
        }

        public byte GetB()
        {
            return xnaColor.B;
        }

        public byte GetA()
        {
            return xnaColor.A;
        }

        public Color Color
        {
            get
            {
                return xnaColor;
            }
        }

        public static byte GetAlpha(uint pixel)
        {
            return (byte)((pixel >> 24) & 0xff);
        }

        public static byte GetRed(uint pixel)
        {
            return (byte)(pixel & 0xff);
        }

        public static byte GetGreen(uint pixel)
        {
            return (byte)((pixel >> 8) & 0xff);
        }

        public static byte GetBlue(uint pixel)
        {
            return (byte)((pixel >> 16) & 0xff);
        }

        public override bool Equals(Object o)
        {
            if ((Object)this == o)
            {
                return true;
            }
            if (o == null || (Object)GetType() != (Object)o.GetType())
            {
                return false;
            }
            LColor color = (LColor)o;
            if (color.A.CompareTo(A) != 0)
            {
                return false;
            }
            if (color.R.CompareTo(R) != 0)
            {
                return false;
            }
            if (color.G.CompareTo(G) != 0)
            {
                return false;
            }
            if (color.B.CompareTo(B) != 0)
            {
                return false;
            }
            return true;
        }

        public bool Equals(float r1, float g1, float b1, float a1)
        {
            if (a1.CompareTo(A) != 0)
            {
                return false;
            }
            if (b1.CompareTo(R) != 0)
            {
                return false;
            }
            if (g1.CompareTo(G) != 0)
            {
                return false;
            }
            if (r1.CompareTo(B) != 0)
            {
                return false;
            }
            return true;
        }

        public LColor Darker()
        {
            return Darker(0.5f);
        }

        public LColor Darker(float scale)
        {
            scale = 1 - scale;
            LColor temp = new LColor(R * scale, G * scale, B * scale, A);
            return temp;
        }

        public LColor Brighter()
        {
            return Brighter(0.2f);
        }

        public LColor Brighter(float scale)
        {
            scale += 1;
            LColor temp = new LColor(R * scale, G * scale, B * scale, A);
            return temp;
        }

        public LColor Multiply(LColor c)
        {
            return new LColor(R * c.R, G * c.G, B * c.B, A * c.A);
        }

        public void Add(LColor c)
        {
            this.R += c.R;
            this.G += c.G;
            this.B += c.B;
            this.A += c.A;
        }

        public void Sub(LColor c)
        {
            this.R -= c.R;
            this.G -= c.G;
            this.B -= c.B;
            this.A -= c.A;
        }

        public void Mul(LColor c)
        {
            this.R *= c.R;
            this.G *= c.G;
            this.B *= c.B;
            this.A *= c.A;
        }

        /// <summary>
        /// 直接复制一个Color
        /// </summary>
        ///
        /// <param name="c"></param>
        /// <returns></returns>
        public LColor Copy(LColor c)
        {
            return new LColor(R, G, B, A);
        }

        /// <summary>
        /// 获得像素相加的Color
        /// </summary>
        ///
        /// <param name="c"></param>
        /// <returns></returns>
        public LColor AddCopy(LColor c)
        {
            LColor copy = new LColor(R, G, B, A);
            copy.R += c.R;
            copy.G += c.G;
            copy.B += c.B;
            copy.A += c.A;
            return copy;
        }

        /// <summary>
        /// 获得像素相减的Color
        /// </summary>
        ///
        /// <param name="c"></param>
        /// <returns></returns>
        public LColor SubCopy(LColor c)
        {
            LColor copy = new LColor(R, G, B, A);
            copy.R -= c.R;
            copy.G -= c.G;
            copy.B -= c.B;
            copy.A -= c.A;
            return copy;
        }

        /// <summary>
        /// 获得像素相乘的Color
        /// </summary>
        ///
        /// <param name="c"></param>
        /// <returns></returns>
        public LColor MulCopy(LColor c)
        {
            LColor copy = new LColor(R, G, B, A);
            copy.R *= c.R;
            copy.G *= c.G;
            copy.B *= c.B;
            copy.A *= c.A;
            return copy;
        }

        /// <summary>
        /// 获得Aplha
        /// </summary>
        ///
        /// <param name="color"></param>
        /// <returns></returns>
        public static int GetAlpha(int color)
        {
            return (int)(((uint)color) >> 24);
        }

        /// <summary>
        /// 获得Red
        /// </summary>
        ///
        /// <param name="color"></param>
        /// <returns></returns>
        public static int GetRed(int color)
        {
            return (color >> 16) & 0xff;
        }

        /// <summary>
        /// 获得Green
        /// </summary>
        ///
        /// <param name="color"></param>
        /// <returns></returns>
        public static int GetGreen(int color)
        {
            return (color >> 8) & 0xff;
        }

        /// <summary>
        /// 获得Blud
        /// </summary>
        ///
        /// <param name="color"></param>
        /// <returns></returns>
        public static int GetBlue(int color)
        {
            return color & 0xff;
        }

        /// <summary>
        /// 像素前乘
        /// </summary>
        ///
        /// <param name="argbColor"></param>
        /// <returns></returns>
        public static int Premultiply(int argbColor)
        {
            int a_0 = (int)(((uint)argbColor) >> 24);
            if (a_0 == 0)
            {
                return 0;
            }
            else if (a_0 == 255)
            {
                return argbColor;
            }
            else
            {
                int r_1 = (argbColor >> 16) & 0xff;
                int g_2 = (argbColor >> 8) & 0xff;
                int b_3 = argbColor & 0xff;
                r_1 = (a_0 * r_1 + 127) / 255;
                g_2 = (a_0 * g_2 + 127) / 255;
                b_3 = (a_0 * b_3 + 127) / 255;
                return (a_0 << 24) | (r_1 << 16) | (g_2 << 8) | b_3;
            }
        }

        /// <summary>
        /// 像素前乘
        /// </summary>
        ///
        /// <param name="rgbColor"></param>
        /// <param name="alpha"></param>
        /// <returns></returns>
        public static int Premultiply(int rgbColor, int alpha)
        {
            if (alpha <= 0)
            {
                return 0;
            }
            else if (alpha >= 255)
            {
                return -16777216 | rgbColor;
            }
            else
            {
                int r_0 = (rgbColor >> 16) & 0xff;
                int g_1 = (rgbColor >> 8) & 0xff;
                int b_2 = rgbColor & 0xff;

                r_0 = (alpha * r_0 + 127) / 255;
                g_1 = (alpha * g_1 + 127) / 255;
                b_2 = (alpha * b_2 + 127) / 255;
                return (alpha << 24) | (r_0 << 16) | (g_1 << 8) | b_2;
            }
        }

        /// <summary>
        /// 消除前乘像素
        /// </summary>
        ///
        /// <param name="preARGBColor"></param>
        /// <returns></returns>
        public static int Unpremultiply(int preARGBColor)
        {
            int a_0 = (int)(((uint)preARGBColor) >> 24);
            if (a_0 == 0)
            {
                return 0;
            }
            else if (a_0 == 255)
            {
                return preARGBColor;
            }
            else
            {
                int r_1 = (preARGBColor >> 16) & 0xff;
                int g_2 = (preARGBColor >> 8) & 0xff;
                int b_3 = preARGBColor & 0xff;

                r_1 = 255 * r_1 / a_0;
                g_2 = 255 * g_2 / a_0;
                b_3 = 255 * b_3 / a_0;
                return (a_0 << 24) | (r_1 << 16) | (g_2 << 8) | b_3;
            }
        }

        /// <summary>
        /// 获得r,g,b
        /// </summary>
        ///
        /// <param name="pixel"></param>
        /// <returns></returns>
        public static int[] GetRGBs(int pixel)
        {
            int[] rgbs = new int[3];
            rgbs[0] = (pixel >> 16) & 0xff;
            rgbs[1] = (pixel >> 8) & 0xff;
            rgbs[2] = (pixel) & 0xff;
            return rgbs;
        }

        public static implicit operator Color(LColor c)
        {
            if (c == null)
            {
                return c;
            }
            return c.xnaColor;
        }

    }
}
