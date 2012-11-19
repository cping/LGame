using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework.Graphics;
using Loon.Core;
using Loon.Core.Graphics.OpenGL;
using Microsoft.Xna.Framework.Content;
using System.IO.IsolatedStorage;
using Loon.Core.Geom;
using Loon.Action.Map;

namespace Loon.Action.Sprite.Effect
{
    public class OutEffect : LObject, ISprite
    {

        private LTexture texture;

        private bool visible, complete;

        private int width, height;

        private int type, multiples;

        private RectBox limit;

        public OutEffect(String fileName, int code)
            : this(new LTexture(fileName), code)
        {

        }

        public OutEffect(LTexture t, int code)
            : this(t, LSystem.screenRect, code)
        {

        }

        public OutEffect(LTexture t, RectBox limit, int code)
        {
            this.texture = t;
            this.type = code;
            this.width = t.Width;
            this.height = t.Height;
            this.multiples = 1;
            this.limit = limit;
            this.visible = true;
        }

        public override void Update(long elapsedTime)
        {
            if (!complete)
            {
                switch (type)
                {
                    case Config.DOWN:
                        Move_45D_down(multiples);
                        break;
                    case Config.UP:
                        Move_45D_up(multiples);
                        break;
                    case Config.LEFT:
                        Move_45D_left(multiples);
                        break;
                    case Config.RIGHT:
                        Move_45D_right(multiples);
                        break;
                    case Config.TDOWN:
                        Move_down(multiples);
                        break;
                    case Config.TUP:
                        Move_up(multiples);
                        break;
                    case Config.TLEFT:
                        Move_left(multiples);
                        break;
                    case Config.TRIGHT:
                        Move_right(multiples);
                        break;
                }
                if (!limit.Intersects(X(), Y(), width, height))
                {
                    complete = true;
                }
            }
        }

        public bool IsComplete()
        {
            return complete;
        }

        public override int GetHeight()
        {
            return width;
        }

        public override int GetWidth()
        {
            return height;
        }

        public void CreateUI(GLEx g)
        {
            if (!visible)
            {
                return;
            }
            if (!complete)
            {
                if (alpha > 0 && alpha < 1)
                {
                    g.SetAlpha(alpha);
                }
                g.DrawTexture(texture, X(), Y());
                if (alpha > 0 && alpha < 1)
                {
                    g.SetAlpha(1);
                }
            }
        }

        public LTexture GetBitmap()
        {
            return texture;
        }

        public RectBox GetCollisionBox()
        {
            return GetRect(X(), Y(), width, height);
        }

        public int GetMultiples()
        {
            return multiples;
        }

        public void SetMultiples(int multiples)
        {
            this.multiples = multiples;
        }

        public bool IsVisible()
        {
            return visible;
        }

        public void SetVisible(bool visible)
        {
            this.visible = visible;
        }

        public void Dispose()
        {
            if (texture != null)
            {
                texture.Destroy();
                texture = null;
            }
        }
    }
}
