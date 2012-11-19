using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Core.Graphics.OpenGL;
using Loon.Core;
using Loon.Utils;

namespace Loon.Action.Sprite.Effect
{
    public class RainKernel : IKernel
    {

        private bool exist;

        private LTexture rain;

        private int id;

        private float offsetX, offsetY, x, y, width, height, rainWidth, rainHeight;

        public RainKernel(int n, int w, int h)
        {
            id = n;
            rain = XNAConfig.LoadTex(LSystem.FRAMEWORK_IMG_NAME + "rain_" + n + ".png");
            rainWidth = rain.GetWidth();
            rainHeight = rain.GetHeight();
            width = w;
            height = h;
            offsetX = 0;
            offsetY = (5 - n) * 30 + 75 + MathUtils.Random() * 15;
        }

        public virtual int Id()
        {
            return id;
        }

        public void Make()
        {
            exist = true;
            x = MathUtils.Random() * width;
            y = -rainHeight;
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
                if (y >= height)
                {
                    x = MathUtils.Random() * width;
                    y = -rainHeight * MathUtils.Random();
                }
            }
        }

        public virtual void Draw(GLEx g)
        {
            if (exist)
            {
                rain.Draw(x, y);
            }
        }

        public virtual LTexture Get()
        {
            return rain;
        }

        public virtual float GetHeight()
        {
            return rainHeight;
        }

        public virtual float GetWidth()
        {
            return rainWidth;
        }

        public virtual void Dispose()
        {
            if (rain != null)
            {
                rain.Destroy();
                rain = null;
            }
        }

    }
}
