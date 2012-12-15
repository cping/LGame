using Loon.Core;
using Loon.Core.Graphics.Opengl;
using System;
using Loon.Core.Graphics;
using Loon.Core.Graphics.Device;
using Loon.Utils;
using Loon.Core.Geom;
namespace Loon.Action.Sprite
{

    public class StatusBar : LObject, ISprite
    {

        protected static internal readonly System.Collections.Generic.Dictionary<Int32, LTexture> colors = new System.Collections.Generic.Dictionary<Int32, LTexture>(
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

        public StatusBar(int width, int height)
            : this(0, 0, width, height)
        {

        }

        public StatusBar(int x, int y, int width, int height)
            : this(100, 100, x, y, width, height)
        {

        }

        public StatusBar(int v, int max, int x, int y, int width, int height)
        {
            lock (typeof(StatusBar))
            {
                quoteCount++;
            }
            this.value_ren = v;
            this.valueMax = max;
            this.valueMin = value_ren;
            this.current = (int)((float)(width * value_ren) / (float)valueMax);
            this.goal = (int)((float)(width * valueMin) / (float)valueMax);
            this.width = width;
            this.height = height;
            this.visible = true;
            this.hit = true;
            this.texture = LoadBarColor(LColor.gray, LColor.red, LColor.orange);
            this.SetLocation(x, y);
        }

        public LTexture LoadBarColor(LColor c1, LColor c2, LColor c3)
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
            hash = LSystem.Unite(hash, c1.GetRGB());
            hash = LSystem.Unite(hash, c2.GetRGB());
            hash = LSystem.Unite(hash, c3.GetRGB());
            LTexture texture = null;
            lock (colors)
            {
                texture = (LTexture)CollectionUtils.Get(colors, hash);
            }
            if (texture == null)
            {
                LImage image = LImage.CreateImage(8, 8, false);
                LGraphics g = image.GetLGraphics();
                g.SetColor(c1);
                g.FillRect(0, 0, 4, 4);
                g.SetColor(c2);
                g.FillRect(4, 0, 4, 4);
                g.SetColor(c3);
                g.FillRect(0, 4, 4, 4);
                g.Dispose();
                texture = image.GetTexture();
                CollectionUtils.Put(colors, hash, texture);
            }
            return (this.texture = texture);
        }

        public void Set(int v)
        {
            this.value_ren = v;
            this.valueMax = v;
            this.valueMin = v;
            this.current = (int)((float)(width * value_ren) / (float)valueMax);
            this.goal = (int)((float)(width * valueMin) / (float)valueMax);
        }

        public void Empty()
        {
            this.value_ren = 0;
            this.valueMin = 0;
            this.current = (int)((float)(width * value_ren) / (float)valueMax);
            this.goal = (int)((float)(width * valueMin) / (float)valueMax);
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
                        ((current * valueMax) / width), value_ren);
            }
            else
            {
                current++;
                value_ren = MathUtils.Mid(value_ren, ((current * valueMax) / width),
                        valueMin);
            }
            return true;
        }

        public void CreateUI(GLEx g)
        {
            if (visible)
            {
                if (showValue)
                {
                    hpString = "" + value_ren;
                    g.SetColor(LColor.white);
                    int cur = g.GetFont().StringWidth(hpString);
                    int h = g.GetFont().GetSize();
                    g.DrawString("" + value_ren, (X() + width / 2 - cur / 2) + 2,
                            (Y() + height / 2 + h / 2));
                }
                DrawBar(g, goal, current, width, GetX(), GetY());
            }
        }

        public RectBox GetCollisionBox()
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

        public virtual void SetVisible(bool visible)
        {
            this.visible = visible;
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

        public void SetMaxValue(int valueMax)
        {
            this.valueMax = valueMax;
            this.current = (width * value_ren) / valueMax;
            this.goal = (width * valueMin) / valueMax;
            this.State();
        }

        public int GetMinValue()
        {
            return valueMin;
        }

        public void SetMinValue(int valueMin)
        {
            this.valueMin = valueMin;
            this.current = (width * value_ren) / valueMax;
            this.goal = (width * valueMin) / valueMax;
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

        public void SetHit(bool hit)
        {
            this.hit = hit;
        }

        public LTexture GetBitmap()
        {
            return texture;
        }

        public void Dispose()
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
