using System;
using System.Text;
using System.Text.RegularExpressions;
using System.Collections.Generic;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Loon.Utils;
using Loon.Core.Geom;
using Loon.Core.Event;
using Loon.Utils.Debug;

namespace Loon.Core.Graphics
{
    public class LFont
    {
        private static LFont staticFont;

        public static LFont GetDefaultFont()
        {
            return staticFont;
        }

        public static void SetDefaultFont(LFont font)
        {
            if (staticFont != null)
            {
                staticFont = null;
            }
            staticFont = font;
        }

        public static LFont GetFont(int size)
        {
            return LFont.GetFont(LSystem.FONT_NAME, 0, size);
        }

        public static LFont GetFont(String familyName, int size)
        {
            return GetFont(familyName, 0, size);
        }

        public static LFont GetFont(String familyName, int style, int size)
        {
            return new LFont(familyName,style,size);
        }

        public const int LEFT = 1;

        public const int RIGHT = 2;

        public const int CENTER = 3;

        public const int JUSTIFY = 4;

        public const int FACE_SYSTEM = 0;

        public const int FACE_MONOSPACE = 32;

        public const int FACE_PROPORTIONAL = 64;

        public const int FONT_STATIC_TEXT = 0;

        public const int FONT_INPUT_TEXT = 1;

        public const int SIZE_SMALL = 8;

        public const int SIZE_LARGE = 16;

        public const int SIZE_MEDIUM = 0;

        public const int STYLE_PLAIN = 0;

        public const int STYLE_BOLD = 1;

        public const int STYLE_ITALIC = 2;

        public const int STYLE_UNDERLINED = 4;

        private static Dictionary<string, SpriteFont> baseFonts;

        private float ascent, descent;

        private string faceName;

        private SpriteFont spriteFont;

        private int size, style;

        private bool isLoaded;

        private string path;

        public LFont(string face, int style, int fontSize)
            : this(null, face, style, fontSize)
        {

        }

        public LFont(string face)
            : this(null, face)
        {

        }
        public LFont(string dir,string face)
            : this(dir, face, 0,20)
        {
       
        }

        public LFont(string dir, string face, int style,int fontSize)
        {
            if (baseFonts == null)
            {
                baseFonts = new Dictionary<string, SpriteFont>(CollectionUtils.INITIAL_CAPACITY);
            }
            else
            {
                baseFonts.Clear();
            }
            if (isLoaded)
            {
                return;
            }
            if (string.IsNullOrEmpty(dir)) { return; }
            if (string.IsNullOrEmpty(face)) { return; }
            this.faceName = face.Trim().ToLower();
            this.spriteFont = (SpriteFont)CollectionUtils.Get(baseFonts, faceName);
            if (spriteFont == null)
            {
                if ("xnb".Equals(FileUtils.GetExtension(face), StringComparison.InvariantCultureIgnoreCase))
                {
                    int idx = face.LastIndexOf(".");
                    face = StringUtils.Substring(face, 0, idx);
                }
                this.size = fontSize;
                if (dir != null)
                {
                    this.path = dir + "/" + face;
                }
                else
                {
                    this.path = face;
                }
            }
            else
            {
                this.isLoaded = true;
            }
            SetAscent(fontSize);
        }

        public static LFont GetFromAssetFont(string path, string face,int style, int fontSize)
        {
            return new LFont(path, face,style, fontSize);
        }

        public int Style
        {
            get
            {
                return this.style;
            }

            set
            {
                this.ascent = style;
            }
        }

        public float Ascent
        {
            get
            {
                return this.ascent;
            }

            set
            {
                this.ascent = value;
            }
        }

        public float Descent
        {
            get
            {
                return this.descent;
            }

            set
            {
                this.descent = value;
            }
        }

        public void SetAscent(float a)
        {
            this.ascent = a;
        }

        public void SetDescent(float d)
        {
            this.descent = d;
        }

        public float GetAscent()
        {
            return ascent == 0 ? GetSize() : ascent;
        }

        public float GetDescent()
        {
            return descent;
        }

        public int GetBaselinePosition()
        {
            return MathUtils.Round(-ascent * size);
        }

        public int GetLineHeight()
        {
            return ((int)MathUtils.Ceil(MathUtils.Abs(ascent)
                    + MathUtils.Abs(descent))) - 2;
        }

        public int GetSize()
        {
            return size;
        }

        public int GetSpacing()
        {
            LoadFont();
            return (int)spriteFont.Spacing;
        }

        public string GetFontName()
        {
            return this.faceName;
        }

        public int CharWidth(char ch)
        {
            return StringWidth(Convert.ToString(ch));
        }

        public int CharsWidth(char[] ch, int offset, int length)
        {
            return SubStringWidth(new string(ch), offset, length);
        }

        private Vector2f mResult = new Vector2f();

        public Vector2f MeasureString(object text)
        {
            LoadFont();
            string tempString = text as string;
            StringBuilder tempStringBuilder = text as StringBuilder;
            if (tempString != null)
            {
                Vector2 v2 = spriteFont.MeasureString(tempString);
                mResult.Set(v2.X, v2.Y);
                return mResult;
            }
            else if (tempStringBuilder != null)
            {
                Vector2 v2 = spriteFont.MeasureString(tempStringBuilder);
                mResult.Set(v2.X, v2.Y);
                return mResult;
            }
            mResult.Set(0, 0);
            return mResult;
        }

        public int GetHeight()
        {
            LoadFont();
            return (int)((size + spriteFont.Spacing) * 1.2f);
        }

        public float GetLeading()
        {
            LoadFont();
            return spriteFont.LineSpacing;
        }

        public int StringWidth(string m)
        {
            LoadFont();
            return (int)spriteFont.MeasureString(m).X;
        }

        public int StringHeight(string str)
        {
            LoadFont();
            return (int)spriteFont.MeasureString(str).Y;
        }

        public int SubStringWidth(string str, int offset, int len)
        {
            return StringWidth(StringUtils.Substring(str, offset, len));
        }

        public static string BreakTextLines(string text,
               int maximumCharactersPerLine, int maximumLines)
        {
            if (maximumLines <= 0)
            {
                return null;
            }
            if (maximumCharactersPerLine <= 0)
            {
                return null;
            }
            if (string.IsNullOrEmpty(text))
            {
                return string.Empty;
            }

            if (text.Length < maximumCharactersPerLine)
            {
                return text;
            }

            StringBuilder stringBuilder = new StringBuilder(text);
            int currentLine = 0;
            int newLineIndex = 0;
            while (((text.Length - newLineIndex) > maximumCharactersPerLine) &&
                (currentLine < maximumLines))
            {
                text.IndexOf(' ', 0);
                int nextIndex = newLineIndex;
                while ((nextIndex >= 0) && (nextIndex < maximumCharactersPerLine))
                {
                    newLineIndex = nextIndex;
                    nextIndex = text.IndexOf(' ', newLineIndex + 1);
                }
                stringBuilder.Replace(' ', '\n', newLineIndex, 1);
                currentLine++;
            }

            return stringBuilder.ToString();
        }

        public static string BreakTextLines(string text,
            int maximumCharactersPerLine)
        {
            if (maximumCharactersPerLine <= 0)
            {
                return null;
            }

            if (string.IsNullOrEmpty(text))
            {
                return string.Empty;
            }

            if (text.Length < maximumCharactersPerLine)
            {
                return text;
            }
            StringBuilder stringBuilder = new StringBuilder(text);
            int currentLine = 0;
            int newLineIndex = 0;
            while (((text.Length - newLineIndex) > maximumCharactersPerLine))
            {
                text.IndexOf(' ', 0);
                int nextIndex = newLineIndex;
                while ((nextIndex >= 0) && (nextIndex < maximumCharactersPerLine))
                {
                    newLineIndex = nextIndex;
                    nextIndex = text.IndexOf(' ', newLineIndex + 1);
                }
                stringBuilder.Replace(' ', '\n', newLineIndex, 1);
                currentLine++;
            }

            return stringBuilder.ToString();
        }

        public void SetStyle(int s)
        {
            this.style = s;
        }

        public int GetStyle()
        {
            return style;
        }

        public int GetTextHeight()
        {
            return StringHeight("H");
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


        private class Font_Updateable : Updateable
        {
            LFont font;

            public Font_Updateable(LFont f)
            {
                this.font = f;

            }

            public void Action()
            {
                font.spriteFont = LSilverlightPlus.Get.Load<SpriteFont>(font.path);
                CollectionUtils.Put(baseFonts, font.faceName, font.spriteFont);
            }
        }

        private void LoadFont()
        {
            if (!isLoaded)
            {
                SpriteFont pFont = (SpriteFont)CollectionUtils.Get(baseFonts, faceName);
                if (pFont == null)
                {
                    Font_Updateable font = new Font_Updateable(this);
                    LSystem.Load(font);
                }
                else
                {
                    this.spriteFont = pFont;
                }
                isLoaded = true;
            }
        }

        public SpriteFont Font
        {
            get
            {
                LoadFont();
                return spriteFont;
            }
        }

        public SpriteFont GetSpriteFont()
        {

            return Font;
        }

        public void Dispose()
        {
            if (baseFonts != null)
            {
                baseFonts.Clear();
            }
            faceName = null;
            path = null;
            spriteFont = null;
            isLoaded = false;
        }

    }
}

