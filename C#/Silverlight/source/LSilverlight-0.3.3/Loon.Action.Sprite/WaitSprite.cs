namespace Loon.Action.Sprite
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using Loon.Core;
    using Loon.Core.Graphics;
    using Loon.Java.Collections;
    using Loon.Core.Geom;
    using Loon.Utils;
    using Loon.Core.Graphics.OpenGL;
    using Microsoft.Xna.Framework;
    using Loon.Core.Timer;

    public class WaitSprite : LObject, ISprite
    {

        private sealed class DrawWait
        {

            private readonly float sx, sy;

            private readonly int ANGLE_STEP;

            private readonly int ARCRADIUS;

            private LColor color;

            private double r;

            private List<RectBox> list;

            internal int width, height;

            private int angle;

            private int style;

            private int paintX, paintY, paintWidth, paintHeight;

            private LColor fill;

            public DrawWait(int s, int width_0, int height_1)
            {
                this.sx = 1.0f;
                this.sy = 1.0f;
                this.ANGLE_STEP = 15;
                this.ARCRADIUS = 120;
                this.style = s;
                this.width = width_0;
                this.height = height_1;
                this.color = new LColor(LColor.white);
                switch (style)
                {
                    case 0:
                        int r1 = width_0 / 8,
                        r2 = height_1 / 8;
                        this.r = ((r1 < r2) ? r1 : r2) / 2;
                        this.list = new List<RectBox>(new RectBox[] {
											new RectBox(sx + 3 * r, sy + 0 * r, 2 * r, 2 * r),
											new RectBox(sx + 5 * r, sy + 1 * r, 2 * r, 2 * r),
											new RectBox(sx + 6 * r, sy + 3 * r, 2 * r, 2 * r),
											new RectBox(sx + 5 * r, sy + 5 * r, 2 * r, 2 * r),
											new RectBox(sx + 3 * r, sy + 6 * r, 2 * r, 2 * r),
											new RectBox(sx + 1 * r, sy + 5 * r, 2 * r, 2 * r),
											new RectBox(sx + 0 * r, sy + 3 * r, 2 * r, 2 * r),
											new RectBox(sx + 1 * r, sy + 1 * r, 2 * r, 2 * r) });
                        break;
                    case 1:
                        this.fill = new LColor(165, 0, 0, 255);
                        this.paintX = (width_0 - ARCRADIUS);
                        this.paintY = (height_1 - ARCRADIUS);
                        this.paintWidth = paintX + ARCRADIUS;
                        this.paintHeight = paintY + ARCRADIUS;
                        break;
                }
            }

            public void Next()
            {
                switch (style)
                {
                    case 0:
                        CollectionUtils.Add(list, CollectionUtils.RemoveAt(list, 0));
                        break;
                    case 1:
                        angle += ANGLE_STEP;
                        break;
                }
            }

            public void Draw(GLEx g, int x, int y)
            {
                switch (style)
                {
                    case 0:
                    Color oldColor = g.GetColor();
					g.SetColor(color);
					float alpha = 0.0f;
					int nx = x + width / 2 - (int) r * 4,
					ny = y + height / 2 - (int) r * 4;
					g.Translate(nx, ny);
					for (IEnumerator<RectBox> it = list.GetEnumerator(); it.MoveNext();) {
                        RectBox s = it.Current;
						alpha = alpha + 0.1f;
						g.SetAlpha(alpha);
						g.FillOval(s.x, s.y, s.width, s.height);
					}
					g.SetAlpha(1.0F);
					g.Translate(-nx, -ny);
					g.SetColor(oldColor);
                        break;
                    case 1:
                        g.SetLineWidth(10);
                        g.Translate(x, y);
                        g.FillOval(0, 0, width, height, fill.Color);
                        int sa = angle % 360;
                        g.FillArc(x + (width - paintWidth) / 2, y
                                + (height - paintHeight) / 2, paintWidth, paintHeight,
                                sa, sa + ANGLE_STEP,Color.Red);
                        g.Translate(-x, -y);
                        g.ResetLineWidth();
                        break;
                }
            }

        }

        private LTimer delay;

        private bool visible;

        private WaitSprite.DrawWait wait;

        private int style;

        private Cycle cycle;

        public WaitSprite(int s):this(s, (int)LSystem.screenRect.width, (int)LSystem.screenRect.height)
        {
            
        }

        public WaitSprite(int s, int w, int h)
        {
            this.style = s;
            this.wait = new WaitSprite.DrawWait(s, w, h);
            this.delay = new LTimer(120);
            this.alpha = 1.0F;
            this.visible = true;
            if (s > 1)
            {
                int width_0 = w / 2;
                int height_1 = h / 2;
                cycle = NewSample(s - 2, width_0, height_1);
                RectBox limit = cycle.GetCollisionBox();
                SetLocation(
                        (w - ((limit.GetWidth() == 0) ? (float)(20) : (float)(limit.GetWidth()))) / 2,
                        (h - ((limit.GetHeight() == 0) ? (float)(20) : (float)(limit.GetHeight()))) / 2);
            }
            Update(0);
        }

        private static Cycle NewSample(int type, float srcWidth,
                float srcHeight)
        {
            float width_0 = 1;
            float height_1 = 1;
            float offset = 0;
            int padding = 0;
            switch (type)
            {
                case 0:
                    offset = 12;
                    if (srcWidth < srcHeight)
                    {
                        width_0 = 60;
                        height_1 = 60;
                        padding = -35;
                    }
                    else
                    {
                        width_0 = 100;
                        height_1 = 100;
                        padding = -35;
                    }
                    break;
                case 1:
                    width_0 = 100;
                    height_1 = 40;
                    if (srcWidth < srcHeight)
                    {
                        offset = 0;
                    }
                    else
                    {
                        offset = 8;
                    }
                    break;
                case 2:
                    width_0 = 30;
                    height_1 = 30;
                    if (srcWidth < srcHeight)
                    {
                        offset = 0;
                    }
                    else
                    {
                        offset = 6;
                    }
                    break;
                case 3:
                    width_0 = 100;
                    height_1 = 100;
                    padding = -30;
                    break;
                case 4:
                    width_0 = 80;
                    height_1 = 80;
                    offset = 14;
                    padding = -15;
                    break;
                case 5:
                    width_0 = 100;
                    height_1 = 100;
                    if (srcWidth < srcHeight)
                    {
                        offset = -4;
                    }
                    break;
                case 6:
                    width_0 = 60;
                    height_1 = 60;
                    offset = 12;
                    if (srcWidth < srcHeight)
                    {
                        padding = -60;
                    }
                    else
                    {
                        padding = -80;
                    }
                    break;
                case 7:
                    width_0 = 60;
                    height_1 = 60;
                    offset = 12;
                    if (srcWidth < srcHeight)
                    {
                        padding = -80;
                    }
                    else
                    {
                        padding = -120;
                    }
                    break;
                case 8:
                    width_0 = 60;
                    height_1 = 60;
                    offset = 12;
                    if (srcWidth < srcHeight)
                    {
                        padding = -60;
                    }
                    else
                    {
                        padding = -80;
                    }
                    break;
                case 9:
                    width_0 = 80;
                    height_1 = 80;
                    if (srcWidth < srcHeight)
                    {
                        offset = -2;
                        padding = -20;
                    }
                    else
                    {
                        padding = -30;
                    }
                    break;
            }
            return Cycle.GetSample(type, srcWidth, srcHeight, width_0, height_1,
                    offset, padding);
        }

        public virtual void CreateUI(GLEx g)
        {
            if (!visible)
            {
                return;
            }
            if (style < 2)
            {
                if (alpha > 0.1d && alpha < 1.0d)
                {
                    g.SetAlpha(alpha);
        
                    wait.Draw(g, X(), Y());
                    g.SetAlpha(1.0F);
                }
                else
                {
                    wait.Draw(g, X(), Y());
                }
            }
            else
            {
                if (cycle != null)
                {
                    cycle.CreateUI(g);
                }
            }
        }

        public override int GetHeight()
        {
            if (cycle != null)
            {
                return cycle.GetCollisionBox().height;
            }
            else
            {
                return wait.height;
            }
        }

        public override int GetWidth()
        {
            if (cycle != null)
            {
                return cycle.GetCollisionBox().width;
            }
            else
            {
                return wait.width;
            }
        }

        public override void Update(long elapsedTime)
        {
            if (!visible)
            {
                return;
            }
            if (cycle != null)
            {
                if (cycle.X() != X() || cycle.Y() != Y())
                {
                    cycle.SetLocation(X(), Y());
                }
                cycle.Update(elapsedTime);
            }
            else
            {
                if (delay.Action(elapsedTime))
                {
                    wait.Next();
                }
            }
        }

        public override void SetAlpha(float a)
        {
            if (cycle != null)
            {
                cycle.SetAlpha(a);
            }
            else
            {
                this.alpha = a;
            }
        }

        public override float GetAlpha()
        {
            if (cycle != null)
            {
                return cycle.GetAlpha();
            }
            else
            {
                return alpha;
            }
        }

        public virtual RectBox GetCollisionBox()
        {
            if (cycle != null)
            {
                return cycle.GetCollisionBox();
            }
            else
            {
                return GetRect(X(), Y(), GetWidth(), GetHeight());
            }
        }

        public virtual bool IsVisible()
        {
            return (cycle != null) ? cycle.IsVisible() : visible;
        }

        public virtual void SetVisible(bool visible_0)
        {
            if (cycle != null)
            {
                cycle.SetVisible(visible_0);
            }
            else
            {
                this.visible = visible_0;
            }
        }

        public virtual LTexture GetBitmap()
        {
            return null;
        }

        public virtual void Dispose()
        {

        }

    }
}
