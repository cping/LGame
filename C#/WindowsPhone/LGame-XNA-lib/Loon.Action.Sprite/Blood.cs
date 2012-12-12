using Loon.Core;
using Loon.Core.Timer;
using Loon.Core.Graphics;
using Loon.Utils;
using Loon.Core.Graphics.Opengl;
using Loon.Core.Geom;
namespace Loon.Action.Sprite
{
    public class Blood : LObject, ISprite
    {

        internal class Drop
        {
            public float x, y, xspeed, yspeed;
        }

        private float xSpeed, ySpeed;

        private LTimer timer;

        private int step, limit;

        private Drop[] drops;

        private bool visible;

        private LColor color;

        public Blood(int x, int y): this(LColor.red, x, y)
        {
           
        }

        public Blood(LColor c, int x, int y)
        {
            this.SetLocation(x, y);
            this.color = c;
            this.timer = new LTimer(20);
            this.drops = new Drop[20];
            this.limit = 50;
            for (int i = 0; i < drops.Length; ++i)
            {
                SetBoolds(i, x, y, 6.0f * (MathUtils.Random() - 0.5f), -2.0f
                        * MathUtils.Random());
            }
            this.xSpeed = 0F;
            this.ySpeed = 0.5F;
            this.step = 0;
            this.visible = true;
        }

        public virtual void SetBoolds(int index, float x, float y, float xs, float ys)
        {
            if (index > drops.Length - 1)
            {
                return;
            }
            drops[index] = new Drop();
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

        public virtual void SetDelay(long delay)
        {
            timer.SetDelay(delay);
        }

        public virtual long GetDelay()
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
            g.SetColor(color);
            for (int i = 0; i < drops.Length; ++i)
            {
                g.FillOval((int)drops[i].x, (int)drops[i].y, 2, 2);
            }
            g.ResetColor();
            if (alpha > 0 && alpha < 1)
            {
                g.SetAlpha(1);
            }
        }

        public virtual LColor GetColor()
        {
            return color;
        }

        public virtual void SetColor(LColor color)
        {
            this.color = color;
        }

        public virtual int GetStep()
        {
            return step;
        }

        public virtual void SetStep(int step)
        {
            this.step = step;
        }

        public virtual int GetLimit()
        {
            return limit;
        }

        public virtual void SetLimit(int limit)
        {
            this.limit = limit;
        }

        public virtual LTexture GetBitmap()
        {
            return null;
        }

        public virtual RectBox GetCollisionBox()
        {
            return null;
        }

        public virtual float GetXSpeed()
        {
            return xSpeed;
        }

        public virtual void SetXSpeed(float speed)
        {
            this.xSpeed = speed;
        }

        public virtual float GetYSpeed()
        {
            return ySpeed;
        }

        public virtual void SetYSpeed(float speed)
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

        public virtual void SetVisible(bool visible)
        {
            this.visible = visible;
        }

        public void Dispose()
        {

        }

    }
}
