namespace Loon.Action.Sprite
{
    using System;
    using System.Collections.Generic;
    using Loon.Core;
    using Loon.Core.Graphics;
    using Loon.Core.Graphics.OpenGL;
    using Microsoft.Xna.Framework;
    using Loon.Core.Geom;

    public class Label : LObject, ISprite
    {

        private LFont font;

        private bool visible;

        private int width, height;

        private LColor color;

        private string label;

        public Label(string l, int x, int y):this(LFont.GetDefaultFont(), l, x, y)
        {
            
        }

        public Label(string l, string font, int type, int size, int x, int y):this(new LFont(font, type, size), l, x, y)
        {
            
        }

        public Label(LFont f, string l, int x, int y)
        {
            this.font = f;
            this.label = l;
            this.color = LColor.black;
            this.visible = true;
            this.SetLocation(x, y);
        }

        public void SetFont(string fontName, int type, int size)
        {
            SetFont(new LFont(fontName, type, size));
        }

        public void SetFont(LFont f)
        {
            this.font = f;
        }

        public virtual void CreateUI(GLEx g)
        {
            if (visible)
            {
                LFont oldFont = g.GetFont();
                Color oldColor = g.GetColor();
                g.SetFont(font);
                g.SetColor(color);
                this.width = font.StringWidth(label);
                this.height = font.GetSize();
                if (alpha > 0 && alpha < 1)
                {
                    g.SetAlpha(alpha);
                    g.DrawString(label, X(), Y() - font.GetAscent());
                    g.SetAlpha(1.0F);
                }
                else
                {
                    g.DrawString(label, X(), Y() - font.GetAscent());
                }
                g.SetFont(oldFont);
                g.SetColor(oldColor);
            }
        }

        public override int GetWidth()
        {
            return width;
        }

        public override int GetHeight()
        {
            return height;
        }

        public override void Update(long timer)
        {

        }

        public virtual RectBox GetCollisionBox()
        {
            return GetRect(X(), Y(), width, height);
        }

        public virtual bool IsVisible()
        {
            return visible;
        }

        public virtual void SetVisible(bool visible_0)
        {
            this.visible = visible_0;
        }

        public string GetLabel()
        {
            return label;
        }

        public void SetLabel(int l)
        {
            SetLabel(l.ToString());
        }

        public void SetLabel(string l)
        {
            this.label = l;
        }

        public LColor GetColor()
        {
            return color;
        }

        public void SetColor(LColor color_0)
        {
            this.color = color_0;
        }

        public virtual LTexture GetBitmap()
        {
            return null;
        }

        public virtual void Dispose()
        {

        }

    }
}
