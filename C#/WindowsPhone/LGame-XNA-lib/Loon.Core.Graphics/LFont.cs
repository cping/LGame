using Microsoft.Xna.Framework.Graphics;
using Loon.Core.Graphics.Opengl;
using Loon.Java;
using SharpZipLib;
using System.IO;
using System.IO.Compression;
using System;
using Loon.Utils.Collection;
using System.IO.IsolatedStorage;
using Loon.Utils;
using Loon.Core.Geom;
namespace Loon.Core.Graphics
{
    public class LFont : BaseFont
    {
        internal static byte[] fontData;
        internal static int fontSpace;
        internal static int offy;
        public static int realsize = 32;
        private static bool initFont;
        private int fontSize;
        public int ascent, descent;
        private string faceName;
        public string charSet;
        public int charSpace;
        public short[][] charWidths;
        public int wordSpace;
        private int style;

        private static LFont defaultFont;

        public static LFont GetDefaultFont()
        {
            if (defaultFont == null)
            {
                defaultFont = GetFont(18);
            }
            return defaultFont;
        }

        private System.Collections.Generic.Dictionary<string, Vector2f> fontSizes = new System.Collections.Generic.Dictionary<string, Vector2f>(
			50);

        public static LFont GetTrueFont()
        {
            return LFont.GetFont(LSystem.FONT_NAME, 0, realsize);
        }

        public static LFont GetFont(int size)
        {
            return LFont.GetFont(LSystem.FONT_NAME, 0, size);
        }

        public static LFont GetFont(string familyName, int size)
        {
            return GetFont(familyName, 0, size);
        }

        public static LFont GetFont(string name, int style, int size)
        {
            return new LFont(name, style, size);
        }

        public Vector2f GetOrigin(string text)
        {
            Vector2f result = (Vector2f)CollectionUtils.Get(fontSizes, text);
            if (result == null)
            {
                result = new Vector2f(StringWidth(text) / 2f, GetHeight() / 2f);
            }
            return result;
        }

        private LFont(int fontSize)
        {
            this.fontSize = fontSize;
            this.ascent = fontSize;
        }

        public LFont(LFont font)
        {
            this.faceName = font.faceName;
            this.style = font.style;
            this.fontSize = font.fontSize;
        }

        private LFont(string name, int style, int size)
            : this(size)
        {
            this.faceName = name;
            this.style = style;

        }

        public int CharsWidth(char[] chars, int offset, int len)
        {
            int num = 0;
            for (int i = offset; i < len; i++)
            {
                int index = this.charSet.IndexOf(chars[i], 0);
                if (index >= 0)
                {
                    num += this.charWidths[index][2] + this.charSpace;
                }
                else
                {
                    num += this.wordSpace;
                }
            }
            return num;
        }

        public int CharHeight()
        {
            return fontSize - 2;
        }

        public int GetLeading()
        {
            return 0;
        }

        public int GetHeight()
        {
            return GetLeading() + GetAscent() + GetDescent();
        }

        public int GetDescent()
        {
            return descent;
        }

        public int GetAscent()
        {
            return ascent;
        }

        public int GetSize()
        {
            return this.fontSize;
        }

        public int GetLineHeight()
        {
            return this.fontSize;
        }

        public int SubStringWidth(string str, int offset, int len)
        {
            return StringWidth(StringUtils.Substring(str, offset, len));
        }

        public int CharWidth(char ch)
        {
            int num = 0;
            if (ch <= '\x00ff')
            {
                num += this.fontSize / 2;
            }
            else
            {
                num += this.fontSize;
            }
            return num;
        }

        public int StringWidth(string str)
        {
            char[] chArray = str.ToCharArray();
            int num = 0;
            for (int i = 0; i < chArray.Length; i++)
            {
                if (chArray[i] <= '\x00ff')
                {
                    num += this.fontSize / 2;
                }
                else
                {
                    num += this.fontSize;
                }
            }
            return num;
        }

        public static Texture2D GetTTFTexture2D(string str)
        {
            Load();
            int width = 0;
            char[] chArray = str.ToCharArray(); ;

                for (int i = 0; i < chArray.Length; i++)
                {
                    if (chArray[i] <= '\x00ff')
                    {
                        width += realsize / 2;
                    }
                    else
                    {
                        width += realsize;
                    }
                }
            
            if (width == 0)
            {
                return null;
            }
            Texture2D textured = new Texture2D(GL.device, width, realsize + offy);
            int[] data = new int[width * (realsize + offy)];
            int off1 = 0;
            int off2 = 0;
            int off3 = realsize / 2;
            int off4 = 0;
            int index = 0;
           
            for (int idx = 0; idx < chArray.Length; idx++)
            {
                int size = (chArray[idx] <= '\x00ff') ? off3 : realsize;
                int space = chArray[idx] * fontSpace;
                for (int i = 0; i < (realsize + offy); i++)
                {
                    off2 = (i * width) + off1;
                    if (realsize > 0x10)
                    {
                        index = space + (i * 4);
                        off4 = (((fontData[index] << 0x18) + (fontData[index + 1] << 0x10)) + (fontData[index + 2] << 8)) + fontData[index + 3];
                    }
                    else
                    {
                        index = space + (i * 2);
                        off4 = (short)((fontData[index] << 8) + fontData[index + 1]);
                    }
                    for (int j = 0; j < size; j++)
                    {
                        if ((off4 & (((int)1) << j)) > 0)
                        {
                            data[off2 + j] = -1;
                        }
                    }
                }
                off1 += size;
            }
            textured.SetData<int>(data);
            return textured;
        }

        public string GetFontName()
        {
            return faceName;
        }

        public static void Load()
        {
            if (!initFont)
            {
                lock (typeof(LFont))
                {
                    if (!initFont)
                    {
                        LoadResToCache();
                        //old method
                        //LoadResToMemory();
                        initFont = true;
                    }
                }
            }
        }

        static void LoadResToMemory()
        {
            Stream stream = null;
            ArrayByte resStream = null;
            try
            {
                stream = XNAConfig.LoadStream(LSystem.FRAMEWORK_IMG_NAME + "font.zip");
                resStream = new ArrayByte(new GZipInputStream(stream), ArrayByte.BIG_ENDIAN);
                realsize = resStream.ReadByte();
                offy = resStream.ReadByte();
                fontSpace = (realsize + offy) * 2;
                if (realsize > 0x10)
                {
                    fontSpace *= 2;
                }
                int num = resStream.ReadInt();
                int num2 = resStream.ReadByte();
                byte[] bufferData = new byte[resStream.Available()];
                resStream.Read(bufferData);
                fontData = bufferData;
            }
            catch (Exception)
            {
                fontData = null;
            }
            finally
            {
                if (stream != null)
                {
                    stream.Dispose();
                    stream.Close();
                    stream = null;
                }
                if (resStream != null)
                {
                    resStream = null;
                }
            }
        }

        static void LoadResToCache()
        {
            string loon_default_font = "lfcache";
            IsolatedStorageFile isoStorage = IsolatedStorageFile.GetUserStoreForApplication();
            if (!isoStorage.FileExists(loon_default_font))
            {
                IsolatedStorageFileStream outStream = isoStorage.CreateFile(loon_default_font);
                Stream gzip = null;
                try
                {
                    gzip = new GZipInputStream(XNAConfig.LoadStream(LSystem.FRAMEWORK_IMG_NAME + "font.zip"));
                    byte[] buffer = new byte[4096];
                    int charsRead;
                    while ((charsRead = gzip.Read(buffer, 0, buffer.Length)) != 0)
                    {
                        outStream.Write(buffer, 0, charsRead);
                    }
                    outStream.Flush();
                    if (gzip != null)
                    {
                        gzip.Close();
                        gzip.Dispose();
                        gzip = null;
                    }
                    buffer = null;
                }
                catch (Exception)
                {
                    if (outStream != null)
                    {
                        outStream.Close();
                        outStream.Dispose();
                        outStream = null;
                    }
                    if (gzip != null)
                    {
                        gzip.Close();
                        gzip.Dispose();
                        gzip = null;
                    }
                    IsolatedStorageFile.GetUserStoreForApplication().DeleteFile(loon_default_font);
                }
                finally
                {
                    if (outStream != null)
                    {
                        outStream.Close();
                        outStream = null;
                    }
                }
            }
            Stream ins = null;
            try
            {
                ins = isoStorage.OpenFile(loon_default_font, FileMode.Open);
                DataInputStream resStream = new DataInputStream(ins);
                realsize = resStream.ReadUnsignedByte();
                offy = resStream.ReadByte();
                fontSpace = (realsize + offy) * 2;
                if (realsize > 0x10)
                {
                    fontSpace *= 2;
                }
                int num = resStream.ReadInt();
                int num2 = resStream.ReadByte();
                byte[] bufferData = new byte[resStream.Available()];
                resStream.Read(bufferData);
                fontData = bufferData;
                if (resStream != null)
                {
                    resStream.Close();
                    resStream = null;
                }
            }
            catch (Exception)
            {
                fontData = null;
                if (ins != null)
                {
                    ins.Close();
                    ins.Dispose();
                    ins = null;
                }
                LoadResToMemory();
            }
            finally
            {
                if (ins != null)
                {
                    ins.Close();
                    ins.Dispose();
                    ins = null;
                }
            }
        }

        public int GetStyle()
        {
            return style;
        }

        public bool IsBold()
        {
            return (style & STYLE_BOLD) != 0;
        }

        public bool IsUnderlined()
        {
            return (style & STYLE_UNDERLINED) != 0;
        }

        public bool IsItalic()
        {
            return (style & STYLE_ITALIC) != 0;
        }

        public bool IsPlain()
        {
            return style == 0;
        }

    }
}
