using Loon.Core.Graphics.Device;
using System;
using Loon.Utils;
using System.Text;
namespace Loon.Core.Graphics.Opengl
{
    public class LSTRFont
    {
        private float fontScale = 1f;

        private bool useCache;

        public static LImage CreateFontImage(string fontName, int style, int size,
        LColor color, string text)
        {
            return CreateFontImage(LFont.GetFont(fontName, style, size), color,
                    text);
        }

        public static LImage CreateFontImage(LFont font, LColor color, string text)
        {
            LImage image = LImage.CreateImage(font.StringWidth(text), font.GetHeight());
            LGraphics g = image.GetLGraphics();
            g.SetFont(font);
            g.DrawString(text, 0, 0, color);
            g.Dispose();
            return image;
        }

        private System.Collections.Generic.Dictionary<string, Loon.Core.Graphics.Opengl.LTextureBatch.GLCache> displays;

        private int totalCharSet = 256;

        private System.Collections.Generic.Dictionary<char, IntObject> customChars = new System.Collections.Generic.Dictionary<char, IntObject>();

        private IntObject[] charArray;

        private LColor[] colors;

        private LFont font;

        private IntObject intObject;

        private Loon.Core.Graphics.Opengl.LTextureBatch.GLCache display;

        private float ascent;

        private int charCurrent;

        private int totalWidth;

        private int textureWidth = 512;

        private int textureHeight = 512;

        private int fontSize = 0;

        private int fontHeight = 0;

        private bool antiAlias;

        private LTextureBatch fontBatch;

        private static LFont trueFont;

        private class IntObject
        {

            public int width;

            public int height;

            public int storedX;

            public int storedY;

        }

        public LSTRFont(LFont font, bool antiAlias)
            : this(font, antiAlias, (char[])null)
        {

        }

        public LSTRFont(LFont font, bool antiAlias, string strings)
            : this(font, antiAlias, strings.ToCharArray())
        {

        }

        public LSTRFont(LFont font, string strings)
            : this(font, true, strings.ToCharArray())
        {

        }

        public LTexture GetTexture()
        {
            return fontBatch.GetTexture();
        }

        public LSTRFont(LFont font, bool anti, char[] additionalChars)
        {
            if (displays == null)
            {
                displays = new System.Collections.Generic.Dictionary<string, Loon.Core.Graphics.Opengl.LTextureBatch.GLCache>(totalCharSet);
            }
            else
            {
                displays.Clear();
            }
            this.useCache = true;
            this.font = font;
            this.fontSize = font.GetSize();
            this.ascent = font.GetAscent();
            this.antiAlias = anti;
            if (antiAlias)
            {
                if (trueFont == null)
                {
                    trueFont = LFont.GetTrueFont();
                }
                if (additionalChars != null && additionalChars.Length > (textureWidth / trueFont.GetSize()))
                {
                    this.textureWidth *= 2;
                    this.textureHeight *= 2;
                }
                this.fontScale = (float)fontSize / (float)trueFont.GetSize();
                this.Make(trueFont, additionalChars);
            }
            else
            {
                this.Make(this.font, additionalChars);
            }
        }

        private void Make(LFont font, char[] customCharsArray)
        {
            if (charArray == null)
            {
                charArray = new IntObject[totalCharSet];
            }
            if (customCharsArray != null && customCharsArray.Length > totalCharSet)
            {
                textureWidth *= 2;
            }
            try
            {
                LImage imgTemp = LImage.CreateImage(textureWidth, textureHeight, true);
                LGraphics g = imgTemp.GetLGraphics();
                g.SetFont(font);
                int rowHeight = 0;
                int positionX = 0;
                int positionY = 0;
                int customCharsLength = (customCharsArray != null) ? customCharsArray.Length
                        : 0;
                this.totalCharSet = customCharsLength == 0 ? totalCharSet : 0;
                StringBuilder sbr = new StringBuilder(totalCharSet);
                for (int i = 0; i < totalCharSet + customCharsLength; i++)
                {
                    char ch = (i < totalCharSet) ? (char)i : customCharsArray[i
                            - totalCharSet];

                    int charwidth = font.CharWidth(ch);
                    if (charwidth <= 0)
                    {
                        charwidth = 1;
                    }
                    int charheight = font.GetHeight();
                    if (charheight <= 0)
                    {
                        charheight = font.GetSize();
                    }

                    IntObject newIntObject = new IntObject();

                    newIntObject.width = charwidth;
                    newIntObject.height = charheight;

                    if (positionX + newIntObject.width >= textureWidth)
                    {
                        g.DrawString(sbr.ToString(), 0, positionY);
                        sbr.Clear();
                        positionX = 0;
                        positionY += rowHeight;
                        rowHeight = 0;
                    }

                    newIntObject.storedX = positionX;
                    newIntObject.storedY = positionY;

                    if (newIntObject.height > fontHeight)
                    {
                        fontHeight = newIntObject.height;

                    }

                    if (newIntObject.height > rowHeight)
                    {
                        rowHeight = newIntObject.height;
                    }

                    sbr.Append(ch);

                    positionX += newIntObject.width;

                    if (i < totalCharSet)
                    {
                        charArray[i] = newIntObject;

                    }
                    else
                    {

                        CollectionUtils.Put(customChars, ch, newIntObject);
                    }

                }
                if (sbr.Length > 0)
                {
                    g.DrawString(sbr.ToString(), 0, positionY);
                    sbr = null;
                }
                g.Dispose();
                g = null;

                fontBatch = new LTextureBatch(imgTemp.GetTexture());

            }
            catch (Exception ex)
            {
                Loon.Utils.Debugging.Log.Exception(ex);
            }
        }

        public void DrawString(string chars, float x, float y, float rotation,
                LColor color)
        {
            DrawString(x, y, 1f, 1f, 0, 0, rotation,
                    chars, color, 0, chars.Length - 1);
        }

        public void DrawString(string chars, float x, float y, float rotation)
        {
            DrawString(x, y, 1f, 1f, 0, 0, rotation,
                    chars, null, 0, chars.Length - 1);
        }

        public void DrawString(string chars, float x, float y, float sx, float sy,
                float ax, float ay, float rotation, LColor c)
        {
            DrawString(x, y, sx, sy, ax, ay, rotation, chars, null, 0,
                    chars.Length - 1);
        }

        private void DrawString(float x, float y, float sx, float sy, float ax,
                float ay, float rotation, string chars, LColor c, int startIndex,
                int endIndex)
        {

            if (displays.Count > LSystem.DEFAULT_MAX_CACHE_SIZE)
            {
                lock (displays)
                {
                    foreach (Loon.Core.Graphics.Opengl.LTextureBatch.GLCache cache in displays.Values)
                    {
                        if (cache != null)
                        {
                            cache.Dispose();
                        }
                    }
                }
                displays.Clear();
            }

            this.intObject = null;
            this.charCurrent = 0;
            this.totalWidth = 0;
            if (rotation != 0 && (ax == 0 && ay == 0))
            {
                ax = font.StringWidth(chars) / 2;
                ay = font.GetHeight();
            }
            if (useCache)
            {
                display = (Loon.Core.Graphics.Opengl.LTextureBatch.GLCache)CollectionUtils.Get(displays, chars);
                if (display == null)
                {
                    fontBatch.GLBegin();
                    char[] charList = chars.ToCharArray();
                    for (int i = 0; i < charList.Length; i++)
                    {
                        charCurrent = charList[i];
                        if (charCurrent < totalCharSet)
                        {
                            intObject = charArray[charCurrent];
                        }
                        else
                        {
                            intObject = (IntObject)CollectionUtils.Get(customChars,
                                 (char)charCurrent);

                        }

                        if (intObject != null)
                        {
                            if ((i >= startIndex) || (i <= endIndex))
                            {
                                if (antiAlias)
                                {
                                    fontBatch.DrawQuad(totalWidth * fontScale, 0,
                                            (totalWidth + intObject.width) * fontScale,
                                            (intObject.height * fontScale), intObject.storedX,
                                            intObject.storedY, intObject.storedX
                                                    + intObject.width,
                                            intObject.storedY + intObject.height);
                                }
                                else
                                {
                                    fontBatch.DrawQuad(totalWidth, 0,
                                          (totalWidth + intObject.width),
                                          intObject.height, intObject.storedX,
                                          intObject.storedY, intObject.storedX
                                                  + intObject.width,
                                          intObject.storedY + intObject.height);
                                }
                            }
                            totalWidth += intObject.width;
                        }
                    }
                    fontBatch.CommitQuad(c, x, y, sx, sy, ax, ay, rotation);
                    CollectionUtils.Put(displays, chars, display = fontBatch.NewGLCache());
                }
                else if (display != null && fontBatch != null
                      && fontBatch.GetTexture() != null)
                {
                    LTextureBatch.CommitQuad(fontBatch.GetTexture(), display, c, x,
                    y, sx, sy, ax, ay, rotation);
                }
            }
            else
            {
                fontBatch.GLBegin();
                char[] charList = chars.ToCharArray();
                for (int i = 0; i < charList.Length; i++)
                {
                    charCurrent = charList[i];
                    if (charCurrent < totalCharSet)
                    {
                        intObject = charArray[charCurrent];
                    }
                    else
                    {
                        intObject = (IntObject)CollectionUtils.Get(customChars,
                                (char)charCurrent);
                    }
                    if (intObject != null)
                    {
                        if ((i >= startIndex) || (i <= endIndex))
                        {
                            if (antiAlias)
                            {
                                fontBatch.DrawQuad(totalWidth * fontScale, 0,
                                    (totalWidth + intObject.width) * fontScale,
                                    (intObject.height * fontScale), intObject.storedX,
                                    intObject.storedY, intObject.storedX
                                            + intObject.width, intObject.storedY
                                            + intObject.height);
                            }
                            else
                            {
                                fontBatch.DrawQuad(totalWidth, 0,
                                        (totalWidth + intObject.width),
                                        intObject.height, intObject.storedX,
                                        intObject.storedY, intObject.storedX
                                                + intObject.width, intObject.storedY
                                                + intObject.height);
                            }
                        }
                        totalWidth += intObject.width;
                    }
                }
                fontBatch.CommitQuad(c, x, y, sx, sy, ax, ay, rotation);
            }
        }

        public void AddChar(char c, float x, float y, LColor color)
        {
            this.charCurrent = c;
            if (charCurrent < totalCharSet)
            {
                intObject = charArray[charCurrent];
            }
            else
            {
                intObject = (IntObject)CollectionUtils.Get(customChars, (char)charCurrent);
            }
            if (intObject != null)
            {
                if (color != null)
                {
                    SetImageColor(color);
                }
                if (antiAlias)
                {
                    fontBatch.Draw(colors, x, y, intObject.width * fontScale,
                             intObject.height * fontScale, intObject.storedX, intObject.storedY,
                            intObject.storedX + intObject.width, intObject.storedY
                                    + intObject.height);
                }
                else
                {
                    fontBatch.Draw(colors, x, y, intObject.width,
                            intObject.height, intObject.storedX, intObject.storedY,
                            intObject.storedX + intObject.width, intObject.storedY
                                    + intObject.height);
                }
                if (colors != null)
                {
                    colors = null;
                }
            }
        }


        public void StartChar()
        {
            fontBatch.GLBegin();
        }

        public void StopChar()
        {
            fontBatch.GLEnd();
        }

        public void PostCharCache()
        {
            fontBatch.PostLastCache();
        }

        public Loon.Core.Graphics.Opengl.LTextureBatch.GLCache SaveCharCache()
        {
            fontBatch.DisposeLastCache();
            return fontBatch.NewGLCache();
        }

        public LTextureBatch GetFontBatch()
        {
            return fontBatch;
        }

        private void SetImageColor(float r, float g, float b)
        {
            SetColor(LTexture.TOP_LEFT, r, g, b);
            SetColor(LTexture.TOP_RIGHT, r, g, b);
            SetColor(LTexture.BOTTOM_LEFT, r, g, b);
            SetColor(LTexture.BOTTOM_RIGHT, r, g, b);
        }

        private void SetImageColor(LColor c)
        {
            if (c == null)
            {
                return;
            }
            SetImageColor(c.r, c.g, c.b);
        }

        private void SetColor(int corner, float r, float g, float b)
        {
            if (colors == null)
            {
                colors = new LColor[] { new LColor(1, 1, 1, 1f),
					new LColor(1, 1, 1, 1f), new LColor(1, 1, 1, 1f),
					new LColor(1, 1, 1, 1f) };
            }
            colors[corner].r = r;
            colors[corner].g = g;
            colors[corner].b = b;
        }


        public int CharWidth(char c)
        {
            return font.CharWidth(c);
        }

        public int GetWidth(string s)
        {
            int totalWidth = 0;
            IntObject intObject = null;
            int currentChar = 0;
            char[] charList = s.ToCharArray();
            for (int i = 0; i < charList.Length; i++)
            {
                currentChar = charList[i];
                if (currentChar < totalCharSet)
                {
                    intObject = charArray[currentChar];
                }
                else
                {
                    intObject = (IntObject)CollectionUtils.Get(customChars,
                           currentChar);
                }

                if (intObject != null)
                    totalWidth += intObject.width;
            }
            return totalWidth;
        }

        public int GetHeight()
        {
            return fontSize;
        }

        public int GetSize()
        {
            return fontSize;
        }

        public int GetLineHeight()
        {
            return fontSize;
        }

        public float GetAscent()
        {
            return ascent;
        }

        public LFont GetFont()
        {
            return font;
        }

        public int GetTotalCharSet()
        {
            return totalCharSet;
        }

        public bool IsUseCache()
        {
            return useCache;
        }

        public void SetUseCache(bool useCache)
        {
            this.useCache = useCache;
        }

        public void Dispose()
        {
            if (fontBatch != null)
            {
                fontBatch.DestoryAll();
            }
            foreach (Loon.Core.Graphics.Opengl.LTextureBatch.GLCache c in displays.Values)
            {
                if (c != null)
                {
                    c.Dispose();
                }
            }
        }
    }
}
