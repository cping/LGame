using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Core;
using Loon.Action.Sprite;
using Loon.Core.Graphics;
using Loon.Core.Timer;
using Loon.Core.Graphics.OpenGL;
using Loon.Utils;
using Loon.Core.Geom;

namespace Loon.Action.Sprite.Effect
{
    public class ArcEffect : LObject, ISprite
    {

        private int count;

        private int div = 10;

        private int turn = 1;

        private int[] sign = { 1, -1 };

        private int width, height;

        private LColor color;

        private bool visible, complete;

        private LTimer timer;


        public ArcEffect(LColor c)
            : this(c, 0, 0, LSystem.screenRect.width, LSystem.screenRect.height)
        {
            
        }

        public ArcEffect(LColor c, int x, int y, int width, int height)
        {
            this.SetLocation(x, y);
            this.width = width;
            this.height = height;
            this.timer = new LTimer(200);
            this.color = c == null ? LColor.black : c;
            this.visible = true;
        }

        public void SetDelay(long delay)
        {
            timer.SetDelay(delay);
        }

        public long GetDelay()
        {
            return timer.GetDelay();
        }

        public bool IsComplete()
        {
            return complete;
        }

        public LColor GetColor()
        {
            return color;
        }

        public void SetColor(LColor color)
        {
            this.color = color;
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
         
            if (complete)
            {
                return;
            }
  
            if (this.count >= this.div)
            {
                this.complete = true;
            }
            if (timer.Action(elapsedTime))
            {
                count++;
            }
        }

        public void CreateUI(GLEx g)
        {
            if (!visible)
            {
                return;
            }
            if (complete)
            {
                return;
            }
            if (alpha > 0 && alpha < 1)
            {
                g.SetAlpha(alpha);
            }
            if (count <= 1)
            {
                g.SetColor(color);
                g.FillRect(X(), Y(), width, height);
                g.ResetColor();
            }
            else
            {
                g.SetColor(color);
                int length = (int)MathUtils.Sqrt(MathUtils.Pow(width / 2, 2.0f)
                        + MathUtils.Pow(height / 2, 2.0f));
                float x = X() + (width / 2 - length);
                float y = Y() + (height / 2 - length);
                float w = width / 2 + length - x;
                float h = height / 2 + length - y;
                float deg = 360f / this.div * this.count;
                g.FillArc(x, y, w, h, 0, this.sign[this.turn] * deg);
                g.ResetColor();
            }
            if (alpha > 0 && alpha < 1)
            {
                g.SetAlpha(1f);
            }
        }

        public void Reset()
        {
            this.complete = false;
            this.count = 0;
            this.turn = 1;
        }

        public int GetTurn()
        {
            return turn;
        }

        public void SetTurn(int turn)
        {
            this.turn = turn;
        }

        public LTexture GetBitmap()
        {
            return null;
        }

        public RectBox GetCollisionBox()
        {
            return GetRect(X(), Y(), width, height);
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

        }

    }
}
