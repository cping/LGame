using java.lang;
using loon.geom;
using loon.utils;
using Microsoft.Xna.Framework;

namespace loon.canvas
{
    public class LColor
    {
        public static int[] ConvertToABGR(int pixelHeight, int pixelWidth, int[] srcPixels)
        {
            return ConvertToABGR(pixelHeight, pixelWidth, srcPixels, srcPixels);
        }

        public static int[] ConvertToABGR(int pixelHeight, int pixelWidth, int[] srcPixels, int[] dstPixels)
        {
            int pixelCount = pixelWidth * pixelHeight;
            for (int i = 0; i < pixelCount; ++i)
            {
                uint pixel = (uint)srcPixels[i];
                uint r = (pixel & 0x00FF0000) >> 16;
                uint g = (pixel & 0x0000FF00) >> 8;
                uint b = (pixel & 0x000000FF);
                uint a = (pixel & 0xFF000000) >> 24;
                dstPixels[i] = LColor.Abgr((int)r, (int)g, (int)b, (int)a);
            }
            return dstPixels;
        }

        public static LColor FromRGB(int value)
        {
            return new LColor(((value >> 16) & 0xFF), ((value >> 8) & 0xFF), (value & 0xFF), 255);
        }

        public static LColor FromRGBA(int value)
        {
            return new LColor(((value >> 16) & 0xFF), ((value >> 8) & 0xFF), (value & 0xFF), ((value >> 24) & 0xFF));
        }

        public static int ParseColor(string colorString)
        {
            return Decode(colorString).GetARGB();
        }

        public static LColor Decode(string colorString)
        {
            return new LColor(colorString);
        }

        public static LColor ValueOf(string colorString)
        {
            return new LColor(colorString);
        }

        public static float[] ToRGBA(int pixel)
        {
            int r = (pixel & 0x00FF0000) >> 16;
            int g = (pixel & 0x0000FF00) >> 8;
            int b = (pixel & 0x000000FF);
            int a = (int)((uint)pixel & 0xFF000000) >> 24;
            if (a < 0)
            {
                a += 256;
            }
            return new float[] { r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f };
        }

        public static int Combine(uint curColor, uint dstColor)
        {
            return Combine((int)curColor, (int)dstColor);
        }

        public static int Combine(int curColor, int dstColor)
        {
            int newA = ((((curColor >> 24) & 0xFF) * (((dstColor >> 24) & 0xFF) + 1)) & 0xFF00) << 16;
            if ((dstColor & 0xFFFFFF) == 0xFFFFFF)
            {
                return newA | (curColor & 0xFFFFFF);
            }
            int newR = ((((curColor >> 16) & 0xFF) * (((dstColor >> 16) & 0xFF) + 1)) & 0xFF00) << 8;
            int newG = (((curColor >> 8) & 0xFF) * (((dstColor >> 8) & 0xFF) + 1)) & 0xFF00;
            int newB = (((curColor & 0xFF) * ((dstColor & 0xFF) + 1)) >> 8) & 0xFF;
            return newA | newR | newG | newB;
        }

        public static int Rgb565(float r, float g, float b)
        {
            return ((int)(r * 31) << 11) | ((int)(g * 63) << 5) | (int)(b * 31);
        }

        public static int Rgba4444(float r, float g, float b, float a)
        {
            return ((int)(r * 15) << 12) | ((int)(g * 15) << 8) | ((int)(b * 15) << 4) | (int)(a * 15);
        }

        public static int Rgb888(float r, float g, float b)
        {
            return ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
        }

        public static int Rgba8888(float r, float g, float b, float a)
        {
            return ((int)(r * 255) << 24) | ((int)(g * 255) << 16) | ((int)(b * 255) << 8) | (int)(a * 255);
        }

        public static int Argb(float a, float r, float g, float b)
        {
            int alpha = (int)(a * 255);
            int red = (int)(r * 255);
            int green = (int)(g * 255);
            int blue = (int)(b * 255);
            return Argb(alpha, red, green, blue);
        }

        public static int Abgr(float a, float r, float g, float b)
        {
            int alpha = (int)(a * 255);
            int red = (int)(r * 255);
            int green = (int)(g * 255);
            int blue = (int)(b * 255);
            return Abgr(alpha, red, green, blue);
        }

        public static int Argb(int a, int r, int g, int b)
        {
            return (a << 24) | (r << 16) | (g << 8) | b;
        }

        public static int Abgr(int a, int r, int g, int b)
        {
            return (a << 24) | (b << 16) | (g << 8) | r;
        }

        public static int Rgb(int r, int g, int b)
        {
            return Argb(0xFF, r, g, b);
        }

        public static int Bgr(int r, int g, int b)
        {
            return Argb(0xFF, r, g, b);
        }

        public static int Alpha(int color, float a)
        {
            if (a < 0f)
            {
                a = 0f;
            }
            else if (a > 1f)
            {
                a = 1f;
            }
            int ialpha = (int)(0xFF * MathUtils.Clamp(a, 0, 1f));
            return (ialpha << 24) | (color & 0xFFFFFF);
        }

        public static int Alpha(int color)
        {
            return (color >> 24) & 0xFF;
        }

        public static int Red(int color)
        {
            return (color >> 16) & 0xFF;
        }

        public static int Green(int color)
        {
            return (color >> 8) & 0xFF;
        }

        public static int Blue(int color)
        {
            return color & 0xFF;
        }

        public static int WithAlpha(int color, int alpha)
        {
            return (color & 0x00ffffff) | (alpha << 24);
        }

        private static int ConvertInt(string value)
        {
            if (!MathUtils.IsNan(value))
            {
                return 0;
            }
            if (value.IndexOf('.') == -1)
            {
                return Integer.ParseInt(value);
            }
            return (int)Double.ParseDouble(value);
        }

        public static float Encode(float upper, float lower)
        {
            int upquant = (int)(upper * 255), lowquant = (int)(lower * 255);
            return (float)(upquant * 256 + lowquant);
        }

        public static float DecodeUpper(float encoded)
        {
            float lower = encoded % 256;
            return (encoded - lower) / 255;
        }

        public static float DecodeLower(float encoded)
        {
            return (encoded % 256) / 255;
        }

        public static float[] WithAlpha(float[] color, float alpha)
        {
            float r = color[0];
            float g = color[1];
            float b = color[2];
            float a = alpha;
            return new float[] { r, g, b, a };
        }

        public static byte[] ArgbToRGBA(int pixel)
        {
            byte[] bytes = new byte[4];
            int r = (pixel >> 16) & 0xFF;
            int g = (pixel >> 8) & 0xFF;
            int b = (pixel >> 0) & 0xFF;
            int a = (pixel >> 24) & 0xFF;
            bytes[0] = (byte)r;
            bytes[1] = (byte)g;
            bytes[2] = (byte)b;
            bytes[3] = (byte)a;
            return bytes;
        }

        public static byte[] ArgbToRGBA(int[] pixels)
        {
            int size = pixels.Length;
            byte[] bytes = new byte[size * 4];
            int p, r, g, b, a;
            int j = 0;
            for (int i = 0; i < size; i++)
            {
                p = pixels[i];
                a = (p >> 24) & 0xFF;
                r = (p >> 16) & 0xFF;
                g = (p >> 8) & 0xFF;
                b = (p >> 0) & 0xFF;
                bytes[j + 0] = (byte)r;
                bytes[j + 1] = (byte)g;
                bytes[j + 2] = (byte)b;
                bytes[j + 3] = (byte)a;
                j += 4;
            }
            return bytes;
        }

        public static byte[] ArgbToRGB(int[] pixels)
        {
            int size = pixels.Length;
            byte[] bytes = new byte[size * 3];
            int p, r, g, b;
            int j = 0;
            for (int i = 0; i < size; i++)
            {
                p = pixels[i];
                r = (p >> 16) & 0xFF;
                g = (p >> 8) & 0xFF;
                b = (p >> 0) & 0xFF;
                bytes[j + 0] = (byte)r;
                bytes[j + 1] = (byte)g;
                bytes[j + 2] = (byte)b;
                j += 3;
            }
            return bytes;
        }

        public static LColor ToBlackWhite(LColor color)
        {
            if (color == null)
            {
                return LColor.gray.Cpy();
            }
            return ToBlackWhite(color, new LColor());
        }

        public static LColor ToBlackWhite(LColor color, LColor targetColor)
        {
            if (color == null)
            {
                return LColor.gray.Cpy();
            }
            if (targetColor == null)
            {
                targetColor = new LColor();
            }
            if ((color.r * 0.299f + color.g * 0.587f + color.b * 0.114f) >= 0.667f)
            {
                targetColor.r = 0f;
                targetColor.g = 0f;
                targetColor.b = 0f;
            }
            else
            {
                targetColor.r = 1f;
                targetColor.g = 1f;
                targetColor.b = 1f;
            }
            targetColor.a = color.a;
            return targetColor;
        }

        // xna(monogame)用color
        protected Color _xna_color;

        // 默认色彩
        public const uint DEF_COLOR = 0xFFFFFFFF;

        // 默认黑色透明区域
        public const uint TRANSPARENT = 0xFF000000;

        public static LColor silver = new LColor(0xffc0c0c0);

        public static LColor lightBlue = new LColor(0xffadd8e6);

        public static LColor lightCoral = new LColor(0xfff08080);

        public static LColor lightCyan = new LColor(0xffe0ffff);

        public static LColor lightGoldenrodYellow = new LColor(0xfffafad2);

        public static LColor lightGreen = new LColor(0xff90ee90);

        public static LColor lightPink = new LColor(0xffffb6c1);

        public static LColor lightSalmon = new LColor(0xffffa07a);

        public static LColor lightSeaGreen = new LColor(0xff20b2aa);

        public static LColor lightSkyBlue = new LColor(0xff87cefa);

        public static LColor lightSlateGray = new LColor(0xff778899);

        public static LColor lightSteelBlue = new LColor(0xffb0c4de);

        public static LColor lightYellow = new LColor(0xffffffe0);

        public static LColor lime = new LColor(0xff00ff00);

        public static LColor limeGreen = new LColor(0xff32cd32);

        public static LColor linen = new LColor(0xfffaf0e6);

        public static LColor maroon = new LColor(0xff800000);

        public static LColor mediumAquamarine = new LColor(0xff66cdaa);

        public static LColor mediumBlue = new LColor(0xff0000cd);

        public static LColor purple = new LColor(0xff800080);

        public static LColor wheat = new LColor(0xfff5deb3);

        public static LColor gold = new LColor(0xffffd700);

        public static LColor white = new LColor(1.0f, 1.0f, 1.0f, 1.0f);

        public static LColor transparent = white;

        public static LColor yellow = new LColor(1.0f, 1.0f, 0.0f, 1.0f);

        public static LColor red = new LColor(1.0f, 0.0f, 0.0f, 1.0f);

        public static LColor blue = new LColor(0.0f, 0.0f, 1.0f, 1.0f);

        public static LColor cornFlowerBlue = new LColor(0.4f, 0.6f, 0.9f, 1.0f);

        public static LColor green = new LColor(0.0f, 1.0f, 0.0f, 1.0f);

        public static LColor black = new LColor(0.0f, 0.0f, 0.0f, 1.0f);

        public static LColor gray = new LColor(0.5f, 0.5f, 0.5f, 1.0f);

        public static LColor cyan = new LColor(0.0f, 1.0f, 1.0f, 1.0f);

        public static LColor darkGray = new LColor(0.3f, 0.3f, 0.3f, 1.0f);

        public static LColor lightGray = new LColor(0.7f, 0.7f, 0.7f, 1.0f);

        public static LColor pink = new LColor(1.0f, 0.7f, 0.7f, 1.0f);

        public static LColor orange = new LColor(1.0f, 0.8f, 0.0f, 1.0f);

        public static LColor magenta = new LColor(1.0f, 0.0f, 1.0f, 1.0f);

        public float r = 0.0f;

        public float g = 0.0f;

        public float b = 0.0f;

        public float a = 1.0f;

        /**
		 * 转换字符串为color
		 * 
		 * @param c
		 */
        public LColor(string c)
        {
            if (c == null)
            {
                SetColor(LColor.white);
                return;
            }
            c = c.Trim().ToLower();
            // 识别字符串格式颜色
            if (c.StartsWith("#"))
            {
                SetColor(HexToColor(c));
            }
            else if (c.StartsWith("Rgb"))
            {
                int start = c.IndexOf('(');
                int end = c.LastIndexOf(')');
                if (start != -1 && end != -1 && end > start)
                {
                    string result = c.JavaSubstring(start + 1, end).Trim();
                    string[] list = StringUtils.Split(result, ',');
                    if (list.Length == 3)
                    {
                        SetColor(ConvertInt(list[0].Trim()), ConvertInt(list[1].Trim()), ConvertInt(list[2].Trim()));
                    }
                    else if (list.Length == 4)
                    {
                        SetColor(ConvertInt(list[0].Trim()), ConvertInt(list[1].Trim()), ConvertInt(list[2].Trim()),
                                ConvertInt(list[3].Trim()));
                    }
                }
            }
            else if (c.StartsWith("Argb"))
            {
                int start = c.IndexOf('(');
                int end = c.LastIndexOf(')');
                if (start != -1 && end != -1 && end > start)
                {
                    string result = c.JavaSubstring(start + 1, end).Trim();
                    string[] list = StringUtils.Split(result, ',');
                    if (list.Length == 3)
                    {
                        SetColor(ConvertInt(list[1].Trim()), ConvertInt(list[2].Trim()), ConvertInt(list[0].Trim()));
                    }
                    else if (list.Length == 4)
                    {
                        SetColor(ConvertInt(list[1].Trim()), ConvertInt(list[2].Trim()), ConvertInt(list[3].Trim()),
                                ConvertInt(list[0].Trim()));
                    }
                }
            }
            else if (c.StartsWith("transparent"))
            {
                SetColor(unchecked((int)TRANSPARENT));
            }
            else if (MathUtils.IsNan(c))
            {
                SetColor(ConvertInt(c));
            }
            else if (StringUtils.IsHex(c))
            {
                SetColor(HexToColor(c));
            }
            else
            {
                LColor color = LColorList.Get().Find(c);
                if (color != null)
                {
                    SetColor(color);
                }
                else
                {
                    SetColor(HexToColor(c));
                }
            }
        }

        public LColor() : this(LColor.white)
        {

        }

        public LColor(LColor color)
        {
            if (color == null)
            {
                SetColor(LColor.white);
                return;
            }
            SetColor(color.r, color.g, color.b, color.a);
        }

        public LColor(int r, int g, int b)
        {
            SetColor(r, g, b);
        }

        public LColor(int r, int g, int b, int a)
        {
            SetColor(r, g, b, a);
        }

        public LColor(float r, float g, float b)
        {
            SetColor(r, g, b);
        }

        public LColor(float r, float g, float b, float a)
        {
            SetColor(r, g, b, a);
        }

        public LColor(uint value) : this(unchecked((int)value))
        {

        }

        public LColor(int value)
        {
            int r = (value & 0x00FF0000) >> 16;
            int g = (value & 0x0000FF00) >> 8;
            int b = (value & 0x000000FF);
            int a = (int)((uint)value & 0xFF000000) >> 24;

            if (a < 0)
            {
                a += 256;
            }
            if (a == 0)
            {
                a = 255;
            }
            SetColor(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
        }

        public LColor Reset()
        {
            return SetColor(1.0f, 1.0f, 1.0f, 1.0f);
        }

        public override int GetHashCode()
        {
            uint result = (r != +0.0f ? NumberUtils.FloatToIntBits(r) : 0);
            result = 31 * result + (g != +0.0f ? NumberUtils.FloatToIntBits(g) : 0);
            result = 31 * result + (b != +0.0f ? NumberUtils.FloatToIntBits(b) : 0);
            result = 31 * result + (a != +0.0f ? NumberUtils.FloatToIntBits(a) : 0);
            return (int)result;
        }


        public override bool Equals(object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || GetType() != o.GetType())
            {
                return false;
            }
            LColor color = (LColor)o;
            if (NumberUtils.Compare(color.a, a) != 0)
            {
                return false;
            }
            if (NumberUtils.Compare(color.b, b) != 0)
            {
                return false;
            }
            if (NumberUtils.Compare(color.g, g) != 0)
            {
                return false;
            }
            if (NumberUtils.Compare(color.r, r) != 0)
            {
                return false;
            }
            return true;
        }

        public bool Equals(float r1, float g1, float b1, float a1)
        {
            if (NumberUtils.Compare(a1, a) != 0)
            {
                return false;
            }
            if (NumberUtils.Compare(b1, b) != 0)
            {
                return false;
            }
            if (NumberUtils.Compare(g1, g) != 0)
            {
                return false;
            }
            if (NumberUtils.Compare(r1, r) != 0)
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
            scale = 1f - MathUtils.Clamp(scale, 0f, 1f);
            LColor temp = new LColor(r * scale, g * scale, b * scale, a);
            return temp;
        }

        public LColor Lighter()
        {
            return Lighter(0.5f);
        }

        public LColor Lighter(float scale)
        {
            scale = MathUtils.Clamp(scale, 0f, 1f);
            float newRed = MathUtils.Clamp(this.r + (1f - this.r) * scale, 0f, 1f);
            float newGreen = MathUtils.Clamp(this.g + (1f - this.g) * scale, 0f, 1f);
            float newBlue = MathUtils.Clamp(this.b + (1f - b) * scale, 0f, 1f);
            return new LColor(newRed, newGreen, newBlue, a);
        }

        public LColor Brighter()
        {
            return Brighter(0.2f);
        }

        public LColor Brighter(float scale)
        {
            scale = MathUtils.Clamp(scale, 0f, 1f) + 1f;
            LColor temp = new LColor(r * scale, g * scale, b * scale, a);
            return temp;
        }

        public LColor SetColorValue(int r, int g, int b, int a)
        {
            this.r = r > 1 ? (float)r / 255f : r;
            this.g = g > 1 ? (float)g / 255f : g;
            this.b = b > 1 ? (float)b / 255f : b;
            this.a = a > 1 ? (float)a / 255f : a;
            return this;
        }

        public LColor SetIntColor(int r, int g, int b, int a)
        {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            return this;
        }

        public LColor SetColor(float r, float g, float b, float a)
        {
            this.r = r > 1f ? r / 255f : r;
            this.g = g > 1f ? g / 255f : g;
            this.b = b > 1f ? b / 255f : b;
            this.a = a > 1f ? a / 255f : a;
            return this;
        }

        public LColor SetFloatColor(float r, float g, float b, float a)
        {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            return this;
        }

        public LColor SetColor(float r, float g, float b)
        {
            return SetColor(r, g, b, b > 1 ? 255 : 1.0f);
        }

        public LColor SetColor(int r, int g, int b, int a)
        {
            this.r = (float)r / 255;
            this.g = (float)g / 255;
            this.b = (float)b / 255;
            this.a = (float)a / 255;
            return this;
        }

        public LColor SetColor(int r, int g, int b)
        {
            return SetColor(r, g, b, 255);
        }

        public LColor SetColor(LColor color)
        {
            if (color == null)
            {
                return this;
            }
            return SetColor(color.r, color.g, color.b, color.a);
        }

        public LColor SetColor(uint pixel)
        {
            return SetColorARGB((int)pixel);
        }

        public LColor SetColor(int pixel)
        {
            return SetColorARGB(pixel);
        }

        public LColor SetColorARGB(uint pixel)
        {
            return SetColorARGB((int)pixel);
        }

        public LColor SetColorARGB(int pixel)
        {
            int r = (pixel & 0x00FF0000) >> 16;
            int g = (pixel & 0x0000FF00) >> 8;
            int b = (pixel & 0x000000FF);
            int a = (int)((uint)pixel & 0xFF000000) >> 24;
            if (a < 0)
            {
                a += 256;
            }
            return SetColor(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
        }

        public LColor SetColorRGB(int pixel)
        {
            int r = (pixel & 0x00FF0000) >> 16;
            int g = (pixel & 0x0000FF00) >> 8;
            int b = (pixel & 0x000000FF);
            return SetColor(r / 255.0f, g / 255.0f, b / 255.0f, 1f);
        }

        public float Red()
        {
            return r;
        }

        public float Green()
        {
            return g;
        }

        public float Blue()
        {
            return b;
        }

        public float Alpha()
        {
            return a;
        }

        public LColor GetBlackWhite()
        {
            return ToBlackWhite(this);
        }

        public int GetRed()
        {
            return (int)(r * 255);
        }

        public int GetGreen()
        {
            return (int)(g * 255);
        }

        public int GetBlue()
        {
            return (int)(b * 255);
        }

        public int GetAlpha()
        {
            return (int)(a * 255);
        }

        public LColor GetHalfRGBA()
        {
            return new LColor(this).DivSelf(2f);
        }

        public LColor GetHalfRGB()
        {
            return new LColor(this.r, this.g, this.b).DivSelf(2f);
        }

        public LColor SetAll(float c)
        {
            return SetColor(c, c, c, c);
        }

        public LColor SetAll(int c)
        {
            return SetColor(c, c, c, c);
        }

        public LColor SetAlpha(float alpha)
        {
            this.a = alpha;
            return this;
        }

        public bool AddRed(float red)
        {
            float n = Added(this.r, red);
            if (n == this.r)
            {
                return false;
            }
            this.r = n;
            return true;
        }

        public bool AddGreen(float green)
        {
            float n = Added(this.g, green);
            if (n == this.g)
            {
                return false;
            }
            this.g = n;
            return true;
        }

        public bool AddBlue(float blue)
        {
            float n = Added(this.b, blue);
            if (n == this.b)
            {
                return false;
            }
            this.b = n;
            return true;
        }

        public bool AddAlpha(float alpha)
        {
            float n = Added(this.a, alpha);
            if (n == this.a)
            {
                return false;
            }
            this.a = n;
            return true;
        }

        private static float Added(float c, float i)
        {
            c += i;
            if (c > 1f)
            {
                c = 1f;
            }
            else if (c < 0f)
            {
                c = 0f;
            }
            return c;
        }

        public LColor AddSelf(float v)
        {
            this.r += v;
            this.g += v;
            this.b += v;
            this.a += v;
            return this;
        }

        public LColor AddSelf(LColor c)
        {
            if (c == null)
            {
                return this;
            }
            this.r += c.r;
            this.g += c.g;
            this.b += c.b;
            this.a += c.a;
            return this;
        }

        public LColor SubSelf(float v)
        {
            this.r -= v;
            this.g -= v;
            this.b -= v;
            this.a -= v;
            return this;
        }

        public LColor SubSelf(LColor c)
        {
            if (c == null)
            {
                return this;
            }
            this.r -= c.r;
            this.g -= c.g;
            this.b -= c.b;
            this.a -= c.a;
            return this;
        }

        public LColor MulSelf(float v)
        {
            this.r *= v;
            this.g *= v;
            this.b *= v;
            this.a *= v;
            return this;
        }

        public LColor MulSelf(LColor c)
        {
            if (c == null)
            {
                return this;
            }
            this.r *= c.r;
            this.g *= c.g;
            this.b *= c.b;
            this.a *= c.a;
            return this;
        }

        public LColor MulSelfAlpha(float a)
        {
            this.a *= a;
            return this;
        }

        public LColor MulSelfAlpha(LColor c)
        {
            if (c == null)
            {
                return this;
            }
            return MulSelfAlpha(c.a);
        }

        public LColor DivSelf(float v)
        {
            this.r /= v;
            this.g /= v;
            this.b /= v;
            this.a /= v;
            return this;
        }

        public LColor DivSelf(LColor c)
        {
            if (c == null)
            {
                return this;
            }
            this.r /= c.r;
            this.g /= c.g;
            this.b /= c.b;
            this.a /= c.a;
            return this;
        }

        public LColor DivSelfAlpha(float a)
        {
            if (a <= 0)
            {
                a = 0.01f;
            }
            this.a /= a;
            return this;
        }

        public LColor DivSelfAlpha(LColor c)
        {
            return DivSelfAlpha(c.a);
        }

        public LColor Mul(float v)
        {
            return Multiply(v);
        }


        public LColor Mul(LColor c)
        {
            return Multiply(c);
        }

        public LColor Multiply(float v)
        {
            return new LColor(r * v, g * v, b * v, a * v);
        }

        public LColor Multiply(LColor c)
        {
            if (c == null)
            {
                return Cpy();
            }
            return new LColor(r * c.r, g * c.g, b * c.b, a * c.a);
        }

        public LColor Div(float v)
        {
            return Divide(v);
        }

        public LColor Div(LColor c)
        {
            return Divide(c);
        }

        public LColor Divide(float v)
        {
            return new LColor(r / v, g / v, b / v, a / v);
        }

        public LColor Divide(LColor c)
        {
            if (c == null)
            {
                return Cpy();
            }
            return new LColor(r / c.r, g / c.g, b / c.b, a / c.a);
        }

        public LColor Add(float v)
        {
            return Addition(v);
        }

        public LColor Add(LColor c)
        {
            return Addition(c);
        }

        public LColor Addition(float v)
        {
            return new LColor(r + v, g + v, b + v, a + v);
        }

        public LColor Addition(LColor c)
        {
            if (c == null)
            {
                return Cpy();
            }
            return new LColor(r + c.r, g + c.g, b + c.b, a + c.a);
        }

        public LColor Sub(float v)
        {
            return Subtraction(v);
        }

        public LColor Sub(LColor c)
        {
            return Subtraction(c);
        }

        public LColor Subtraction(float v)
        {
            return new LColor(r - v, g - v, b - v, a - v);
        }

        public LColor Subtraction(LColor c)
        {
            if (c == null)
            {
                return Cpy();
            }
            return new LColor(r - c.r, g - c.g, b - c.b, a - c.a);
        }

        public LColor Cpy()
        {
            return new LColor(r, g, b, a);
        }

        public LColor AddCopy(LColor c)
        {
            return Addition(c);
        }

        public LColor SubCopy(LColor c)
        {
            return Subtraction(c);
        }

        public LColor DivCopy(LColor c)
        {
            return Divide(c);
        }

        public LColor MulCopy(LColor c)
        {
            return Multiply(c);
        }

        public static LColor Lerp(LColor value1, LColor value2, float amount)
        {
            return new LColor(Lerp(value1.GetRed(), value2.GetRed(), amount),
                    Lerp(value1.GetGreen(), value2.GetGreen(), amount), Lerp(value1.GetBlue(), value2.GetBlue(), amount),
                    Lerp(value1.GetAlpha(), value2.GetAlpha(), amount));
        }

        private static int Lerp(int color1, int color2, float amount)
        {
            return (int)(color1 + (color2 - color1) * amount);
        }

        public LColor Lerp(LColor target, float alpha)
        {
            return Lerp(this, target, alpha);
        }

        public int GetARGB()
        {
            return Argb(GetAlpha(), GetRed(), GetGreen(), GetBlue());
        }

        public int GetABGR()
        {
            return Abgr(GetAlpha(), GetRed(), GetGreen(), GetBlue());
        }

        public int GetARGB(float alpha)
        {
            return Argb((int)(a * alpha * 255), GetRed(), GetGreen(), GetBlue());
        }

        public int GetABGR(float alpha)
        {
            return Abgr((int)(a * alpha * 255), GetRed(), GetGreen(), GetBlue());
        }

        public int GetRGB()
        {
            return Rgb(GetRed(), GetGreen(), GetBlue());
        }

        public int GetBGR()
        {
            return Bgr(GetRed(), GetGreen(), GetBlue());
        }

        public static int GetRGB(int r, int g, int b)
        {
            return Rgb(r, g, b);
        }

        public static int GetBGR(int r, int g, int b)
        {
            return Bgr(r, g, b);
        }

        public static int GetRGB(int pixels)
        {
            int r = (pixels >> 16) & 0xFF;
            int g = (pixels >> 8) & 0xFF;
            int b = pixels & 0xFF;
            return Rgb(r, g, b);
        }

        public static int GetBGR(int pixels)
        {
            int r = (pixels >> 16) & 0xFF;
            int g = (pixels >> 8) & 0xFF;
            int b = pixels & 0xFF;
            return Bgr(r, g, b);
        }

        public static int GetARGB(int r, int g, int b, int alpha)
        {
            return Argb(alpha, r, g, b);
        }

        public static int GetABGR(int r, int g, int b, int alpha)
        {
            return Abgr(alpha, r, g, b);
        }

        public static int GetAlpha(uint color)
        {
            return GetAlpha((int)color);
        }

        public static int GetAlpha(int color)
        {
            return (int)((uint)color >> 24);
        }

        public static int GetRed(int color)
        {
            return (color >> 16) & 0xFF;
        }

        public static int GetGreen(int color)
        {
            return (color >> 8) & 0xFF;
        }

        public static int GetBlue(int color)
        {
            return color & 0xFF;
        }

        public static int Premultiply(int argbColor)
        {
            int a = (int)((uint)argbColor >> 24);
            if (a == 0)
            {
                return 0;
            }
            else if (a == 255)
            {
                return argbColor;
            }
            else
            {
                int r = (argbColor >> 16) & 0xFF;
                int g = (argbColor >> 8) & 0xFF;
                int b = argbColor & 0xFF;
                r = (a * r + 127) / 255;
                g = (a * g + 127) / 255;
                b = (a * b + 127) / 255;
                return (a << 24) | (r << 16) | (g << 8) | b;
            }
        }

        public static int[] GetRGBs(int pixel)
        {
            int[] rgbs = new int[3];
            rgbs[0] = (pixel >> 16) & 0xFF;
            rgbs[1] = (pixel >> 8) & 0xFF;
            rgbs[2] = (pixel) & 0xFF;
            return rgbs;
        }

        public static int[] GetRGBAs(int pixel)
        {
            int[] rgbas = new int[4];
            rgbas[0] = (pixel >> 16) & 0xFF;
            rgbas[1] = (pixel >> 8) & 0xFF;
            rgbas[2] = (pixel) & 0xFF;
            rgbas[3] = (int)((uint)pixel >> 24);
            return rgbas;
        }

        public byte[] ToRgbaByteArray()
        {
            return new byte[] { (byte)GetRed(), (byte)GetGreen(), (byte)GetBlue(), (byte)GetAlpha() };
        }

        public byte[] ToRgbByteArray()
        {
            return new byte[] { (byte)GetRed(), (byte)GetGreen(), (byte)GetBlue() };
        }

        public int ToIntBits()
        {
            int color = ((int)(255 * a) << 24) | ((int)(255 * b) << 16) | ((int)(255 * g) << 8) | ((int)(255 * r));
            return color;
        }

        public static float ToFloatBits(float r, float g, float b, float a)
        {
            int color = ((int)(255 * a) << 24) | ((int)(255 * b) << 16) | ((int)(255 * g) << 8) | ((int)(255 * r));
            return NumberUtils.IntBitsToFloat((int)(color & 0xfeffffff));
        }

        public float ToFloatBits()
        {
            int color = ((int)(255 * a) << 24) | ((int)(255 * b) << 16) | ((int)(255 * g) << 8) | ((int)(255 * r));
            return NumberUtils.IntBitsToFloat((int)(color & 0xfeffffff));
        }

        public static LColor HsvToColor(float h, float s, float v)
        {
            if (h == 0 && s == 0)
            {
                return new LColor(v, v, v);
            }
            float c = s * v;
            float x = c * (1 - MathUtils.Abs(h % 2 - 1));
            float m = v - c;

            if (h < 1)
            {
                return new LColor(c + m, x + m, m);
            }
            else if (h < 2)
            {
                return new LColor(x + m, c + m, m);
            }
            else if (h < 3)
            {
                return new LColor(m, c + m, x + m);
            }
            else if (h < 4)
            {
                return new LColor(m, x + m, c + m);
            }
            else if (h < 5)
            {
                return new LColor(x + m, m, c + m);
            }
            else
            {
                return new LColor(c + m, m, x + m);
            }
        }

        public static string CssColorString(int color)
        {
            double a = ((color >> 24) & 0xFF) / 255.0;
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = (color >> 0) & 0xFF;
            return "rgba(" + r + "," + g + "," + b + "," + a + ")";
        }

        public static LColor HexToColor(string c)
        {
            try
            {
                if (c.StartsWith("#"))
                {
                    return HexToColor(c.JavaSubstring(1));
                }
                else
                {
                    return new LColor((int)CharUtils.FromHexToLong(c));
                }
            }
            catch (Throwable)
            {
                return new LColor();
            }
        }

        public static LColor StringToColor(string c)
        {
            return HexToColor(c);
        }

        public string ToCSS()
        {
            return "rgba(" + (int)(r * 255) + "," + (int)(g * 255) + "," + (int)(b * 255) + "," + (int)(a * 255) + ")";
        }

        public Vector3f GetVector3()
        {
            return new Vector3f(r, g, b);
        }

        public Vector4f GetVector4()
        {
            return new Vector4f(r, g, b, a);
        }

        public Alpha GetAlphaObject()
        {
            return new Alpha(a);
        }

        public LColor Percent(float percent)
        {
            if (percent < -1)
            {
                return new LColor(0, 0, 0, GetAlpha());
            }
            if (percent > 1)
            {
                return new LColor(255, 255, 255, GetAlpha());
            }
            if (percent < 0)
            {
                percent = 1 + percent;
                int r = MathUtils.Max(0, MathUtils.Min(255, (int)((GetRed() * percent) + 0.5)));
                int g = MathUtils.Max(0, MathUtils.Min(255, (int)((GetGreen() * percent) + 0.5)));
                int b = MathUtils.Max(0, MathUtils.Min(255, (int)((GetBlue() * percent) + 0.5)));
                return new LColor(r, g, b, GetAlpha());
            }
            else if (percent > 0)
            {
                int r = MathUtils.Max(0, MathUtils.Min(255, (int)(((255 - GetRed()) * percent) + GetRed() + 0.5)));
                int g = MathUtils.Max(0, MathUtils.Min(255, (int)(((255 - GetGreen()) * percent) + GetGreen() + 0.5)));
                int b = MathUtils.Max(0, MathUtils.Min(255, (int)(((255 - GetBlue()) * percent) + GetBlue() + 0.5)));
                return new LColor(r, g, b, GetAlpha());
            }
            return new LColor(GetRed(), GetGreen(), GetBlue(), GetAlpha());
        }


        public static LColor GetRandomRGBColor(float startColor, float endColor)
        {
            return new LColor(MathUtils.Random(startColor, endColor), MathUtils.Random(startColor, endColor),
                    MathUtils.Random(startColor, endColor));
        }

        public static LColor GetRandomRGBAColor(float startColor, float endColor)
        {
            return new LColor(MathUtils.Random(startColor, endColor), MathUtils.Random(startColor, endColor),
                    MathUtils.Random(startColor, endColor), MathUtils.Random(startColor, endColor));
        }

        public static LColor GetRandomRGBColor()
        {
            return GetRandomRGBColor(0f, 1f);
        }

        public static LColor GetRandomRGBAColor()
        {
            return GetRandomRGBAColor(0f, 1f);
        }

        public static float GetLuminanceRGB(float r, float g, float b)
        {
            return 0.2126f * r + 0.7152f * g + 0.0722f * b;
        }

        public static float GetLuminanceRGB(int r, int g, int b)
        {
            return 0.2126f * ((float)r / 255f) + 0.7152f * ((float)g / 255f) + 0.0722f * ((float)b / 255f);
        }

        public float GetLuminanceRGB()
        {
            return GetLuminanceRGB(r, g, b);
        }

        public LColor GetLuminanceRGBColor()
        {
            return new LColor(0.2126f * r, 0.7152f * g, 0.0722f * b);
        }

        public LColor GetRGBtoHSL()
        {
            return GetRGBtoHSL(r, g, b);
        }

        public static LColor GetRGBtoHSL(float r, float g, float b)
        {
            int max = (int)MathUtils.Max(r, g, b);
            int min = (int)MathUtils.Min(r, g, b);
            float h = 0, s, l = (max + min) / 2;
            if (max == min)
            {
                h = s = 0;
            }
            else
            {
                float d = max - min;
                s = l > 0.5f ? d / (2 - max - min) : d / (max + min);
                int ir = (int)r;
                int ig = (int)g;
                int ib = (int)b;
                if (max == ir)
                {
                    h = (g - b) / d + (g < b ? 6 : 0);
                }
                else if (max == ig)
                {
                    h = (b - r) / d + 2;
                }
                else if (max == ib)
                {
                    h = (r - g) / d + 4;
                }
            }
            h /= 6;
            return new LColor(h, s, l);
        }

        protected static float Hue2rgb(float p, float q, float t)
        {
            if (t < 0)
            {
                t += 1;
            }
            if (t > 1)
            {
                t -= 1;
            }
            if (t < 1 / 6)
            {
                return p + (q - p) * 6 * t;
            }
            if (t < 1 / 2)
            {
                return q;
            }
            if (t < 2 / 3)
            {
                return p + (q - p) * (2 / 3 - t) * 6;
            }
            return p;
        }

        public static LColor GetHSLtoRGB(float h, float s, float l)
        {
            float r, g, b;
            if (s == 0)
            {
                r = g = b = l;
            }
            else
            {
                float q = l < 0.5f ? l * (1 + s) : l + s - l * s;
                float p = 2 * l - q;
                r = Hue2rgb(p, q, h + 1 / 3);
                g = Hue2rgb(p, q, h);
                b = Hue2rgb(p, q, h - 1 / 3);
            }
            return new LColor(r, g, b);
        }

        public LColor GetHSLtoRGB()
        {
            return GetHSLtoRGB(r, g, b);
        }

        public static bool PutName(string colorName, LColor color)
        {
            return LColorList.Get().PutColor(colorName, color);
        }

        public static LColor FindName(string colorName)
        {
            return LColorList.Get().Find(colorName);
        }

        public static string GetColorName(int pixel)
        {
            return LColorList.Get().Find(pixel);
        }

        public static string GetColorName(LColor color)
        {
            return LColorList.Get().Find(color);
        }

        public uint GetXNAPixel()
        {
            return unchecked((uint)GetABGR());
        }

        public Color GetXNAColor()
        {
            if (_xna_color == null)
            {
                _xna_color = new Color(unchecked((uint)GetABGR()));
            }
            else
            {
                _xna_color.PackedValue = unchecked((uint)GetABGR());
            }
            return _xna_color;
        }

        public string ToString(int color)
        {
            string value = CharUtils.ToHex(color);
            for (; value.Length() < 8;)
            {
                value = "0" + value;
            }
            return value;
        }

        public string ToString(string format)
        {
            if (StringUtils.IsEmpty(format))
            {
                return ToString();
            }
            string newFormat = format.Trim().ToLower();
            if ("Rgb".Equals(newFormat))
            {
                return ToString(GetRGB());
            }
            else if ("Argb".Equals(newFormat) || "rgba".Equals(newFormat))
            {
                return ToString(GetARGB());
            }
            else if ("hsl".Equals(newFormat))
            {
                return ToString(GetRGBtoHSL().GetARGB());
            }
            else if ("alpha".Equals(newFormat))
            {
                return ToString(GetAlpha());
            }
            return ToString();
        }

        public override string ToString()
        {
            return ToString(GetARGB());
        }
    }
}
