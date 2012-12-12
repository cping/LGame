using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Core.Graphics.Opengl;
using Loon.Core;
using Loon.Utils;

namespace Loon.Action.Sprite.Effect
{
    public class SnowKernel : IKernel
    {

        private bool exist;

        private LTexture snow;

        private int id;

        private float offsetX, offsetY, speed, x, y, width, height, snowWidth,
                snowHeight;

        public SnowKernel(int n, int w, int h)
        {
            snow = XNAConfig
                    .LoadTex(LSystem.FRAMEWORK_IMG_NAME + "snow_" + n + ".png");
            snowWidth = snow.GetWidth();
            snowHeight = snow.GetHeight();
            width = w;
            height = h;
            offsetX = 0;
            offsetY = n * 0.6f + 1.9f + MathUtils.Random() * 0.2f;
            speed = MathUtils.Random();
        }

        public void SetId(int i)
        {
            this.id = i;
        }

        public virtual int Id()
        {
            return id;
        }

        public void Make()
        {
            exist = true;
            x = MathUtils.Random() * width;
            y = -snowHeight;
        }

        public virtual void Update()
        {
            if (!exist)
            {
                if (MathUtils.Random() < 0.002d)
                {
                    Make();
                }
            }
            else
            {
                x += offsetX;
                y += offsetY;
                offsetX += speed;
                speed += (LSystem.random.Next() - 0.5f) * 0.3f;
                if (offsetX >= 1.5d)
                {
                    offsetX = 1.5f;
                }
                if (offsetX <= -1.5d)
                {
                    offsetX = -1.5f;
                }
                if (speed >= 0.2d)
                {
                    speed = 0.2f;
                }
                if (speed <= -0.2d)
                {
                    speed = -0.2f;
                }
                if (y >= height)
                {
                    y = -snowHeight;
                    x = MathUtils.Random() * width;
                }
            }
        }

        public virtual void Draw(GLEx g)
        {
            if (exist)
            {
                snow.Draw(x, y);
            }
        }

        public virtual LTexture Get()
        {
            return snow;
        }

        public virtual float GetHeight()
        {
            return snowHeight;
        }

        public virtual float GetWidth()
        {
            return snowWidth;
        }

        public virtual void Dispose()
        {
            if (snow != null)
            {
                snow.Destroy();
                snow = null;
            }
        }

    }
}
