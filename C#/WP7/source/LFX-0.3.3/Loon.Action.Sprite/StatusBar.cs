namespace Loon.Action.Sprite
{
    using System;
    using System.Collections.Generic;
    using Loon.Core;
    using Loon.Core.Graphics.OpenGL;
    using Loon.Core.Graphics;
    using Loon.Utils;
    using Loon.Core.Geom;
    using Microsoft.Xna.Framework;

    public class StatusBar : LObject, ISprite
    {

        private static readonly Dictionary<Int32, LTexture> colors = new Dictionary<Int32, LTexture>(
                10);

        private static readonly int[] backPos = { 1, 1, 3, 3 };

        private static readonly int[] beforePos = { 5, 1, 7, 3 };

        private static readonly int[] afterPos = { 1, 5, 3, 7 };

        private static int quoteCount = 0;

        protected internal bool hit, visible, showValue, dead;

        private int width, height;

        private int value_ren, valueMax, valueMin;

        private int current, goal;

        private string hpString;

        private LTexture texture;

        private static bool useBegin;

        public StatusBar(int width, int height):  this(0, 0, width, height)
        {
          
        }

        public StatusBar(int x, int y, int width, int height): this(100, 100, x, y, width, height)
        {
       
        }

        public StatusBar(int value_ren, int max, int x, int y, int width, int height)
        {
            quoteCount++;
            this.value_ren = value_ren;
            this.valueMax = max;
            this.valueMin = value_ren;
            this.current = (width * value_ren) / valueMax;
            this.goal = (width * valueMin) / valueMax;
            this.width = width;
            this.height = height;
            this.visible = true;
            this.hit = true;
            this.texture = LoadBarColor(Color.Gray, Color.Red, Color.Orange);
            this.SetLocation(x, y);
        }

        /// <summary>
        /// Ë³ÐòÎª±³¾°£¬Ç°¾°£¬ÖÐ¾°
        /// </summary>
        ///
        /// <param name="c1"></param>
        /// <param name="c2"></param>
        /// <param name="c3"></param>
        /// <returns></returns>
        public LTexture LoadBarColor(Color c1, Color c2, Color c3)
        {
            if (colors.Count > 10)
            {
                lock (colors)
                {
                    foreach (LTexture tex2d in colors.Values)
                    {
                        if (tex2d != null)
                        {
                            tex2d.Destroy();
                        }
                    }
                    colors.Clear();
                }
            }
            int hash = 1;
            hash = LSystem.Unite(hash, c1.PackedValue);
            hash = LSystem.Unite(hash, c2.PackedValue);
            hash = LSystem.Unite(hash, c3.PackedValue);
            LTexture texture_0 = null;
            lock (colors)
            {
                texture_0 = (LTexture)CollectionUtils.Get(colors, hash);
            }
            if (texture_0 == null)
            {
                LPixmap image = new LPixmap(8, 8, true);
                image.SetColor(c1);
                image.FillRect(0, 0, 4, 4);
                image.SetColor(c2);
                image.FillRect(4, 0, 4, 4);
                image.SetColor(c3);
                image.FillRect(0, 4, 4, 4);
                image.Dispose();
                texture_0 = image.Texture;
                CollectionUtils.Put(colors, hash, texture_0);
            }
            return (this.texture = texture_0);
        }

        public LTexture LoadBarColor(LColor c1, LColor c2, LColor c3)
        {
            return LoadBarColor(c1.Color, c2.Color, c3.Color);
        }

        public void Set(int v)
        {
            this.value_ren = v;
            this.valueMax = v;
            this.valueMin = v;
            this.current = (width * value_ren) / valueMax;
            this.goal = (width * valueMin) / valueMax;
        }

        public void Empty()
        {
            this.value_ren = 0;
            this.valueMin = 0;
            this.current = (width * value_ren) / valueMax;
            this.goal = (width * valueMin) / valueMax;
        }

        public static void GLBegin()
        {
            lock (colors)
            {
                foreach (LTexture tex2d in colors.Values)
                {
                    if (tex2d != null)
                    {
                        tex2d.GLBegin();
                    }
                }
                useBegin = true;
            }
        }

        public static void GLEnd()
        {
            lock (colors)
            {
                foreach (LTexture tex2d in colors.Values)
                {
                    if (tex2d != null)
                    {
                        tex2d.GLEnd();
                    }
                }
                useBegin = false;
            }
        }

        private void DrawBar(GLEx g, float v1, float v2, float size, float x,
                float y)
        {
            float cv1 = (width * v1) / size;
            float cv2;
            if (v1 == v2)
            {
                cv2 = cv1;
            }
            else
            {
                cv2 = (width * v2) / size;
            }
            if (!useBegin)
            {
                texture.GLBegin();
            }
            if (cv1 < width || cv2 < height)
            {
                texture.Draw(x, y, width, height, backPos[0], backPos[1],
                        backPos[2], backPos[3]);
            }
            if (valueMin < value_ren)
            {
                if (cv1 == width)
                {
                    texture.Draw(x, y, cv1, height, beforePos[0], beforePos[1],
                            beforePos[2], beforePos[3]);
                }
                else
                {
                    if (!dead)
                    {
                        texture.Draw(x, y, cv2, height, afterPos[0], afterPos[1],
                                afterPos[2], afterPos[3]);
                    }
                    texture.Draw(x, y, cv1, height, beforePos[0], beforePos[1],
                           beforePos[2], beforePos[3]);
                }
            }
            else
            {
                if (cv2 == width)
                {
                    texture.Draw(x, y, cv2, height, beforePos[0], beforePos[1],
                            beforePos[2], beforePos[3]);
                }
                else
                {
                    texture.Draw(x, y, cv1, height, afterPos[0], afterPos[1],
                            afterPos[2], afterPos[3]);
                    texture.Draw(x, y, cv2, height, beforePos[0], beforePos[1],
                            beforePos[2], beforePos[3]);
                }
            }
            if (!useBegin)
            {
                texture.GLEnd();
            }
        }

        public void UpdateTo(int v1, int v2)
        {
            this.SetValue(v1);
            this.SetUpdate(v2);
        }

        public void SetUpdate(int val)
        {
            valueMin = MathUtils.Mid(0, val, valueMax);
            current = (width * value_ren) / valueMax;
            goal = (width * valueMin) / valueMax;
        }

        public void SetDead(bool d)
        {
            this.dead = d;
        }

        public bool State()
        {
            if (current == goal)
            {
                return false;
            }
            if (current > goal)
            {
                current--;
                value_ren = MathUtils.Mid(valueMin,
                        (int)((current * valueMax) / width), value_ren);
            }
            else
            {
                current++;
                value_ren = MathUtils.Mid(value_ren, (int)((current * valueMax) / width),
                        valueMin);
            }
            return true;
        }

        public virtual void CreateUI(GLEx g)
        {
            if (visible)
            {
                if (showValue)
                {
                    hpString = "" + value_ren;
                    g.SetColor(LColor.white);
                    int current_0 = g.GetFont().StringWidth(hpString);
                    int h = g.GetFont().GetSize();
                    g.DrawString("" + value_ren, (X() + width / 2 - current_0 / 2) + 2,
                            (Y() + height / 2 + h / 2));
                }
                DrawBar(g, goal, current, width, GetX(), GetY());
            }
        }

        public virtual RectBox GetCollisionBox()
        {
            return GetRect(X(), Y(), width, height);
        }

        public bool IsShowHP()
        {
            return showValue;
        }

        public void SetShowHP(bool showHP)
        {
            this.showValue = showHP;
        }

        public override int GetWidth()
        {
            return width;
        }

        public override int GetHeight()
        {
            return height;
        }

        public virtual bool IsVisible()
        {
            return visible;
        }

        public virtual void SetVisible(bool visible_0)
        {
            this.visible = visible_0;
        }

        public override void Update(long elapsedTime)
        {
            if (visible && hit)
            {
                State();
            }
        }

        public int GetMaxValue()
        {
            return valueMax;
        }

        public void SetMaxValue(int valueMax_0)
        {
            this.valueMax = valueMax_0;
            this.current = (width * value_ren) / valueMax_0;
            this.goal = (width * valueMin) / valueMax_0;
            this.State();
        }

        public int GetMinValue()
        {
            return valueMin;
        }

        public void SetMinValue(int valueMin_0)
        {
            this.valueMin = valueMin_0;
            this.current = (width * value_ren) / valueMax;
            this.goal = (width * valueMin_0) / valueMax;
            this.State();
        }

        public int GetValue()
        {
            return value_ren;
        }

        public void SetValue(int value_ren)
        {
            this.value_ren = value_ren;
        }

        public bool IsHit()
        {
            return hit;
        }

        public void SetHit(bool h)
        {
            this.hit = h;
        }

        public virtual LTexture GetBitmap()
        {
            return texture;
        }

        public virtual void Dispose()
        {
            lock (colors)
            {
                quoteCount--;
                if (quoteCount <= 0)
                {
                    if (colors != null)
                    {
                        foreach (LTexture tex2d in colors.Values)
                        {
                            if (tex2d != null)
                            {
                                tex2d.Destroy();
                            }
                        }
                        colors.Clear();
                    }
                }
            }
        }
    }
}
