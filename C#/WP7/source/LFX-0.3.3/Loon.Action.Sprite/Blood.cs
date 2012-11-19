namespace Loon.Action.Sprite
{

    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using Loon.Core;
    using Loon.Core.Timer;
    using Loon.Core.Graphics;
    using Loon.Core.Graphics.OpenGL;
    using Loon.Core.Geom;
    using Loon.Utils;


    public class Blood : LObject, ISprite
    {

        private const long serialVersionUID = 1L;

        internal class Drop
        {
            public float x, y, xspeed, yspeed;
        }

        private float xSpeed, ySpeed;

        private LTimer timer;

        private int step, limit;

        private Blood.Drop[] drops;

        private bool visible;

        private LColor color;

        public Blood(int x, int y):this(LColor.red, x, y)
        {
            
        }

        public Blood(LColor c, int x, int y)
        {
            this.alpha = 1;
            this.SetLocation(x, y);
            this.color = c;
            this.timer = new LTimer(20);
            this.drops = new Blood.Drop[20];
            this.limit = 50;
            for (int i = 0; i < drops.Length; ++i)
            {
                SetBoolds(i, x, y, 6.0f * ((float)MathUtils.Random() - 0.5f), -2.0f
                        * (float)MathUtils.Random());
            }
            this.xSpeed = 0F;
            this.ySpeed = 0.5F;
            this.step = 0;
            this.visible = true;
        }

        public void SetBoolds(int index, float x, float y, float xs, float ys)
        {
            if (index > drops.Length - 1)
            {
                return;
            }
            drops[index] = new Blood.Drop();
            drops[index].x = x;
            drops[index].y = y;
            drops[index].xspeed = xs;
            drops[index].yspeed = ys;
        }

        public override void Update(long elapsedTime)
        {
            if (timer.Action(elapsedTime))
            {
                for (int i = 0; i < drops.Length; ++i)
                {
                    drops[i].xspeed += xSpeed;
                    drops[i].yspeed += ySpeed;
                    drops[i].x -= drops[i].xspeed;
                    drops[i].y += drops[i].yspeed;
                }
                step++;
                if (step > limit)
                {
                    this.visible = false;
                }
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

        public virtual void CreateUI(GLEx g)
        {
            if (!visible)
            {
                return;
            }
            if (alpha > 0 && alpha < 1)
            {
                g.SetAlpha(alpha);
            }
            for (int i = 0; i < drops.Length; ++i)
            {
                g.FillOval((int)drops[i].x, (int)drops[i].y, 2, 2, color.Color);
            }
            if (alpha > 0 && alpha < 1)
            {
                g.SetAlpha(1);
            }
        }

        public LColor GetColor()
        {
            return color;
        }

        public void SetColor(LColor color_0)
        {
            this.color = color_0;
        }

        public int GetStep()
        {
            return step;
        }

        public void SetStep(int step_0)
        {
            this.step = step_0;
        }

        public int GetLimit()
        {
            return limit;
        }

        public void SetLimit(int l)
        {
            this.limit = l;
        }

        public virtual LTexture GetBitmap()
        {
            return null;
        }

        public virtual RectBox GetCollisionBox()
        {
            return null;
        }

        public float GetXSpeed()
        {
            return xSpeed;
        }

        public void SetXSpeed(float speed)
        {
            this.xSpeed = speed;
        }

        public float GetYSpeed()
        {
            return ySpeed;
        }

        public void SetYSpeed(float speed)
        {
            this.ySpeed = speed;
        }

        public override int GetHeight()
        {
            return 0;
        }

        public override int GetWidth()
        {
            return 0;
        }

        public virtual bool IsVisible()
        {
            return visible;
        }

        public virtual void SetVisible(bool visible_0)
        {
            this.visible = visible_0;
        }

        public virtual void Dispose()
        {

        }

    }
}
