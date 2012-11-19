using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Loon.Core;
using Loon.Core.Graphics.OpenGL;
using Loon.Core.Timer;
using Loon.Action.Map;
using Loon.Core.Geom;

namespace Loon.Action.Sprite.Effect
{
    public class ScrollEffect : LObject,ISprite
    {

        private int backgroundLoop;

        private int count;

        private int width, height;

        private LTexture texture;

        private bool visible, stop;

        private LTimer timer;

        private int code;

        public ScrollEffect(String fileName)
            : this(new LTexture(fileName))
        {
            
        }

        public ScrollEffect(LTexture tex2d)
            : this(Config.DOWN, tex2d, LSystem.screenRect)
        {
            
        }

        public ScrollEffect(int d, String fileName)
            : this(d, new LTexture(fileName))
        {
            
        }

        public ScrollEffect(int d, LTexture tex2d)
            : this(d, tex2d, LSystem.screenRect)
        {
            
        }

        public ScrollEffect(int d, String fileName, RectBox limit)
            : this(d, new LTexture(fileName), limit)
        {
           
        }

        public ScrollEffect(int d, LTexture tex2d, RectBox limit)
            : this(d, tex2d, limit.x, limit.y, limit.width, limit.height)
        {
            
        }

        public ScrollEffect(int d, LTexture tex2d, float x, float y, int w, int h)
        {
            this.SetLocation(x, y);
            this.texture = tex2d;
            this.width = w;
            this.height = h;
            this.count = 1;
            this.timer = new LTimer(10);
            this.visible = true;
            this.code = d;
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
            if (stop)
            {
                return;
            }
            if (timer.Action(elapsedTime))
            {
                switch (code)
                {
                    case Config.DOWN:
                    case Config.TDOWN:
                    case Config.UP:
                    case Config.TUP:
                        this.backgroundLoop = ((backgroundLoop + count) % height);
                        break;
                    case Config.LEFT:
                    case Config.RIGHT:
                    case Config.TLEFT:
                    case Config.TRIGHT:
                        this.backgroundLoop = ((backgroundLoop + count) % width);
                        break;
                }
            }
        }

        public void CreateUI(GLEx g)
        {
            if (!visible)
            {
                return;
            }
            if (alpha > 0 && alpha < 1)
            {
                g.SetAlpha(alpha);
            }
            texture.GLBegin();
            switch (code)
            {
                case Config.DOWN:
                case Config.TDOWN:
                    for (int i = -1; i < 1; i++)
                    {
                        for (int j = 0; j < 1; j++)
                        {
                            texture.Draw(X() + (j * width), Y()
                                    + (i * height + backgroundLoop), width, height, 0,
                                    0, width, height);
                        }
                    }
                    break;
                case Config.RIGHT:
                case Config.TRIGHT:
                    for (int j = -1; j < 1; j++)
                    {
                        for (int i = 0; i < 1; i++)
                        {
                            texture.Draw(X() + (j * width + backgroundLoop), Y()
                                    + (i * height), width, height, 0, 0, width, height);
                        }
                    }
                    break;
                case Config.UP:
                case Config.TUP:
                    for (int i = -1; i < 1; i++)
                    {
                        for (int j = 0; j < 1; j++)
                        {
                            texture.Draw(X() + (j * width), Y()
                                    - (i * height + backgroundLoop), width, height, 0,
                                    0, width, height);
                        }
                    }
                    break;
                case Config.LEFT:
                case Config.TLEFT:
                    for (int j = -1; j < 1; j++)
                    {
                        for (int i = 0; i < 1; i++)
                        {
                            texture.Draw(X() - (j * width + backgroundLoop), Y()
                                    + (i * height), width, height, 0, 0, width, height);
                        }
                    }
                    break;
            }
            texture.GLEnd();
            if (alpha > 0 && alpha < 1)
            {
                g.SetAlpha(1f);
            }
        }

        public int GetCount()
        {
            return count;
        }

        public void SetCount(int count)
        {
            this.count = count;
        }

        public LTexture GetBitmap()
        {
            return texture;
        }

        public bool IsStop()
        {
            return stop;
        }

        public void SetStop(bool stop)
        {
            this.stop = stop;
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
            if (texture != null)
            {
                texture.Destroy();
                texture = null;
            }
        }
    }
}
