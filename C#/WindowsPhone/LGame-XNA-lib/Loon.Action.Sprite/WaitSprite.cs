namespace Loon.Action.Sprite
{
    using Loon.Core;
    using Loon.Core.Graphics;
    using Loon.Java.Collections;
    using Loon.Core.Geom;
    using Loon.Utils;
    using Microsoft.Xna.Framework;
    using Loon.Core.Timer;
    using Loon.Core.Graphics.Opengl;
    using System.Collections.Generic;

    public class WaitSprite : LObject, ISprite
    {

        private sealed class DrawWait
        {

            private readonly float sx = 1.0f, sy = 1.0f;

            private readonly int ANGLE_STEP = 15;

            private readonly int ARCRADIUS = 120;

            private LColor color;

            private double r;

            private List<object> list;

            internal int width, height;

            private int angle;

            private int style;

            private int paintX, paintY, paintWidth, paintHeight;

            private LColor Fill;

            public DrawWait(int s, int width, int height)
            {
                this.style = s;
                this.width = width;
                this.height = height;
                this.color = new LColor(1f, 1f, 1f);
                switch (style)
                {
                    case 0:
                        int r1 = width / 8,
                        r2 = height / 8;
                        this.r = ((r1 < r2) ? r1 : r2) / 2;
                        this.list = new List<object>(Arrays.AsList<object>(new object[] {
											new RectBox(sx + 3 * r, sy + 0 * r, 2 * r, 2 * r),
											new RectBox(sx + 5 * r, sy + 1 * r, 2 * r, 2 * r),
											new RectBox(sx + 6 * r, sy + 3 * r, 2 * r, 2 * r),
											new RectBox(sx + 5 * r, sy + 5 * r, 2 * r, 2 * r),
											new RectBox(sx + 3 * r, sy + 6 * r, 2 * r, 2 * r),
											new RectBox(sx + 1 * r, sy + 5 * r, 2 * r, 2 * r),
											new RectBox(sx + 0 * r, sy + 3 * r, 2 * r, 2 * r),
											new RectBox(sx + 1 * r, sy + 1 * r, 2 * r, 2 * r) }));
                        break;
                    case 1:
                        this.Fill = new LColor(165, 0, 0, 255);
                        this.paintX = (width - ARCRADIUS);
                        this.paintY = (height - ARCRADIUS);
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
                LColor oldColor = g.GetColor();
                g.SetColor(color);
                switch (style)
                {
                    case 0:
                        float alpha = 0.0f;
                        int nx = x + width / 2 - (int)r * 4,
                        ny = y + height / 2 - (int)r * 4;
                        g.Translate(nx, ny);
                        for (IIterator it = new IteratorAdapter(list.GetEnumerator()); it.HasNext(); )
                        {
                            RectBox s = (RectBox)it.Next();
                            alpha = alpha + 0.1f;
                            g.SetAlpha(alpha);
                            g.FillOval(s.x, s.y, s.width, s.height);
                        }
                        g.SetAlpha(1.0F);
                        g.Translate(-nx, -ny);
                        break;
                    case 1:
                        g.SetLineWidth(10);
                        g.Translate(x, y);
                        g.SetColor(Fill);
                        g.DrawOval(0, 0, width, height);
                        int sa = angle % 360;
                        g.FillArc(x + (width - paintWidth) / 2, y
                                + (height - paintHeight) / 2, paintWidth, paintHeight,
                                sa, sa + ANGLE_STEP);
                        g.Translate(-x, -y);
                        g.ResetLineWidth();
                        break;
                }
                g.SetColor(oldColor);
            }

        }

        private LTimer delay;

        private bool visible;

        private DrawWait wait;

        private int style;

        private Cycle cycle;

        public WaitSprite(int s):this(s, LSystem.screenRect.width, LSystem.screenRect.height)
        {
            
        }

        public WaitSprite(int s, int w, int h)
        {
            this.style = s;
            this.wait = new DrawWait(s, w, h);
            this.delay = new LTimer(120);
            this.alpha = 1.0F;
            this.visible = true;
            if (s > 1)
            {
                int width = w / 2;
                int height = h / 2;
                cycle = NewSample(s - 2, width, height);
                RectBox limit = cycle.GetCollisionBox();
                SetLocation(
                        (w - ((limit.GetWidth() == 0) ? 20 : limit.GetWidth())) / 2,
                        (h - ((limit.GetHeight() == 0) ? 20 : limit.GetHeight())) / 2);
            }
            Update(0);
        }

        private static Cycle NewSample(int type, float srcWidth,
                float srcHeight)
        {
            float width = 1;
            float height = 1;
            float offset = 0;
            int padding = 0;
            switch (type)
            {
                case 0:
                    offset = 12;
                    if (srcWidth < srcHeight)
                    {
                        width = 60;
                        height = 60;
                        padding = -35;
                    }
                    else
                    {
                        width = 100;
                        height = 100;
                        padding = -35;
                    }
                    break;
                case 1:
                    width = 100;
                    height = 40;
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
                    width = 30;
                    height = 30;
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
                    width = 100;
                    height = 100;
                    padding = -30;
                    break;
                case 4:
                    width = 80;
                    height = 80;
                    offset = 14;
                    padding = -15;
                    break;
                case 5:
                    width = 100;
                    height = 100;
                    if (srcWidth < srcHeight)
                    {
                        offset = -4;
                    }
                    break;
                case 6:
                    width = 60;
                    height = 60;
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
                    width = 60;
                    height = 60;
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
                    width = 60;
                    height = 60;
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
                    width = 80;
                    height = 80;
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
            return Cycle.GetSample(type, srcWidth, srcHeight, width, height,
                    offset, padding);
        }

        public void CreateUI(GLEx g)
        {
            if (!visible)
            {
                return;
            }
            if (style < 2)
            {
                if (alpha > 0.1f && alpha < 1.0f)
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

        public override void SetAlpha(float alpha)
        {
            if (cycle != null)
            {
                cycle.SetAlpha(alpha);
            }
            else
            {
                this.alpha = alpha;
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

        public RectBox GetCollisionBox()
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

        public bool IsVisible()
        {
            return (cycle != null) ? cycle.IsVisible() : visible;
        }

        public void SetVisible(bool visible)
        {
            if (cycle != null)
            {
                cycle.SetVisible(visible);
            }
            else
            {
                this.visible = visible;
            }
        }

        public LTexture GetBitmap()
        {
            return null;
        }

        public void Dispose()
        {

        }

    }
}
