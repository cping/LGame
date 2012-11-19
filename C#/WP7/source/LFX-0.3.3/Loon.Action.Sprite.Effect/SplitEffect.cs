using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Core;
using Loon.Core.Geom;
using Loon.Core.Graphics.OpenGL;
using Loon.Core.Timer;
using Loon.Action.Map;

namespace Loon.Action.Sprite.Effect
{
    public class SplitEffect : LObject, ISprite
    {

        private Vector2f v1, v2;

        private int width, height, halfWidth, halfHeight, multiples, direction;

        private bool visible, complete, special;

        private RectBox limit;

        private LTexture texture;

        private LTimer timer;

        public SplitEffect(String fileName, int d):this(new LTexture(fileName), d)
        {
            
        }

        public SplitEffect(LTexture t, int d):this(t, LSystem.screenRect, d)
        {
            
        }

        public SplitEffect(LTexture t, RectBox limit_0, int d)
        {
            this.texture = t;
            this.width = texture.GetWidth();
            this.height = texture.GetHeight();
            this.halfWidth = width / 2;
            this.halfHeight = height / 2;
            this.multiples = 2;
            this.direction = d;
            this.limit = limit_0;
            this.timer = new LTimer(10);
            this.visible = true;
            this.v1 = new Vector2f();
            this.v2 = new Vector2f();
            switch (direction)
            {
                case Config.UP:
                case Config.DOWN:
                    special = true;
                    {
                        v1.Set(0, 0);
                        v2.Set(halfWidth, 0);
                        break;
                    }
                case Config.TLEFT:
                case Config.TRIGHT:
                    v1.Set(0, 0);
                    v2.Set(halfWidth, 0);
                    break;
                case Config.LEFT:
                case Config.RIGHT:
                    special = true;
                    {
                        v1.Set(0, 0);
                        v2.Set(0, halfHeight);
                        break;
                    }
                case Config.TUP:
                case Config.TDOWN:
                    v1.Set(0, 0);
                    v2.Set(0, halfHeight);
                    break;
            }
        }

        public void SetDelay(long delay)
        {
            timer.SetDelay(delay);
        }

        public long GetDelay()
        {
            return timer.GetDelay();
        }

        public override int GetHeight()
        {
            return height;
        }

        public override int GetWidth()
        {
            return width;
        }

        public override void Update(long elapsedTime)
        {
            if (!complete)
            {
                if (timer.Action(elapsedTime))
                {
                    switch (direction)
                    {
                        case Config.LEFT:
                        case Config.RIGHT:
                        case Config.TLEFT:
                        case Config.TRIGHT:
                            v1.Move_multiples(Config.TLEFT, multiples);
                            v2.Move_multiples(Config.TRIGHT, multiples);
                            break;
                        case Config.UP:
                        case Config.DOWN:
                        case Config.TUP:
                        case Config.TDOWN:
                            v1.Move_multiples(Config.TUP, multiples);
                            v2.Move_multiples(Config.TDOWN, multiples);
                            break;
                    }

                    if (special)
                    {
                        if (!limit.Intersects(v1.x, v1.y, halfHeight, halfWidth)
                                && !limit.Intersects(v2.x, v2.y, halfHeight,
                                        halfWidth))
                        {
                            this.complete = true;
                        }
                    }
                    else if (!limit.Intersects(v1.x, v1.y, halfWidth, halfHeight)
                          && !limit.Intersects(v2.x, v2.y, halfWidth, halfHeight))
                    {
                        this.complete = true;
                    }
                }
            }
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
                float x1 = v1.x + GetX();
                float y1 = v1.y + GetY();

                float x2 = v2.x + GetX();
                float y2 = v2.y + GetY();
                texture.GLBegin();
                switch (direction)
                {
                    case Config.LEFT:
                    case Config.RIGHT:
                    case Config.TUP:
                    case Config.TDOWN:
                        texture.Draw(x1, y1, width, halfHeight, 0, 0, width, halfHeight);
                        texture.Draw(x2, y2, width, halfHeight, 0, halfHeight, width,
                                height);
                        break;
                    case Config.UP:
                    case Config.DOWN:
                    case Config.TLEFT:
                    case Config.TRIGHT:
                        texture.Draw(x1, y1, halfWidth, height, 0, 0, halfWidth, height);
                        texture.Draw(x2, y2, halfWidth, height, halfWidth, 0, width,
                                height);
                        break;

                }
                texture.GLEnd();
                if (alpha > 0 && alpha < 1)
                {
                    g.SetAlpha(1f);
                }
            }
        }

        public bool IsComplete()
        {
            return complete;
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

        public void SetMultiples(int multiples_0)
        {
            this.multiples = multiples_0;
        }

        public bool IsVisible()
        {
            return visible;
        }

        public void SetVisible(bool visible_0)
        {
            this.visible = visible_0;
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
