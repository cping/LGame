using System.Collections.Generic;
using Loon.Core.Geom;
using System;
using Loon.Utils.Collection;
using Loon.Core.Resource;
using Loon.Core.Graphics;
using Loon.Core.Graphics.Opengl;
using Loon.Utils;
namespace Loon.Action.Sprite
{

    public class SpriteFont
    {

        public static SpriteFont Read(string resName)
        {
            try
            {
                List<RectBox> xGlyphs = new List<RectBox>(), xCropping = new List<RectBox>();
                List<Char> xChars = new List<Char>();
                int xSpacingV;
                float xSpacingH;
                List<float[]> xKerning = new List<float[]>();

                ArrayByte arrays = new ArrayByte(Resources.OpenResource(resName),
                        ArrayByte.BIG_ENDIAN);

                int size = arrays.ReadInt();

                LImage image = LImage.CreateImage(arrays.ReadByteArray(size));

                int count = arrays.ReadInt();
                while (count-- > 0)
                {
                    xGlyphs.Add(new RectBox(arrays.ReadInt(), arrays.ReadInt(),
                            arrays.ReadInt(), arrays.ReadInt()));
                    xCropping.Add(new RectBox(arrays.ReadInt(), arrays.ReadInt(),
                            arrays.ReadInt(), arrays.ReadInt()));
                    xChars.Add((char)arrays.ReadInt());
                }

                xSpacingV = arrays.ReadInt();
                xSpacingH = arrays.ReadFloat();

                count = arrays.ReadInt();
                while (count-- > 0)
                {
                    xKerning.Add(new float[] { arrays.ReadFloat(),
							arrays.ReadFloat(), arrays.ReadFloat() });
                }
                arrays.Dispose();
                return new SpriteFont(new LTexture(GLLoader.GetTextureData(image),
                        Loon.Core.Graphics.Opengl.LTexture.Format.LINEAR), xGlyphs, xCropping, xChars, xSpacingV,
                        xSpacingH, xKerning, 'A');
            }
            catch (Exception e)
            {
                Loon.Utils.Debugging.Log.Exception(e);
            }
            return null;
        }

        protected internal LTexture texture;
        protected internal List<RectBox> glyphs;
        protected internal List<RectBox> cropping;
        protected internal List<Char> charMap;
        protected internal int lineSpacing;
        protected internal int maxCharY;
        protected internal float spacing;
        protected internal List<float[]> kerning;
        protected internal char defaultchar;

        public SpriteFont(LTexture tex2d, List<RectBox> gs,
                List<RectBox> crops, List<Char> chars, int line,
                float space, List<float[]> kern, char def)
        {
            this.texture = tex2d;
            this.glyphs = gs;
            this.cropping = crops;
            this.charMap = chars;
            this.lineSpacing = 0;
            this.spacing = 0f;
            this.kerning = kern;
            this.defaultchar = def;
            Int32 max = 0;
            foreach (RectBox rect in glyphs)
            {
                if (max == null || rect.GetHeight() > max)
                {
                    max = (int)rect.GetHeight();
                }
            }
            this.maxCharY = max;
        }

        protected internal void DrawChar(SpriteBatch batch, float x, float y, RectBox glyph,
                RectBox cropping, LColor color)
        {
            batch.Draw(texture, x + cropping.x, y + cropping.y, glyph.GetWidth(),
                    glyph.GetHeight(), glyph.GetX(), glyph.GetY(),
                    glyph.GetWidth(), glyph.GetHeight(), color);
        }

        protected internal void DrawString(SpriteBatch batch, string cs, float x,
                float y)
        {
            DrawString(batch, cs, x, y, LColor.white);
        }

        protected internal void DrawString(SpriteBatch batch, string cs, float x,
                float y, LColor color)
        {
            float xx = 0, yy = 0;
            for (int i = 0; i < cs.Length; i++)
            {
                char c = cs[i], c2 = (i != 0) ? (char)(cs[i - 1]) : (char)(0);
                if (c2 != 0)
                {
                    for (int j = 0; j < kerning.Count; j++)
                    {
                        if (kerning[j][1] == c && kerning[j][0] == c2)
                        {
                            xx += kerning[j][2];
                            break;
                        }
                    }
                }
                for (int j = 0; j < charMap.Count; j++)
                {
                    if (charMap[j] != c)
                    {
                        continue;
                    }

                    DrawChar(batch, x + xx, y + yy, glyphs[j], cropping[j],
                            color);
                    xx += glyphs[j].GetWidth() + spacing;
                }
                if (c == '\n')
                {
                    xx = 0;
                    yy += GetLineHeight();
                }
            }
        }

        private readonly Vector2f pos = new Vector2f();

        protected internal void DrawString(SpriteBatch batch, string cs,
                Vector2f local, LColor color, float rotation, Vector2f origin,
                Vector2f scale, SpriteEffects spriteEffects)
        {
            pos.Set(0, 0);
            int flip = 1;
            float beginningofline = 0f;
            bool flag = true;
            if (spriteEffects == SpriteEffects.FlipHorizontally)
            {
                beginningofline = this.Measure(cs).x * scale.x;
                flip = -1;
            }
            if (spriteEffects == SpriteEffects.FlipVertically)
            {
                pos.y = (this.Measure(cs).y - this.lineSpacing) * scale.y;
            }
            else
            {
                pos.y = 0f;
            }
            pos.x = beginningofline;
            for (int i = 0; i < cs.Length; i++)
            {
                char character = cs[i];
                switch ((int)character)
                {
                    case '\r':
                        break;
                    case '\n':
                        flag = true;
                        pos.x = beginningofline;
                        if (spriteEffects == SpriteEffects.FlipVertically)
                        {
                            pos.y -= this.lineSpacing * scale.y;
                        }
                        else
                        {
                            pos.y += this.lineSpacing * scale.y;
                        }
                        break;
                    default:
                        {
                            int indexForCharacter = this.CharacterIndex(character);
                            float[] charkerning = this.kerning[indexForCharacter];
                            if (flag)
                            {
                                charkerning[0] = Math.Max(charkerning[0], 0f);
                            }
                            else
                            {
                                pos.x += (this.spacing * scale.x) * flip;
                            }
                            pos.x += (charkerning[0] * scale.x) * flip;
                            RectBox rectangle = this.glyphs[indexForCharacter];
                            RectBox rectangle2 = this.cropping[indexForCharacter];
                            Vector2f position = pos.Cpy();
                            position.x += rectangle2.x * scale.x;
                            position.y += rectangle2.y * scale.y;
                            position.AddLocal(local);
                            batch.Draw(this.texture, position, rectangle, color, rotation,
                                    origin, scale, spriteEffects);
                            flag = false;
                            pos.x += ((charkerning[1] + charkerning[2]) * scale.x) * flip;
                            break;
                        }
                }
            }
        }

        public int GetWidth(string cs)
        {
            List<Single> list = new List<Single>();
            int y = 0;
            for (int i = 0; i < cs.Length; i++)
            {
                if (list.Count <= y)
                {
                    list.Add(0f);
                }
                char c = cs[i], c2 = (i != 0) ? (char)(cs[i - 1]) : (char)(0);
                if (c2 != 0)
                    for (int j = 0; i < kerning.Count; j++)
                    {
                        if (kerning[j][1] == c && kerning[j][0] == c2)
                        {
                            list[y]= list[y] + kerning[j][2];
                            break;
                        }
                    }
                for (int j = 0; j < charMap.Count; j++)
                {
                    if (charMap[j] != c)
                    {
                        continue;
                    }
                    list[y]= list[y] + glyphs[j].GetWidth();
                    if (i != cs.Length - 1)
                    {
                        list[y]= list[y] + spacing;
                    }
                }
                if (c == '\n')
                {
                    y++;
                }
            }
            Single maxX = 0;
            for (int j = 0; j < list.Count; j++)
            {
                if (maxX == 0 || list[j] > maxX)
                {
                    maxX = list[j];
                }
            }
            return (int)(float)maxX;
        }

        public int GetHeight(string cs)
        {
            return StringUtils.Split(cs,"\\\\n").Length * GetLineHeight();
        }

        public int GetLineHeight()
        {
            return maxCharY + lineSpacing;
        }

        public int CharacterIndex(char character)
        {
            int lowindex = 0;
            int highindex = this.charMap.Count - 1;
            while (lowindex <= highindex)
            {
                int index = lowindex + ((highindex - lowindex) >> 1);
                if (this.charMap[index] == character)
                {
                    return index;
                }
                if (this.charMap[index] < character)
                {
                    lowindex = index + 1;
                }
                else
                {
                    highindex = index - 1;
                }
            }

            if (this.defaultchar != '\0')
            {
                char ch = this.defaultchar;
                if (character != ch)
                {
                    return this.CharacterIndex(ch);
                }
            }
            throw new ArgumentException("Character not in Font");
        }

        protected internal Vector2f Measure(string cs)
        {
            if (cs.Length == 0)
            {
                return new Vector2f();
            }
            Vector2f zero = new Vector2f();
            zero.y = this.lineSpacing;
            float min = 0f;
            int count = 0;
            float z = 0f;
            bool flag = true;

            for (int i = 0; i < cs.Length; i++)
            {
                if (cs[i] != '\r')
                {
                    if (cs[i] == '\n')
                    {
                        zero.x += MathUtils.Max(z, 0f);
                        z = 0f;
                        min = MathUtils.Max(zero.x, min);
                        zero = new Vector2f();
                        zero.y = this.lineSpacing;
                        flag = true;
                        count++;
                    }
                    else
                    {
                        float[] vector2 = this.kerning[this.CharacterIndex(cs
                                [i])];
                        if (flag)
                        {
                            vector2[0] = MathUtils.Max(vector2[0], 0f);
                        }
                        else
                        {
                            zero.x += this.spacing + z;
                        }
                        zero.x += vector2[0] + vector2[1];
                        z = vector2[2];
                        RectBox rectangle = this.cropping[this
                                .CharacterIndex(cs[i])];
                        zero.y = MathUtils.Max(zero.y, rectangle.height);
                        flag = false;
                    }
                }
            }
            zero.x += MathUtils.Max(z, 0f);
            zero.y += count * this.lineSpacing;
            zero.x = MathUtils.Max(zero.x, min);
            return zero;
        }

        public Vector2f MeasureString(string cs)
        {
            return Measure(cs);
        }
    }
}
