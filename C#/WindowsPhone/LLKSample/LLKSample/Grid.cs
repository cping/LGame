using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Action.Sprite;
using Loon.Core.Graphics.Opengl;

namespace LLKSample
{
   public  class Grid : Picture
    {
        private Animation animation, a1, a2;

        private int type, xpos, ypos;

        public Grid(LTexture img):     base(img)
        {
       
        }

        public Grid(int x, int y):       base(x, y)
        {
     
            xpos = x;
            ypos = y;

        }

        public int GetXpos()
        {
            return xpos;
        }

        public int GetYpos()
        {
            return ypos;
        }

        public bool IsPassable()
        {
            return !IsVisible();
        }

        public override void CreateUI(GLEx g)
        {
            base.CreateUI(g);
            switch (type)
            {
                case 0:
                    if (a1 == null)
                    {
                        a1 = Animation.GetDefaultAnimation("assets/s.png", 3, 48, 48,
                                100);
                    }
                    animation = a1;
                    break;
                case 2:
                    if (a2 == null)
                    {
                        a2 = Animation
                                .GetDefaultAnimation("assets/s1.png", 48, 48, 100);
                    }
                    animation = a2;
                    break;
                default:
                    break;
            }
            if (animation == null)
            {
                return;
            }
            if (type == 0 || type == 2)
            {
                LTexture img = animation.GetSpriteImage();
                if (img != null)
                {
                    g.DrawTexture(img, X() + (GetWidth() - img.GetWidth()) / 2, Y()
                            + (GetHeight() - img.GetHeight()) / 2);
                }
            }
        }

        public override void Update(long t)
        {
            base.Update(t);
            if (animation != null)
            {
                animation.Update(t);
            }
        }

        public void SetBorder(int type)
        {
            this.type = type;

        }
    }
}
