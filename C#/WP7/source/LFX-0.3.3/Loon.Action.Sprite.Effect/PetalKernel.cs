using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Core.Graphics.OpenGL;
using Loon.Core;
using Loon.Utils;

namespace Loon.Action.Sprite.Effect
{
    public class PetalKernel : IKernel
    {

        private bool exist;

        private LTexture sakura;

        private float offsetX, offsetY, speed, x, y, width, height, sakuraWidth,
                sakuraHeight;

        private int id;

        public PetalKernel(int n, int w, int h)
        {
            id = n;
            sakura = XNAConfig.LoadTex(LSystem.FRAMEWORK_IMG_NAME + "sakura_"
                            + n + ".png");
            sakuraWidth = sakura.GetWidth();
            sakuraHeight = sakura.GetHeight();
            width = w;
            height = h;
            offsetX = 0;
            offsetY = n * 0.6f + 1.9f + MathUtils.Random() * 0.2f;
            speed = MathUtils.Random();
        }

        public virtual int Id()
        {
            return id;
        }

        public void Make()
        {
            exist = true;
            x = MathUtils.Random() * width;
            y = -sakuraHeight;
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
                speed += (MathUtils.Random() - 0.5f) * 0.3f;
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
                    y = -(float)(LSystem.random.NextDouble() * 1) - sakuraHeight;
                    x = (float)(LSystem.random.NextDouble() * (width - 1));
                }
            }
        }

        public virtual void Draw(GLEx g)
        {
            if (exist)
            {
                sakura.Draw(x, y);
            }
        }

        public virtual LTexture Get()
        {
            return sakura;
        }

        public virtual float GetHeight()
        {
            return sakuraHeight;
        }

        public virtual float GetWidth()
        {
            return sakuraWidth;
        }

        public virtual void Dispose()
        {
            if (sakura != null)
            {
                sakura.Destroy();
                sakura = null;
            }
        }
    }
}
