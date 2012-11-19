namespace Loon.Core.Graphics.Component
{

    using Loon.Core.Graphics.OpenGL;
    using Loon.Core.Input;
    using Loon.Core.Geom;
    using Loon.Utils;
    using System.Collections.Generic;

    //0.3.3版新增类,用以进行跨平台手势操作。
    public class LGesture : LComponent
    {

        private float mX;
        private float mY;

        private float curveEndX;
        private float curveEndY;

        private bool resetGesture;

        private bool autoClear;

        private Path goalPath;

        private LColor color = LColor.orange;

        private int lineWidth;

        public LGesture(int x, int y, int w, int h, bool c)
            : base(x, y, w, h)
        {
            this.autoClear = c;
            this.lineWidth = 5;
        }

        public LGesture(int x, int y, int w, int h)
            : this(x, y, w, h, true)
        {

        }

        public LGesture(bool flag)
            : this(0, 0, LSystem.screenRect.width, LSystem.screenRect.height, flag)
        {

        }

        public LGesture()
            : this(0, 0, LSystem.screenRect.width, LSystem.screenRect.height, true)
        {

        }

        public override void CreateUI(GLEx g, int x, int y, LComponent component,
                LTexture[] buttonImage)
        {
            if (visible && goalPath != null)
            {
                g.SetLineWidth(lineWidth);
                g.GLBegin(GL.GL_LINE_STRIP);
                g.Draw(goalPath, color);
                g.GLEnd();
                g.ResetLineWidth();
                g.ResetColor();
            }
        }

        protected internal override void ProcessTouchPressed()
        {
            int x = Touch.X();
            int y = Touch.Y();
            if (GetCollisionBox().Contains(x, y))
            {
                mX = x;
                mY = y;
                if (resetGesture)
                {
                    resetGesture = false;
                    if (goalPath != null)
                    {
                        goalPath.Clear();
                    }
                }
                if (goalPath == null)
                {
                    goalPath = new Path(x, y);

                }
                else
                {
                    goalPath.Set(x, y);
                }
                curveEndX = x;
                curveEndY = y;
                DownClick();
            }
        }

        protected internal override void ProcessTouchReleased()
        {
            if (autoClear)
            {
                Clear();
            }
            UpClick();
        }

        protected internal override void ProcessTouchDragged()
        {
            if (input.IsMoving())
            {
                int x = Touch.X();
                int y = Touch.Y();
                if (GetCollisionBox().Contains(x, y))
                {
                    float previousX = mX;
                    float previousY = mY;

                    float dx = MathUtils.Abs(x - previousX);
                    float dy = MathUtils.Abs(y - previousY);

                    if (dx >= 3 || dy >= 3)
                    {
                        float cX = curveEndX = (x + previousX) / 2;
                        float cY = curveEndY = (y + previousY) / 2;
                        if (goalPath != null)
                        {
                            goalPath.QuadTo(previousX, previousY, cX, cY);
                        }
                        mX = x;
                        mY = y;
                    }
                    DragClick();
                }
            }
        }

        public float[] GetPoints()
        {
            if (goalPath != null)
            {
                return goalPath.GetPoints();
            }
            return null;
        }

        public List<Vector2f> GetList()
        {
            if (goalPath != null)
            {
                float[] points = goalPath.GetPoints();
                int size = points.Length;
                List<Vector2f> result = new List<Vector2f>(size);
                for (int i = 0; i < size; i++)
                {
                    result.Add(new Vector2f(points[i], points[i + 1]));
                }
                return result;
            }
            return null;
        }

        private static float Distance(float x1, float y1, float x2,
                float y2)
        {
            float deltaX = x1 - x2;
            float deltaY = y1 - y2;
            return MathUtils.Sqrt(deltaX * deltaX + deltaY * deltaY);
        }

        public float GetLength()
        {
            if (goalPath != null)
            {
                float length = 0;
                float[] points = goalPath.GetPoints();
                int size = points.Length;
                for (int i = 0; i < size; )
                {
                    if (i < size - 3)
                    {
                        length += Distance(points[0 + i], points[1 + i],
                                points[2 + i], points[3 + i]);
                    }
                    i += 4;
                }
                return length;
            }
            return 0;
        }

        public float[] GetCenter()
        {
            if (goalPath != null)
            {
                return goalPath.GetCenter();
            }
            return new float[] { 0, 0 };
        }

        public void DragClick()
        {
            if (Click != null)
            {
                Click.DragClick(this, input.GetTouchX(), input.GetTouchY());
            }
        }

        public void DownClick()
        {
            if (Click != null)
            {
                Click.DownClick(this, input.GetTouchX(), input.GetTouchY());
            }
        }

        public void UpClick()
        {
            if (Click != null)
            {
                Click.UpClick(this, input.GetTouchX(), input.GetTouchY());
            }
        }

        public void Clear()
        {
            if (goalPath != null)
            {
                goalPath.Clear();
            }
        }

        public float GetCurveEndX()
        {
            return curveEndX;
        }

        public void SetCurveEndX(float curveEndX)
        {
            this.curveEndX = curveEndX;
        }

        public float GetCurveEndY()
        {
            return curveEndY;
        }

        public void SetCurveEndY(float curveEndY)
        {
            this.curveEndY = curveEndY;
        }

        public Path GetPath()
        {
            return goalPath;
        }

        public LColor GetColor()
        {
            return color;
        }

        public void SetColor(LColor color)
        {
            this.color = color;
        }

        public int GetLineWidth()
        {
            return lineWidth;
        }

        public void SetLineWidth(int lineWidth)
        {
            this.lineWidth = lineWidth;
        }

        public bool IsAutoClear()
        {
            return autoClear;
        }

        public void SetAutoClear(bool autoClear)
        {
            this.autoClear = autoClear;
        }

        public override string GetUIName()
        {
            return "Gesture";
        }

    }
}
